FROM openjdk:17

ENV OPENAI_KEY=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

VOLUME /tmp
EXPOSE 8080
COPY target/gardening-0.0.1-SNAPSHOT.jar gardening-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/gardening-0.0.1-SNAPSHOT.jar"]
