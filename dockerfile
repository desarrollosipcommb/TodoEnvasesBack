# Paso 1: Compilar la aplicación
FROM maven:3.8.8-eclipse-temurin-8 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Paso 2: Crear la imagen de ejecución
FROM eclipse-temurin:8-jre
WORKDIR /app

# Copiamos el archivo .war y lo nombramos app.war
COPY --from=build /app/target/*.war app.war

EXPOSE 8080

# EJECUTAMOS app.war (Spring Boot permite ejecutar WARs como si fueran JARs)
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.war"]