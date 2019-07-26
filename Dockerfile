# Step 1
FROM openjdk:12-oracle AS build

RUN mkdir -p /var/rabbit
WORKDIR /var/rabbit

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod 777 ./mvnw && sudo ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Step 2

FROM openjdk:12-oracle

ARG DEPENDENCY=/var/rabbit/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.rabbit.backend.BackendApplication"]