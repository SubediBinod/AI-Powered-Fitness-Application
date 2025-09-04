FROM openjdk:21
WORKDIR /app
COPY target/spring-boot.jar myapp.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","myapp.jar"]
