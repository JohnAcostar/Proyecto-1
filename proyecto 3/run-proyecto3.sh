#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
mkdir -p bin

sources_file="$(mktemp)"
find src -name '*.java' -print > "$sources_file"
javac --release 21 -encoding UTF-8 -cp "lib/*" -d bin @"$sources_file"
rm -f "$sources_file"

if [[ "${1:-}" != "--compile-only" ]]; then
  java -cp "bin:lib/*" gui.MainSwing
fi
