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

DOCROOT="$( cd "$( dirname "$0" )" && pwd )"

usage()
{
cat <<EOF
usage: $0 options
This script starts the sandbox.
EOF
}

COVERAGE=
while getopts ":c:" OPTION
do
    case ${OPTION} in
    ?)
        usage
        exit
        ;;
    esac
done

if [[ ! -d "${DOCROOT}/logs" ]]; then
    mkdir "${DOCROOT}/logs"
fi

echo "Starting sandbox ..."
${DOCROOT}/start-sandbox.sh ${COVERAGE} &> ${DOCROOT}/logs/sandbox.log &

echo "all output will be logged to ${DOCROOT}/logs"
