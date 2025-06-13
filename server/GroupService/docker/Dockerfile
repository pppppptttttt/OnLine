FROM astita/openjdk21-alpine
WORKDIR /app

ADD ./build/libs/group-service.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
