# Etapa 1: build con Maven y JDK 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos los archivos de configuración primero (para cachear dependencias)
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copiamos el código fuente y construimos
COPY src ./src
RUN mvn -q -DskipTests package

# Etapa 2: imagen ligera para correr la app
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiamos el jar generado
COPY --from=build /app/target/*.jar app.jar

# Render expone la variable PORT, tu app la usa con server.port=${PORT:8080}
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
