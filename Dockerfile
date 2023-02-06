FROM openjdk:8-alpine

WORKDIR /usr/app
COPY . .
RUN ./gradlew build -x test

ENTRYPOINT ["java","-jar","./build/libs/postinscripciones-0.0.1-SNAPSHOT.jar"]