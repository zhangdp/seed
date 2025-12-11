FROM eclipse-temurin:21
WORKDIR /app
EXPOSE 8080
ARG app_name=seed
ENV TZ=Asia/Shanghai \
    APP_NAME=${app_name} \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-server -Xms2g -Xmx2g -XX:MaxMetaspaceSize=512m"
COPY target/*.jar ${APP_NAME}.jar
CMD ["sh", "-c", "java $JAVA_OPTS -jar ${APP_NAME}.jar"]
