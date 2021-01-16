FROM adoptopenjdk/openjdk11:slim
LABEL maintainer="starcraft66@gmail.com"

ADD build/libs/nocom-*.jar /opt/nocom-http/nocom.jar

ENTRYPOINT ["java", "-jar", "/opt/nocom-http/nocom.jar"]
