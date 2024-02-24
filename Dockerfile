FROM eclipse-temurin:17.0.3_7-jre

WORKDIR /app

COPY build/libs/crm-0.0.1-SNAPSHOT.jar /app/crm.jar

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MinRAMPercentage=60.0 -XX:MaxRAMPercentage=80.0"

EXPOSE 8080

ENTRYPOINT java $JAVA_OPTS -jar /app/crm.jar
