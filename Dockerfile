FROM openjdk:17
EXPOSE 8080
COPY target/gardening-0.0.1-SNAPSHOT.jar gardening-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/gardening-0.0.1-SNAPSHOT.jar"]
