#!/bin/bash
# =========================================================
# MY HOME - build and run (macOS / Linux)
# Usage: ./run.sh
# =========================================================

echo ""
mkdir -p out
javac -d out $(find src -name "*.java")

if [ $? -ne 0 ]; then
    echo ""
    echo "Compilation failed. Please check the errors above."
    exit 1
fi

echo ""
echo ""
echo ""
java -cp out com.myhome.Main
