#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
BUILD_DIR="$PROJECT_DIR/build"
DIST_DIR="$PROJECT_DIR/dist"
rm -rf "$BUILD_DIR" "$DIST_DIR"
mkdir -p "$BUILD_DIR" "$DIST_DIR"
cd "$PROJECT_DIR"
javac -d "$BUILD_DIR" -sourcepath . -Xlint:-deprecation -Xlint:-removal \
    astro/galaxy/DustDisk.java astro/galaxy/DustDiskCanvas.java astro/galaxy/Body3d.java astro/galaxy/Data3d.java \
    myutil/AppletFrame.java myutil/GL.java myutil/SL.java myutil/Format.java
cd "$BUILD_DIR"
jar cfe "$DIST_DIR/DustDiskSim.jar" astro.galaxy.DustDisk astro/ myutil/
echo "Build complete: $DIST_DIR/DustDiskSim.jar"
