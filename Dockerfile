# Step 1
FROM openjdk:12-alpine AS build

RUN mkdir -p /var/rabbit
WORKDIR /var/rabbit

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod 777 ./mvnw && ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Step 2
FROM openjdk:12-alpine

ARG DEPENDENCY=/var/rabbit/target/dependency

RUN mkdir -p /var/rabbit
RUN mkdir -p /var/rabbit/files/attach
RUN mkdir -p /var/rabbit/files/avatar

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /var/rabbit/lib
COPY --from=build ${DEPENDENCY}/META-INF /var/rabbit/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /var/rabbit

WORKDIR /var/rabbit
ENTRYPOINT ["java","-cp","/var/rabbit:/var/rabbit/lib/*","com.rabbit.backend.BackendApplication"]