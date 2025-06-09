FROM eclipse-temurin:17-jdk-jammy

COPY . /app
WORKDIR /app

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "target/apiBasica-0.0.1-SNAPSHOT.jar"]
