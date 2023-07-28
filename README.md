# sandbox

Run this to start docker container:

```
# $COMMON_PARENT_DIR is the directory where all of your Circle GitHub repositories exist
cd $COMMON_PARENT_DIR/sandbox
./docker-start-containers.sh -i
```

Run this to build your dev:

```
./build-dev.sh rebuild-db
```

Run this to start your service:

```
./start-sandbox.sh
```

Test your service:

```
curl --insecure https://localhost:11121/api/ping
```

You should see an empty response. If you see a 404 response body, it means that your service is not up and running.

Rebuild and Restart Service

If you made a code change, run this to rebuild and restart your service:

```
docker-compose down
./docker-start-containers.sh -i
./build-dev.sh skip-tests rebuild-db

```
