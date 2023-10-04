FROM eclipse-temurin:17-jre-focal

EXPOSE 8080

ADD build/libs/inq-0.0.1-SNAPSHOT.jar inq-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","inq-0.0.1-SNAPSHOT.jar"]