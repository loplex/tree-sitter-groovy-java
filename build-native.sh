#!/bin/bash
set -e

# Ensure we are in the project root directory
cd "$(dirname "$0")"

# Detect OS and Architecture
OS_NAME=${OS_NAME:-$(uname -s | tr '[:upper:]' '[:lower:]')}
ARCH_NAME=${ARCH_NAME:-$(uname -m)}
CC=${CC:-gcc}

# Normalize Architecture
case "$ARCH_NAME" in
    x86_64)
        ARCH="amd64"
        ;;
    aarch64|arm64)
        ARCH="aarch64"
        ;;
    *)
        ARCH="$ARCH_NAME"
        ;;
esac

# Normalize OS variables
case "$OS_NAME" in
    *linux*)
        OS="linux"
        JNI_OS="linux"
        LIB_EXT=".so"
        ;;
    *darwin*)
        OS="darwin"
        JNI_OS="darwin"
        LIB_EXT=".dylib"
        ;;
    *mingw*|*cygwin*|*msys*)
        OS="windows"
        JNI_OS="win32"
        LIB_EXT=".dll"
        ;;
    *)
        echo "Unsupported OS: $OS_NAME"
        exit 1
        ;;
esac

LIB_NAME="libtree-sitter-groovy${LIB_EXT}"
TARGET_DIR="target/classes/native/${OS}-${ARCH}"

# Setup directories
mkdir -p target/native-build
mkdir -p "$TARGET_DIR"

# Locate JAVA_HOME for JNI headers
if [ -z "$JAVA_HOME" ]; then
    JAVA_HOME="$(dirname "$(dirname "$(readlink -f "$(which java)")")")"
fi

echo "Using JAVA_HOME: $JAVA_HOME"
echo "Building for: ${OS}-${ARCH}"

# Compile parser.c, scanner.c and our JNI wrapper into a shared library
$CC -O3 -shared -fPIC \
    -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/$JNI_OS" \
    -I"third_party/tree-sitter-groovy/src" \
    third_party/tree-sitter-groovy/src/parser.c \
    third_party/tree-sitter-groovy/src/scanner.c \
    src/main/native/TreeSitterGroovy.c \
    -o "target/native-build/$LIB_NAME"

# Put it in resources
cp "target/native-build/$LIB_NAME" "$TARGET_DIR/"
echo "Successfully built $LIB_NAME into $TARGET_DIR"
