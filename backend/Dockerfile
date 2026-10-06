# Stage 1: Build JAR with Maven
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# Stage 2: Minimal Runtime Image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create upload directory
RUN mkdir -p uploads/resumes

COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
