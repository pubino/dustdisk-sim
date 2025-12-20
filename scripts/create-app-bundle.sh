#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
DIST_DIR="$PROJECT_DIR/dist"
APP_NAME="DustDiskSimulator"
DISPLAY_NAME="Dust Disk Simulator"
BUNDLE_ID="edu.princeton.vanderbei.dustdisk-sim"
VERSION="1.0.0"
[ ! -f "$DIST_DIR/DustDiskSim.jar" ] && "$SCRIPT_DIR/build.sh"
APP_DIR="$DIST_DIR/$APP_NAME.app"
rm -rf "$APP_DIR"
mkdir -p "$APP_DIR/Contents/MacOS" "$APP_DIR/Contents/Resources"
cp "$DIST_DIR/DustDiskSim.jar" "$APP_DIR/Contents/Resources/"
cat > "$APP_DIR/Contents/Info.plist" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0"><dict>
<key>CFBundleExecutable</key><string>launcher</string>
<key>CFBundleIdentifier</key><string>$BUNDLE_ID</string>
<key>CFBundleName</key><string>$DISPLAY_NAME</string>
<key>CFBundleVersion</key><string>$VERSION</string>
<key>NSHighResolutionCapable</key><true/>
</dict></plist>
EOF
cat > "$APP_DIR/Contents/MacOS/launcher" << 'EOF'
#!/bin/bash
JAVA="$(/usr/libexec/java_home 2>/dev/null)/bin/java"
[ ! -x "$JAVA" ] && JAVA="java"
DIR="$(cd "$(dirname "$0")/../Resources" && pwd)"
exec "$JAVA" -Xmx512m -jar "$DIR/DustDiskSim.jar"
EOF
chmod +x "$APP_DIR/Contents/MacOS/launcher"
echo "Created: $APP_DIR"
