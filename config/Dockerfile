FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/config-1.0.0-SNAPSHOT.jar config.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /config.jar" ]
EXPOSE 8762