FROM openjdk:17

ENV OPENAI_KEY=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

VOLUME /tmp
EXPOSE 8080
COPY target/gardening-0.0.1-SNAPSHOT.jar gardening-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/gardening-0.0.1-SNAPSHOT.jar"]
