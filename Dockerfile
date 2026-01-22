FROM eclipse-temurin:21
WORKDIR /app
EXPOSE 8080
ARG app_name=seed
ENV TZ=Asia/Shanghai \
    APP_NAME=${app_name} \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-server -XX:+UseZGC -XX:+ZGenerational -XX:MaxRAMPercentage=75"
COPY target/*.jar ${APP_NAME}.jar
CMD ["sh", "-c", "java $JAVA_OPTS -jar ${APP_NAME}.jar"]
