FROM eclipse-temurin:21
WORKDIR /app
EXPOSE 8080
ARG app_name=seed
ENV TZ=Asia/Shanghai APP_NAME=${APP_NAME} JVM_XMX=2048m JVM_OPT_EXT=""
WORKDIR /app
COPY target/*.jar ${APP_NAME}.jar
CMD java -jar -Xms${JVM_XMX} -Xmx${JVM_XMX} -XX:MetaspaceSize=192m -XX:MaxMetaspaceSize=384m -XX:+UseG1GC -XX:G1NewSizePercent=30 -XX:+AlwaysPreTouch ${JVM_OPT_EXT} ${APP_NAME}.jar
