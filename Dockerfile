FROM eclipse-temurin:25-jre
WORKDIR /app
EXPOSE 8080
ARG app_name=seed
ENV TZ=Asia/Shanghai \
    APP_NAME=${app_name} \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-server -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m -XX:InitialRAMPercentage=75 -XX:MaxRAMPercentage=75 -XX:+AlwaysPreTouch"
COPY target/*.jar ${APP_NAME}.jar
CMD ["sh", "-c", "exec java $JAVA_OPTS -jar ${APP_NAME}.jar"]
