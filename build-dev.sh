#!/usr/bin/env bash
# Copyright (c) 2023, Circle Internet Financial Trading Company Limited.
# All rights reserved.
#
# Circle Internet Financial Trading Company Limited CONFIDENTIAL
#
# This file includes unpublished proprietary source code of Circle Internet
# Financial Trading Company Limited, Inc. The copyright notice above does not
# evidence any actual or intended publication of such source code. Disclosure
# of this source code or any related proprietary information is strictly
# prohibited without the express written permission of Circle Internet Financial
# Trading Company Limited.

# Check for Maven
command -v mvn >/dev/null 2>&1 || { echo >&2 "I require Maven but it's not installed.  Aborting."; exit 1; }

DOCROOT="$( cd "$( dirname "$0" )" && pwd )"

# Prepare localhost-based development environment.
USER_ID=$(id -u -n | sed 's/\./_/g')

# YML file parameters
export SUFFIX=${SUFFIX:-"_${USER_ID}"}

# DB migration
export APP_CONFIG_OPTION_DB_MIGRATION_LOCATIONS='db/postgres/sandbox','db/postgres/dev-qa'

export TRUST_SIGNING_CERTIFICATES="true"
export VERIFY_SIGNING_HOST="false"

# Stop the existing processes
./stop-all.sh

# Default dev env arguments
args=""

# Override args to be passed to maven
for var in "$@"
do
    if [[ ${var} == "rebuild-db" ]]; then
        echo "Setting skip.migration"
        args="$args -Dskip.migration=false"
    elif [[ ${var} == 'skip-tests' ]]; then
        echo "Setting skipTests"
        args="$args -DskipTests"
    elif [[ ${var} == 'update-snapshot' ]]; then
        echo "updating new snapshots from repository: this will override local snapshots"
        args="${args} -U"
    else
        args="${args} ${var}"
    fi
done


# Clean and compile
echo "Running mvn ${args} clean verify"
mvn ${args} -e clean verify
rc=$?
if [[ ${rc} != 0 ]] ; then
    exit ${rc}
fi

echo "Completed on $(date)"
