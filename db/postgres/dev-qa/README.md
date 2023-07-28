# Folder for dev/qa migrations

This folder contains all dev/qa flyway migrations.

Contents of this folders are **never** copied to the final docker image. They are present in the build stage of the
multistage docker build. This stage is used for testing in dev/qa. The `docker-compose.yml` overrides
`APP_CONFIG_OPTION_DB_MIGRATION_LOCATIONS` for the test services to include this folder along with the staging/prod
migrations.
