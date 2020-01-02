FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/user-service-1.0.0-SNAPSHOT.jar user-service.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /user-service.jar" ]
EXPOSE 8082