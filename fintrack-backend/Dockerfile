# 1️⃣ Imagen base con Maven y JDK 17 para compilar
FROM maven:3.9.6-eclipse-temurin-17 AS build

# 2️⃣ Crea un directorio en el contenedor
WORKDIR /app

# 3️⃣ Copia pom.xml y descarga dependencias primero (para aprovechar la cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# 4️⃣ Copia el resto del código fuente y compila el proyecto
COPY src ./src
RUN mvn clean package -DskipTests

# 5️⃣ Nueva imagen más ligera para producción
FROM eclipse-temurin:17-jdk-jammy

# 6️⃣ Directorio donde se ejecutará la app
WORKDIR /app

# 7️⃣ Copia el .jar generado desde la fase de build
COPY --from=build /app/target/*.jar app.jar

# 8️⃣ Expone el puerto (ajústalo si tu aplicación usa otro)
EXPOSE 8080

# 9️⃣ Comando para ejecutar tu aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
