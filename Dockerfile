FROM eclipse-temurin:25-jdk AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY app/pom.xml app/pom.xml
RUN ./mvnw -B -pl app -am -Dmaven.test.skip=true dependency:resolve -DincludeScope=runtime

COPY app/src app/src
RUN ./mvnw -B -pl app -am -Dmaven.test.skip=true package \
  && ./mvnw -B -pl app -Dmaven.test.skip=true dependency:copy-dependencies \
    -DincludeScope=runtime \
    -DoutputDirectory=target/dependency

FROM eclipse-temurin:25-jre

WORKDIR /app

RUN addgroup --system app && adduser --system --ingroup app app

COPY --from=build /workspace/app/target/service-template-app-*.jar /app/app.jar
COPY --from=build /workspace/app/target/dependency /app/lib

USER app
EXPOSE 8080
EXPOSE 9090

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS:-} -cp '/app/app.jar:/app/lib/*' com.example.service.Main"]
