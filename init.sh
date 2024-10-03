#!/usr/bin/env bash

set -e

export SPRING_DATASOURCE_URL="${JDBC_DATABASE_URL}"

ENVIRONMENT_NAME="${SPRING_PROFILES_ACTIVE:-"staging"}"
JVM_OPS="${JVM_OPS:-""}"
APPLICATION_PORT="${PORT:-"8000"}"
echo "ENVIRONMENT_NAME: ${ENVIRONMENT_NAME}"

COMMAND=${1:-"web"}
echo $COMMAND

echo "Application port:  $APPLICATION_PORT"

case "$COMMAND" in
  migrate|web|worker)
    exec java ${JVM_OPS} -Djava.security.egd=file:/dev/./urandom \
      -javaagent:/app/dd-java-agent.jar \
      -Ddatadog.slf4j.simpleLogger.logFile=System.out \
      -Ddatadog.slf4j.simpleLogger.dateTimeFormat="yyyy-MM-dd HH:mm:ss.SSS" \
      -Duser.timezone=America/Sao_Paulo \
      -Ddd.profiling.enabled=false \
      -Ddd.logs.injection=true \
      -Ddd.trace.sample.rate=1 \
      -Ddd.kafka.legacy.tracing.enabled=false \
      -XX:FlightRecorderOptions=stackdepth=256 \
      -Duser.timezone=America/Sao_Paulo \
      -Dspring.profiles.active=${ENVIRONMENT_NAME} \
      -Dserver.port=$APPLICATION_PORT \
      -jar /app/kotlin-spring-sample-*.jar \
      $COMMAND
    ;;
  *)
    exec sh -c "$*"
    ;;
esac
