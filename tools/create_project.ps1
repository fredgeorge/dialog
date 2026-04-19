<#
.SYNOPSIS
    Creates a new Kotlin project from kotlin_gradle_template.

.DESCRIPTION
    Copies the template, initialises git, renames packages/modules, restructures
    source directories, updates package/import declarations, verifies no stray
    'template' references remain, and runs the gradle build.

.PARAMETER ProjectName
    The name of the new project. Must be a valid Kotlin package segment
    (letters, digits, underscores; may not start with a digit).

.EXAMPLE
    ./create_project.ps1 context
#>

[CmdletBinding()]
param(
    [Parameter(Mandatory = $true, Position = 0)]
    [string] $ProjectName
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

# ── 0. Validate argument ──────────────────────────────────────────────────────
if ($ProjectName -notmatch '^[A-Za-z_][A-Za-z0-9_]*$') {
    Write-Error "'$ProjectName' is not a valid project name. Use letters, digits, and underscores only; must not start with a digit."
    exit 1
}

$KotlinSrc = Join-Path $HOME 'src/kotlin'
$Template  = Join-Path $KotlinSrc 'kotlin_gradle_template'
$Dest      = Join-Path $KotlinSrc $ProjectName

# ── Helper: recursive copy with excluded names/patterns ──────────────────────
function Copy-TemplateTree {
    param(
        [string]   $Source,
        [string]   $Destination,
        [string[]] $ExcludeNames     = @(),   # exact directory/file names
        [string[]] $ExcludePatterns  = @()    # wildcard patterns on file names
    )

    if (-not (Test-Path -LiteralPath $Destination)) {
        New-Item -ItemType Directory -Path $Destination -Force | Out-Null
    }

    Get-ChildItem -LiteralPath $Source -Force | ForEach-Object {
        if ($ExcludeNames -contains $_.Name) { return }

        $target = Join-Path $Destination $_.Name

        if ($_.PSIsContainer) {
            Copy-TemplateTree -Source $_.FullName -Destination $target `
                              -ExcludeNames $ExcludeNames `
                              -ExcludePatterns $ExcludePatterns
        } else {
            foreach ($pat in $ExcludePatterns) {
                if ($_.Name -like $pat) { return }
            }
            Copy-Item -LiteralPath $_.FullName -Destination $target -Force
        }
    }
}

# ── Helper: in-place regex replace on a file (UTF-8, no BOM) ─────────────────
function Edit-FileContent {
    param(
        [string] $Path,
        [string] $Pattern,
        [string] $Replacement
    )
    if (-not (Test-Path -LiteralPath $Path)) { return }
    $content = Get-Content -LiteralPath $Path -Raw
    $updated = [regex]::Replace($content, $Pattern, $Replacement)
    # Preserve original line endings as best as possible by using Set-Content -NoNewline
    Set-Content -LiteralPath $Path -Value $updated -NoNewline -Encoding utf8
}

# ── 1. Copy template ──────────────────────────────────────────────────────────
Write-Host "==> Copying template to $Dest ..."

if (Test-Path -LiteralPath $Dest) {
    Write-Error "$Dest already exists. Aborting."
    exit 1
}

Copy-TemplateTree -Source $Template -Destination $Dest `
                  -ExcludeNames   @('.git', '.gradle', 'build') `
                  -ExcludePatterns @('*.jar')

# gradle-wrapper.jar is excluded by .gitignore (*.jar) but required for ./gradlew
$wrapperSrc = Join-Path $Template 'gradle/wrapper/gradle-wrapper.jar'
$wrapperDst = Join-Path $Dest     'gradle/wrapper/gradle-wrapper.jar'
New-Item -ItemType Directory -Path (Split-Path $wrapperDst) -Force | Out-Null
Copy-Item -LiteralPath $wrapperSrc -Destination $wrapperDst -Force

Write-Host "    Done."

# ── 2. Git init ───────────────────────────────────────────────────────────────
Write-Host "==> Initialising git repository ..."
Push-Location $Dest
try {
    git init
    git remote add origin "https://github.com/fredgeorge/$ProjectName.git"
}
finally {
    Pop-Location
}
Write-Host "    Done."

# ── 3. String substitutions ───────────────────────────────────────────────────
Write-Host "==> Substituting project-specific strings ..."

Edit-FileContent -Path (Join-Path $Dest 'settings.gradle.kts') `
                 -Pattern '"kotlin-gradle-template"' `
                 -Replacement ('"' + $ProjectName + '"')

Edit-FileContent -Path (Join-Path $Dest 'build.gradle.kts') `
                 -Pattern 'com\.nrkei\.project\.template' `
                 -Replacement "com.nrkei.project.$ProjectName"

Edit-FileContent -Path (Join-Path $Dest 'engine/build.gradle.kts') `
                 -Pattern '"template-engine"' `
                 -Replacement ('"' + $ProjectName + '-engine"')

Edit-FileContent -Path (Join-Path $Dest 'persistence/build.gradle.kts') `
                 -Pattern '"template-persistence"' `
                 -Replacement ('"' + $ProjectName + '-persistence"')

Edit-FileContent -Path (Join-Path $Dest 'README.md') `
                 -Pattern '# kotlin_gradle_template' `
                 -Replacement "# $ProjectName"

Write-Host "    Done."

# ── 4. Restructure package directories ────────────────────────────────────────
Write-Host "==> Restructuring package directories ..."

$SourceRoots = @(
    'engine/src/main/kotlin',
    'persistence/src/main/kotlin',
    'persistence/src/test/kotlin',
    'tests/src/test/kotlin'
)

foreach ($root in $SourceRoots) {
    $oldDir = Join-Path $Dest "$root/com/nrkei/project/template"
    $newDir = Join-Path $Dest "$root/com/nrkei/project/$ProjectName"

    if (Test-Path -LiteralPath $oldDir -PathType Container) {
        New-Item -ItemType Directory -Path $newDir -Force | Out-Null

        # Move unit subdirectory first if it exists
        $oldUnit = Join-Path $oldDir 'unit'
        if (Test-Path -LiteralPath $oldUnit -PathType Container) {
            $newUnit = Join-Path $newDir 'unit'
            New-Item -ItemType Directory -Path $newUnit -Force | Out-Null
            Get-ChildItem -LiteralPath $oldUnit -Filter '*.kt' -File |
                ForEach-Object { Move-Item -LiteralPath $_.FullName -Destination $newUnit -Force }
        }

        # Move top-level .kt files
        Get-ChildItem -LiteralPath $oldDir -Filter '*.kt' -File |
            ForEach-Object { Move-Item -LiteralPath $_.FullName -Destination $newDir -Force }
    }
}

Write-Host "    Done."

# ── 5. Update package/import declarations ─────────────────────────────────────
Write-Host "==> Updating package and import declarations in .kt files ..."

Get-ChildItem -LiteralPath $Dest -Recurse -Filter '*.kt' -File | ForEach-Object {
    Edit-FileContent -Path $_.FullName `
                     -Pattern 'com\.nrkei\.project\.template' `
                     -Replacement "com.nrkei.project.$ProjectName"
}

Write-Host "    Done."

# ── 6. Remove empty template directories ──────────────────────────────────────
Write-Host "==> Removing empty template package directories ..."

foreach ($root in $SourceRoots) {
    $oldUnit = Join-Path $Dest "$root/com/nrkei/project/template/unit"
    $oldDir  = Join-Path $Dest "$root/com/nrkei/project/template"

    foreach ($p in @($oldUnit, $oldDir)) {
        if (Test-Path -LiteralPath $p -PathType Container) {
            if (-not (Get-ChildItem -LiteralPath $p -Force)) {
                Remove-Item -LiteralPath $p -Force
            }
        }
    }
}

Write-Host "    Done."

# ── 7. Verify ─────────────────────────────────────────────────────────────────
Write-Host "==> Verifying no 'template' references remain ..."

$extensions     = '*.kt', '*.kts', '*.xml', '*.md'
$excludedNames  = 'ai_template.md', 'CLAUDE.md'

$matchingFiles = Get-ChildItem -LiteralPath $Dest -Recurse -File -Include $extensions |
    Where-Object { $excludedNames -notcontains $_.Name } |
    Where-Object { Select-String -LiteralPath $_.FullName -Pattern 'template' -SimpleMatch -Quiet }

if ($matchingFiles) {
    Write-Warning "'template' still found in:"
    $matchingFiles | ForEach-Object { Write-Host $_.FullName }
}
else {
    Write-Host "    Clean — no stray 'template' references found."
}

Write-Host "==> Running gradlew clean build test ..."
Push-Location $Dest
try {
    if ($IsWindows) {
        & .\gradlew.bat clean build test
    } else {
        & ./gradlew clean build test
    }
    if ($LASTEXITCODE -ne 0) {
        throw "gradle build failed with exit code $LASTEXITCODE"
    }
}
finally {
    Pop-Location
}

Write-Host "    Build and tests passed."

# ── 8. GitHub + initial commit ────────────────────────────────────────────────
Write-Host ""
Write-Host "==> Next steps (run manually after creating the GitHub repo):"
Write-Host ""
Write-Host "    gh repo create fredgeorge/$ProjectName --private"
Write-Host ""
Write-Host "    cd $Dest"
Write-Host "    git add ."
Write-Host "    git commit -m 'Fred: Initial project from kotlin_gradle_template'"
Write-Host "    git branch -M main"
Write-Host "    git push -u origin main"
Write-Host ""
Write-Host "All done!"
