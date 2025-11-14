# 🧩 Etapa 1: Compilación del backend con Maven y Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos el proyecto completo
COPY pom.xml .
COPY src ./src

# Compilamos el .jar (sin ejecutar tests)
RUN mvn clean package -DskipTests

# 🪶 Etapa 2: Imagen ligera para ejecución
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar el jar construido
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto del backend
EXPOSE 9090

# Variable por defecto (puede sobreescribirse en docker-compose)
ENV SPRING_PROFILES_ACTIVE=prod

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
