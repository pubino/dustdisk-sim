#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
DIST_DIR="$PROJECT_DIR/dist"
APP_PATH="$DIST_DIR/DustDiskSimulator.app"
ZIP_PATH="$DIST_DIR/DustDiskSim.zip"
DEVELOPER_ID_APP="${DEVELOPER_ID_APP:-Developer ID Application: Your Name (TEAM_ID)}"
KEYCHAIN_PROFILE="${KEYCHAIN_PROFILE:-DustDiskSim-notarize}"
[ ! -d "$APP_PATH" ] && { echo "Error: App not found"; exit 1; }
codesign --force --deep --options runtime --timestamp --sign "$DEVELOPER_ID_APP" "$APP_PATH"
ditto -c -k --keepParent "$APP_PATH" "$ZIP_PATH"
xcrun notarytool submit "$ZIP_PATH" --keychain-profile "$KEYCHAIN_PROFILE" --wait
xcrun stapler staple "$APP_PATH"
echo "Complete!"
