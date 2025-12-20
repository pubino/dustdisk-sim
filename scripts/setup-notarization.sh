#!/bin/bash
set -e
KEYCHAIN_PROFILE="${KEYCHAIN_PROFILE:-DustDiskSim-notarize}"
echo "=== Apple Notarization Setup ==="
read -p "Apple ID: " APPLE_ID
read -p "Team ID: " TEAM_ID
read -s -p "App-Specific Password: " APP_PASSWORD
echo ""
xcrun notarytool store-credentials "$KEYCHAIN_PROFILE" --apple-id "$APPLE_ID" --team-id "$TEAM_ID" --password "$APP_PASSWORD"
echo "Credentials stored!"
