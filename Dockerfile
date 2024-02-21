FROM eclipse-temurin:17.0.3_7-jre

WORKDIR /app

RUN find .
COPY build/libs/mailcampaign-0.0.1.jar /app/mailcampaign.jar

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MinRAMPercentage=60.0 -XX:MaxRAMPercentage=80.0"

EXPOSE 8080

ENTRYPOINT java $JAVA_OPTS -jar /app/mailcampaign.jar
