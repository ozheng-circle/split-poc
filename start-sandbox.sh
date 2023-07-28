#!/usr/bin/env bash
# Copyright (c) 2023, Circle Internet Financial Trading Company Limited.
# All rights reserved.
#
# Circle Internet Financial Trading Company Limited CONFIDENTIAL
# This file includes unpublished proprietary source code of Circle Internet
# Financial Trading Company Limited, Inc. The copyright notice above does not
# evidence any actual or intended publication of such source code. Disclosure
# of this source code or any related proprietary information is strictly
# prohibited without the express written permission of Circle Internet Financial
# Trading Company Limited.

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d\" -f 2 | cut -d. -f 1)
if [[ "$JAVA_VERSION" -lt "17" ]]; then
    echo "Please install Java 17 or later."
fi

export APP_ENV=${APP_ENV:-dev}
export SECRET_TYPE=${SECRET_TYPE:-file}
export KEYSTORE_PATH=${KEYSTORE_PATH:-./sandbox/keystore.jks}
# set suffix to username if it does not exist
USER_ID=$(id -u -n | sed 's/\./_/g')
export SUFFIX=${SUFFIX:-"_${USER_ID}"}

export DOCROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

PID_FILE="$DOCROOT/sandbox.pid"

usage()
{
cat <<EOF
usage: $0 options
This script starts the sandbox-client service.
OPTIONS:
    -y      yaml configuration file to be loaded
EOF
}

BOOTCLASSPATH=
YAML=${DOCROOT}/sandbox/target/test-classes/sandbox.yml


while getopts ":c:y:" OPTION
do
    case ${OPTION} in
    y)
        if [[ -f "$OPTARG" ]];
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

if [[ -e "${PID_FILE}" ]];
then
    PID=$(cat ${PID_FILE})
    if kill -0 &>/dev/null ${PID}; then
    echo "sandbox already running"
    exit
    else
    rm ${PID_FILE}
    fi
fi

if [[ -f "${DOCROOT}/sandbox/target/sandbox-0.0.1-SNAPSHOT.jar" && -f "$YAML" ]];
then
    java ${BOOTCLASSPATH} -jar ${DOCROOT}/sandbox/target/sandbox-0.0.1-SNAPSHOT.jar server "${YAML}" &
    echo $! > ${PID_FILE}
else
    echo "The sandbox jar and yaml configuration file were not found. You may need to build first?"
fi
