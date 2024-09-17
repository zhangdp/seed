FROM eclipse-temurin:21-jre
WORKDIR /app
EXPOSE 8080
ARG app_name=seed
ENV TZ=Asia/Shanghai APP_NAME=${APP_NAME} JVM_XMS=2048m JVM_XMX=2048m JVM_MS=128m JVM_MMS=384m JVM_OPT_EXT=""
WORKDIR /app
COPY target/*.jar ${APP_NAME}.jar
CMD java -jar -Xms${JVM_XMS} -Xmx${JVM_XMX} -XX:MetaspaceSize=${JVM_MS} -XX:MaxMetaspaceSize=${JVM_MMS} ${JVM_OPT_EXT} ${APP_NAME}.jar
