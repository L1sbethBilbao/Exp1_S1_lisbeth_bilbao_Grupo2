# Etapa 1: compilar con Maven
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY src src

RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Etapa 2: imagen liviana para ejecutar
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/target/inscripcion-cursos-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
