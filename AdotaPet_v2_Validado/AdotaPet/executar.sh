#!/bin/sh
cd "$(dirname "$0")" || exit 1
exec java -jar dist/AdotaPet.jar
