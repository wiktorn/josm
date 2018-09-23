#!/bin/sh

die() { echo "$@" 1>&2 ; exit 1; }

echo "java -version:"
java -Xmx32m -version
echo "javac -version:"
javac -J-Xmx32m -version
echo "ant -version"
ant -version

_JAVA_VER_FOR_ANT=$(java -version 2>&1 | grep "openjdk version" | sed -e 's/openjdk version "\([^"]*\)".*$/\1/')

echo "Using osm dev user $OSM_USERDEF"

ANT_OPTS="-Xmx600m" ant "-Dant.java.version=${_JAVA_VER_FOR_ANT}" $OSM_USERDEF $OSM_PASSWORDDEF $TARGET
