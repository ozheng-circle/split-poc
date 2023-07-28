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

# Convenience script for rebuilding the db without re-creating sandbox jar
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d\" -f 2 | cut -d. -f 1)
if [[ "$JAVA_VERSION" -lt "17" ]]; then
    echo "Please install Java 17 or later."
fi

# DB migration
export APP_CONFIG_OPTION_DB_MIGRATION_LOCATIONS='db/postgres/sandbox','db/postgres/dev-qa'

DOCROOT="$( cd "$( dirname "$0" )" && pwd )"

usage()
{
cat <<EOF
usage: $0 options
This script starts the sandbox service.
OPTIONS:
    -y      yaml configuration file to be loaded
EOF
}

BOOTCLASSPATH="-Dexec.cleanupDaemonThreads=false"
YAML=$DOCROOT/sandbox/target/test-classes/sandbox.yml


while getopts ":y:" OPTION
do
    case $OPTION in
    y)
        if [ -f "$OPTARG" ];
        then
            YAML="$OPTARG"
        else
            echo $OPTARG not found.
            exit
        fi
        ;;
    ?)
        usage
        exit
        ;;
    esac
done


if [[ -f "$DOCROOT/sandbox/target/sandbox-0.0.1-SNAPSHOT.jar" && -f "$YAML" ]];
then
	echo "ready to create db"
    java $BOOTCLASSPATH -jar $DOCROOT/sandbox/target/sandbox-0.0.1-SNAPSHOT.jar createdb "$YAML"

    java $BOOTCLASSPATH -jar $DOCROOT/sandbox/target/sandbox-0.0.1-SNAPSHOT.jar flyway migrate "$YAML"

    java $BOOTCLASSPATH -jar $DOCROOT/sandbox/target/sandbox-0.0.1-SNAPSHOT.jar flyway info "$YAML"
else
    echo "The sandbox jar and yaml configuration file were not found. You may need to build first?"
fi
