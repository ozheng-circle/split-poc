#!/usr/bin/env bash

# Runs the service.  This script should never be called directly; it is intended to be called either
# by runDev.sh (in development) or supervisord (in production).  It's just used to abstract out the
# service start command.  Note that it expects to be in the same directory that service has been
# installed to.

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"


# Check for the debug flag.  -d will cause the service to start up and wait
# for a debugger to be attached.
if [[ $* == *-d* ]]; then
    echo "IN DEBUG MODE"
    # If are connecting to this from IntelliJ, use 5014 instead of 5006 (5006 is the
    # port on the Docker container; 5014 is the port on your development machine)
    DEBUG_ARGS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006"
else
    DEBUG_ARGS=""
fi

set -e
set -x

# cd to the install directory (ie our current one)
cd $DIR

# Run the service
java -cp "./sandbox-0.0.1-SNAPSHOT.jar:./dependency/*" $DEBUG_ARGS com.circle.sandbox.Application server ./sandbox.yml
