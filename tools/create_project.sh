#!/usr/bin/env bash
# Creates a new Kotlin project from kotlin_gradle_template
# Usage: bash create_project.sh <project_name>

set -euo pipefail

# ── 0. Validate argument ──────────────────────────────────────────────────────
if [ $# -ne 1 ]; then
  echo "Usage: $0 <project_name>" >&2
  echo "Example: $0 context" >&2
  exit 1
fi

PROJECT_NAME="$1"

# Basic sanity check: must be a valid Kotlin package segment
# (letters, digits, underscores; cannot start with a digit)
if ! [[ "$PROJECT_NAME" =~ ^[A-Za-z_][A-Za-z0-9_]*$ ]]; then
  echo "ERROR: '$PROJECT_NAME' is not a valid project name." >&2
  echo "       Use letters, digits, and underscores only; must not start with a digit." >&2
  exit 1
fi

KOTLIN_SRC="$HOME/src/kotlin"
TEMPLATE="$KOTLIN_SRC/kotlin_gradle_template"
DEST="$KOTLIN_SRC/$PROJECT_NAME"

# ── 1. Copy template ──────────────────────────────────────────────────────────
echo "==> Copying template to $DEST ..."

if [ -d "$DEST" ]; then
  echo "ERROR: $DEST already exists. Aborting." >&2
  exit 1
fi

rsync -a \
  --exclude='.git' \
  --exclude='.gradle' \
  --exclude='build' \
  --exclude='*.jar' \
  "$TEMPLATE/" "$DEST/"

# gradle-wrapper.jar is excluded by .gitignore (*.jar) but required for ./gradlew
cp "$TEMPLATE/gradle/wrapper/gradle-wrapper.jar" \
   "$DEST/gradle/wrapper/gradle-wrapper.jar"

echo "    Done."

# ── 2. Git init ───────────────────────────────────────────────────────────────
echo "==> Initialising git repository ..."
cd "$DEST"
git init
git remote add origin "https://github.com/fredgeorge/$PROJECT_NAME.git"
echo "    Done."

# ── 3. String substitutions ───────────────────────────────────────────────────
echo "==> Substituting project-specific strings ..."

sed -i '' \
  's/"kotlin-gradle-template"/"'"$PROJECT_NAME"'"/g' \
  "$DEST/settings.gradle.kts"

sed -i '' \
  's/com\.nrkei\.project\.template/com.nrkei.project.'"$PROJECT_NAME"'/g' \
  "$DEST/build.gradle.kts"

sed -i '' \
  's/"template-engine"/"'"$PROJECT_NAME"'-engine"/g' \
  "$DEST/engine/build.gradle.kts"

sed -i '' \
  's/"template-persistence"/"'"$PROJECT_NAME"'-persistence"/g' \
  "$DEST/persistence/build.gradle.kts"

sed -i '' \
  's/# kotlin_gradle_template/# '"$PROJECT_NAME"'/g' \
  "$DEST/README.md"

echo "    Done."

# ── 4. Restructure package directories & update declarations ──────────────────
echo "==> Restructuring package directories ..."

SOURCE_ROOTS=(
  "engine/src/main/kotlin"
  "persistence/src/main/kotlin"
  "persistence/src/test/kotlin"
  "tests/src/test/kotlin"
)

for root in "${SOURCE_ROOTS[@]}"; do
  OLD_DIR="$DEST/$root/com/nrkei/project/template"
  NEW_DIR="$DEST/$root/com/nrkei/project/$PROJECT_NAME"

  if [ -d "$OLD_DIR" ]; then
    mkdir -p "$NEW_DIR"

    # Move unit subdirectory first if it exists
    if [ -d "$OLD_DIR/unit" ]; then
      mkdir -p "$NEW_DIR/unit"
      find "$OLD_DIR/unit" -maxdepth 1 -name "*.kt" -exec mv {} "$NEW_DIR/unit/" \;
    fi

    # Move top-level .kt files
    find "$OLD_DIR" -maxdepth 1 -name "*.kt" -exec mv {} "$NEW_DIR/" \;
  fi
done

echo "    Done."

# ── 5. Update package/import declarations ─────────────────────────────────────
echo "==> Updating package and import declarations in .kt files ..."

find "$DEST" -name "*.kt" | while read -r f; do
  sed -i '' \
    's/com\.nrkei\.project\.template/com.nrkei.project.'"$PROJECT_NAME"'/g' \
    "$f"
done

echo "    Done."

# ── 6. Remove empty template directories ──────────────────────────────────────
echo "==> Removing empty template package directories ..."

for root in "${SOURCE_ROOTS[@]}"; do
  OLD_UNIT="$DEST/$root/com/nrkei/project/template/unit"
  OLD_DIR="$DEST/$root/com/nrkei/project/template"
  [ -d "$OLD_UNIT" ] && rmdir "$OLD_UNIT" 2>/dev/null || true
  [ -d "$OLD_DIR"  ] && rmdir "$OLD_DIR"  2>/dev/null || true
done

echo "    Done."

# ── 7. Verify ─────────────────────────────────────────────────────────────────
echo "==> Verifying no 'template' references remain ..."

MATCHES=$(grep -r --include="*.kt" --include="*.kts" --include="*.xml" --include="*.md" \
  "template" "$DEST" \
  --exclude="ai_template.md" --exclude="CLAUDE.md" \
  -l 2>/dev/null || true)

if [ -n "$MATCHES" ]; then
  echo "WARNING: 'template' still found in:"
  echo "$MATCHES"
else
  echo "    Clean — no stray 'template' references found."
fi

echo "==> Running ./gradlew clean build test ..."
cd "$DEST"
./gradlew clean build test

echo "    Build and tests passed."

# ── 8. GitHub + initial commit ────────────────────────────────────────────────
echo ""
echo "==> Next steps (run manually after creating the GitHub repo):"
echo ""
echo "    gh repo create fredgeorge/$PROJECT_NAME --private"
echo ""
echo "    cd $DEST"
echo "    git add ."
echo "    git commit -m 'Fred: Initial project from kotlin_gradle_template'"
echo "    git branch -M main"
echo "    git push -u origin main"
echo ""
echo "All done!"
