FROM bellsoft/liberica-openjdk-alpine-musl:11
WORKDIR /
ARG VERSION
ADD /target/lessons-$VERSION.jar app.jar
EXPOSE 8080
CMD java -jar app.jar