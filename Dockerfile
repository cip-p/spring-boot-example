FROM eclipse-temurin:17.0.5_8-jdk-focal

COPY target/cake-service-0.0.1-SNAPSHOT.jar cake-service.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","/cake-service.jar"]
