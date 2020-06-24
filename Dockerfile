FROM openjdk:11-jre-slim
LABEL maintainer="starcraft66@gmail.com"

ENV NOCOM_OPTIONS=''

ADD build/libs/nocom-*.jar /opt/nocom-http/nocom.jar

CMD java -jar /opt/nocom-http/nocom.jar $NOCOM_OPTIONS
