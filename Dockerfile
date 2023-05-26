FROM centos:7

ARG JAVA_VERSION=17.0.7
ARG JVM_PATH=/usr/lib/jvm

WORKDIR /tmp

RUN curl -OL https://download.oracle.com/java/17/archive/jdk-${JAVA_VERSION}_linux-x64_bin.tar.gz \
    && mkdir -p ${JVM_PATH} \
    && tar -zxvf jdk-${JAVA_VERSION}_linux-x64_bin.tar.gz -C ${JVM_PATH} \
    && rm -f jdk-${JAVA_VERSION}_linux-x64_bin.tar.gz

ENV LANG=C.UTF-8
ENV JAVA_HOME=${JVM_PATH}/jdk-${JAVA_VERSION}
ENV PATH=$PATH:$JAVA_HOME/bin

ENV APP_NAME=seed
ENV SPRING_PROFILE=prod
ENV JAVA_OPTS="-Xms1024m -Xmx2048m"

RUN java -version

EXPOSE 8080

COPY ./target/*.jar ./${APP_NAME}.jar

CMD java -Dspring.profiles.active=${SPRING_PROFILE} ${JAVA_OPTS} -jar ${APP_NAME}.jar


