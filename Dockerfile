FROM eclipse-temurin:17-jre
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV TZ=Asia/Shanghai JAVA_OPTS="-Xms1024m -Xmx2048m -Dspring.profiles.active=prod"
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
