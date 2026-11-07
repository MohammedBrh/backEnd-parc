FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q clean package -DskipTests \
 && ls -la target \
 && cp "$(ls target/*.jar target/*.war 2>/dev/null | grep -v original | head -1)" /app/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/app.jar app.jar
ENV JAVA_OPTS="-Xmx350m -Xss512k"
EXPOSE 8080
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]