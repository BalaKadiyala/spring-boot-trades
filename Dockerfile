FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# 1. Copy Maven wrapper first
COPY mvnw .
COPY .mvn .mvn

# 2. Copy pom.xml
COPY pom.xml .

# 3. Pre-download dependencies (cached unless pom.xml changes)
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# 4. Now copy the rest of the source
COPY src ./src

# 5. Build the project
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=docker

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]