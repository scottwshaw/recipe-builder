FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/recipe-builder.jar /recipe-builder/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/recipe-builder/app.jar"]
