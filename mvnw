#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper script for POSIX systems
# ----------------------------------------------------------------------------
set -e

# resolve target-dir
PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
BASE_DIR=`dirname "$PRG"`
BASE_DIR=`cd "$BASE_DIR" >/dev/null; pwd`

# Setup environment
WRAPPER_PROPERTIES="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"
DISTRIBUTION_URL=$(grep "^distributionUrl=" "$WRAPPER_PROPERTIES" | cut -d= -f2- | tr -d '\r')
DIST_VERSION=$(echo "$DISTRIBUTION_URL" | sed -E 's/.*apache-maven-([0-9.]+)-bin\.zip/\1/')

USER_HOME="${HOME:-$USERPROFILE}"
MAVEN_USER_HOME="${MAVEN_USER_HOME:-$USER_HOME/.m2}"
MAVEN_HOME="$MAVEN_USER_HOME/wrapper/dists/apache-maven-$DIST_VERSION/bin"

if [ ! -d "$MAVEN_HOME" ]; then
  mkdir -p "$MAVEN_USER_HOME/wrapper/dists"
  ARCHIVE="$MAVEN_USER_HOME/wrapper/dists/apache-maven-$DIST_VERSION-bin.zip"
  echo "Downloading Maven $DIST_VERSION..."
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$DISTRIBUTION_URL" -o "$ARCHIVE"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$DISTRIBUTION_URL" -O "$ARCHIVE"
  else
    echo "Error: curl or wget is required to download Maven." >&2
    exit 1
  fi
  echo "Unzipping Maven..."
  unzip -q -o "$ARCHIVE" -d "$MAVEN_USER_HOME/wrapper/dists/apache-maven-$DIST_VERSION"
  EXTRACTED_DIR=$(find "$MAVEN_USER_HOME/wrapper/dists/apache-maven-$DIST_VERSION" -mindepth 1 -maxdepth 1 -type d | head -n 1)
  if [ -n "$EXTRACTED_DIR" ] && [ "$EXTRACTED_DIR" != "$MAVEN_USER_HOME/wrapper/dists/apache-maven-$DIST_VERSION" ]; then
    cp -R "$EXTRACTED_DIR"/* "$MAVEN_USER_HOME/wrapper/dists/apache-maven-$DIST_VERSION/"
    rm -rf "$EXTRACTED_DIR"
  fi
  rm -f "$ARCHIVE"
fi

MVN_CMD="$MAVEN_USER_HOME/wrapper/dists/apache-maven-$DIST_VERSION/bin/mvn"
chmod +x "$MVN_CMD" 2>/dev/null || true

exec "$MVN_CMD" "$@"
