FROM eclipse-temurin:21-jre
WORKDIR /app
EXPOSE 8080
ARG jar_file=target/*.jar
ARG app_name=seed
ENV TZ=Asia/Shanghai APP_NAME=${app_name} JAVA_OPTS="-server -Xms2048m -Xmx2048m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m"
COPY ${jar_file} ${APP_NAME}.jar
CMD java -jar ${JAVA_OPTS} ${APP_NAME}.jar
