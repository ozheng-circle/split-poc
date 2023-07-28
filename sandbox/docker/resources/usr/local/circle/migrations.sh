#!/usr/bin/env bash

# This script is used to perform migrations for the service.  It also handles
# creating the database if appropriate.

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

echo "Running migrations..."

set -x

# cd to the install directory (ie our current one), create the database, and run
# the migration commands
cd "$DIR"
java -cp "./sandbox-0.0.1-SNAPSHOT.jar:./dependency/*"  com.circle.sandbox.Application createdb ./sandbox.yml
java -cp "./sandbox-0.0.1-SNAPSHOT.jar:./dependency/*" com.circle.sandbox.Application flyway migrate ./sandbox.yml
RESULT=$?
java -cp "./sandbox-0.0.1-SNAPSHOT.jar:./dependency/*" com.circle.sandbox.Application flyway info ./sandbox.yml
 if (( RESULT == 0 )) ; then
    echo "DB migration succeeded"
    exit 0
else
    echo "DB migration failed"
    exit 1
fi
