# Instructions for Creating a New Project from This Template

## Setup

Given a new `<project_name>`, create `~/src/kotlin/<project_name>` as follows:

1. Copy (not clone) `~/src/kotlin/kotlin_gradle_template` to `~/src/kotlin/<project_name>`,
   excluding `.git`, `.gradle`, and `build` directories. Ignore files per .gitignore.
   Then separately copy `gradle/wrapper/gradle-wrapper.jar` — it is excluded by `.gitignore`
   (`*.jar`) but is required for `./gradlew` to run.
2. In the new directory, run `git init` and add a remote:
   `git remote add origin https://github.com/fredgeorge/<project_name>.git`

## String Substitutions

Make the following exact replacements throughout the project:

| File | Old string | New string |
|------|-----------|------------|
| `settings.gradle.kts` | `"kotlin-gradle-template"` | `"<project_name>"` |
| `build.gradle.kts` | `"com.nrkei.project.template"` | `"com.nrkei.project.<project_name>"` |
| `engine/build.gradle.kts` | `"template-engine"` | `"<project_name>-engine"` |
| `persistence/build.gradle.kts` | `"template-persistence"` | `"<project_name>-persistence"` |
| `README.md` | `# kotlin_gradle_template` | `# <project_name>` |

## Package Directory Restructure

Move all Kotlin source files from the old package path to the new one:

- `…/kotlin/com/nrkei/project/template/` → `…/kotlin/com/nrkei/project/<project_name>/`
- `…/kotlin/com/nrkei/project/template/unit/` → `…/kotlin/com/nrkei/project/<project_name>/unit/`

This applies under all four source roots:
- `engine/src/main/kotlin/`
- `persistence/src/main/kotlin/`
- `persistence/src/test/kotlin/`
- `tests/src/test/kotlin/`

After moving files, update the `package` and `import` declarations inside each `.kt` file,
replacing `com.nrkei.project.template` with `com.nrkei.project.<project_name>`.

Then remove the now-empty `template` (and `template/unit`) directories from all four source roots.

## Verify

- No occurrences of `template` remain in any `.kt`, `.kts`, `.xml`, or `.md` file
  (except in `ai_template.md` and `CLAUDE.md`, which describe the template itself).
- `./gradlew clean build test` passes.

## Push to GitHub

First, create the repo on GitHub — either at https://github.com/new (name it `<project_name>`,
leave it completely empty), or via the `gh` CLI:

```bash
gh repo create fredgeorge/<project_name> --private
```

Then make the initial commit and push from Terminal:

```bash
cd ~/src/kotlin/<project_name>
git add .
git commit -m "Fred: Initial project from kotlin_gradle_template"
git branch -M main
git push -u origin main
```

## Cleanup

Run these commands in Terminal to remove the now-empty `template` package directories
(substitute `<project_name>` with the actual project name):

```bash
cd ~/src/kotlin/<project_name>
rmdir engine/src/main/kotlin/com/nrkei/project/template
rmdir persistence/src/main/kotlin/com/nrkei/project/template
rmdir persistence/src/test/kotlin/com/nrkei/project/template/unit
rmdir persistence/src/test/kotlin/com/nrkei/project/template
rmdir tests/src/test/kotlin/com/nrkei/project/template/unit
rmdir tests/src/test/kotlin/com/nrkei/project/template
```
