#!/usr/bin/env bash

OUTPUT_DIR="logs"
mkdir -p ${OUTPUT_DIR}
CONTAINERS=$(docker ps --format '{{.Names}}')
echo "found containers: ${CONTAINERS}"
for CONTAINER in ${CONTAINERS}; do
    docker logs ${CONTAINER} >& ${OUTPUT_DIR}/${CONTAINER}.log
done

echo "Successfully exported logs in ${OUTPUT_DIR} directory."
