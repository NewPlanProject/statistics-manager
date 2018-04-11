FROM registry-vpc.cn-beijing.aliyuncs.com/iiotprd/jdk8:latest
VOLUME /tmp
COPY  Shanghai  /etc/localtime
ADD @project.build.finalName@.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]