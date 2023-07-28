ARG BASE_JRE=514563129364.dkr.ecr.us-east-1.amazonaws.com/circle-base/java:17-openjdk-jre-17

FROM $BASE_JRE

COPY target/sandbox-0.0.1-SNAPSHOT.jar /usr/local/circle/sandbox-0.0.1-SNAPSHOT.jar

COPY sandbox.yml /usr/local/circle/sandbox.yml

COPY keystore.jks /usr/local/circle/keystore.jks

COPY docker/resources/etc/supervisor/conf.d/sandbox.conf /etc/supervisor/conf.d/
COPY docker/resources/usr/local/circle /usr/local/circle/

ENV APP_ENV "dev"
ENV APP_DIR  /usr/local/circle
ENV SUFFIX ""
ENV KEYSTORE_PATH /usr/local/circle/keystore.jks

# Circle User Should own and run App
RUN  mkdir -p /etc/default \
  && touch /etc/default/circle \
  && mkdir -p "${APP_DIR}/logs" \
  && chown -R circle:nogroup "${APP_DIR}"

# See https://github.com/circlefin/docker-base for more info on supervisord setup.
# Start supervisord.
CMD [ "/usr/bin/supervisord", "-c", "/etc/supervisord.conf"]
