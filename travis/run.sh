#!/bin/sh

die() { echo "$@" 1>&2 ; exit 1; }

echo "java -version:"
java -Xmx32m -version
echo "javac -version:"
javac -J-Xmx32m -version
echo

echo "Using osm dev user $OSM_USERDEF"

ANT_OPTS="-Xmx600m" ant $OSM_USERDEF $OSM_PASSWORDDEF $TARGET
