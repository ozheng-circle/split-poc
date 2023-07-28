FROM 514563129364.dkr.ecr.us-east-1.amazonaws.com/circle-dockerhub/python:3.7-slim-buster

RUN apt-get update && apt-get install -y jq

RUN pip3 install --trusted-host pypi.org --trusted-host pypi.python.org --trusted-host=files.pythonhosted.org --no-cache-dir --user cookiecutter

COPY java-dw-microservice /app/java-dw-microservice

CMD  jq --arg project "${GITHUB_REPO_NAME}" \
       --arg project_name "${GITHUB_REPO_NAME}" \
       --arg business_domain "${BUSINESS_DOMAIN}" \
       --arg aws_account "${AWS_ACCOUNT}" \
       --arg group_id "${GROUP_ID}" \
       --arg package "${PACKAGE}" \
       --arg app_port "${APP_PORT}" \
       --arg admin_port "${ADMIN_PORT}" \
       --arg db_port "${DB_PORT}" \
       --arg dba_password "${DBA_PASSWORD}" \
       --arg db_password "${DB_PASSWORD}" ".project = \$project | \
        .project_name = \$project_name | \
        .business_domain = [\$business_domain] | \
        .aws_account = [\$aws_account] | \
        .group_id = \$group_id | \
        .package = \$package | \
        .app_port = \$app_port | \
        .admin_port = \$admin_port | \
        .db_port = \$db_port | \
        .dba_password = \$dba_password | \
        .db_password = \$db_password"  /app/java-dw-microservice/cookiecutter.json > /app/cookiecutter-updated.json \ 
    && mv /app/cookiecutter-updated.json /app/java-dw-microservice/cookiecutter.json \
    && cd /service-template && python -m cookiecutter /app/java-dw-microservice -f --no-input
