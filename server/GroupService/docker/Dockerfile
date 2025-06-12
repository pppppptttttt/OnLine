FROM astita/openjdk21-alpine
WORKDIR /app
COPY build/libs/groupService.jar .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "groupService.jar"]