FROM gradle:8.10.0-jdk21 AS builder

USER root

ARG artifactory_contextUrl
ARG artifactory_user
ARG artifactory_password

ENV ORG_GRADLE_PROJECT_artifactory_user=$artifactory_user
ENV ORG_GRADLE_PROJECT_artifactory_password=$artifactory_password
ENV ORG_GRADLE_PROJECT_artifactory_contextUrl=$artifactory_contextUrl

ENV APP_DIR /app
WORKDIR $APP_DIR

RUN wget -O dd-java-agent.jar 'https://dtdg.co/latest-java-tracer'

COPY build.gradle.kts $APP_DIR/
COPY settings.gradle.kts $APP_DIR/

RUN gradle dependencies --info

COPY . $APP_DIR

RUN gradle clean bootJar

USER guest

# -----------------------------------------------------------------------------

FROM amazoncorretto:21

WORKDIR /app

COPY --from=builder /app/init.sh /app
COPY --from=builder /app/build/libs/kotlin-spring-sample-*.jar /app/

COPY --from=builder /app/dd-java-agent.jar /app/

ENTRYPOINT ["sh", "init.sh"]
