#!/bin/bash
# =========================================================
# NESTORA - build and run (macOS / Linux)
# Usage: ./run.sh
# =========================================================

echo "Compiling NESTORA..."
mkdir -p out
javac -d out $(find src -name "*.java")

if [ $? -ne 0 ]; then
    echo ""
    echo "Compilation failed. Please check the errors above."
    exit 1
fi

echo ""
echo "Starting NESTORA..."
echo ""
java -cp out com.nestora.Main
