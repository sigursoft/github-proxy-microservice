FROM amazoncorretto:11

RUN mkdir /app

WORKDIR /app

COPY /target/dist/libs libs

COPY target/dist/simple-github-service-2.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "-DHOST=0.0.0.0", "app.jar"]
