#!/bin/bash
set -e

# 1. Generate version with timestamp
VERSION="0.0.5-$(date +%Y%m%d%H%M%S)"
echo "🚀 Releasing commun version: $VERSION"

# 2. Update version in pom.xml
./mvnw versions:set -DnewVersion=$VERSION
./mvnw versions:commit

# 3. Clean, build, and deploy to GitHub Packages
echo "🔨 Building & deploying commun..."
./mvnw clean deploy -DskipTests -Drevision=$VERSION

# 4. Commit and push version change
git add pom.xml
git commit -m "chore: release commun $VERSION" || echo "No changes to commit"
git push origin main

# 5. Tag this version (optional but good practice)
git tag "v$VERSION"
git push origin "v$VERSION"

echo "✅ commun $VERSION built & deployed to GitHub Packages."

# 6. Trigger GitHub Actions workflow (optional)
if command -v gh &>/dev/null; then
    read -p "Do you want to trigger GitHub Actions (pedagogie build)? (y/n): " answer
    if [[ "$answer" == "y" ]]; then
        gh workflow run "📦 Deploy commun"
        echo "🔄 Workflow triggered manually."
    else
        echo "⏭ Skipped manual workflow trigger."
    fi
else
    echo "⚠️ GitHub CLI (gh) not found. Skipping manual workflow trigger."
fi
