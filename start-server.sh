#!/usr/bin/env bash
# start-server.sh — build and run the collaborative whiteboard server.
#
# Usage:
#   ./start-server.sh              # runs on default port 8080
#   ./start-server.sh 9090         # runs on port 9090
#   SKIP_BUILD=1 ./start-server.sh # skip mvn package, just run the existing jar

set -euo pipefail

# Always run from the directory this script lives in
cd "$(dirname "$0")"

PORT="${1:-8080}"
SRC_DIR="src/main/java/com/whiteboard"

# ── 1. Prereq check ──────────────────────────────────────────────
command -v java >/dev/null 2>&1 || { echo "ERROR: 'java' not found in PATH. Install JDK 17+."; exit 1; }
command -v mvn  >/dev/null 2>&1 || { echo "ERROR: 'mvn' not found in PATH. Install Maven 3.6+."; exit 1; }

# ── 2. Auto-fix project layout if needed ─────────────────────────
# Maven expects sources under src/main/java/com/whiteboard/, and Java
# requires the file extension to be lowercase ".java".
if [ ! -d "$SRC_DIR" ]; then
    echo "Setting up Maven source layout under $SRC_DIR ..."
    mkdir -p "$SRC_DIR"
fi

# Move any stray .java/.Java files at the repo root into the package dir
shopt -s nullglob nocaseglob
for f in *.java; do
    base="$(basename "$f")"
    # Force lowercase extension (e.g. WhiteboardServer.Java -> WhiteboardServer.java)
    name="${base%.*}"
    target="$SRC_DIR/${name}.java"
    if [ ! -f "$target" ]; then
        echo "  moving $f -> $target"
        mv "$f" "$target"
    fi
done
shopt -u nullglob nocaseglob

# ── 3. Build ─────────────────────────────────────────────────────
JAR="target/collaborative-whiteboard-1.0-SNAPSHOT.jar"

if [ "${SKIP_BUILD:-0}" != "1" ] || [ ! -f "$JAR" ]; then
    echo "Building with Maven..."
    mvn -q clean package
fi

if [ ! -f "$JAR" ]; then
    echo "ERROR: build did not produce $JAR"
    exit 1
fi

# ── 4. Run ───────────────────────────────────────────────────────
echo
echo "Starting whiteboard server on ws://localhost:${PORT}"
echo "Open index.html in two or more browser tabs to collaborate."
echo "Press Ctrl+C to stop."
echo

exec java -jar "$JAR" "$PORT"
