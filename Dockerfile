# Use a lightweight Java 17 Runtime
FROM eclipse-temurin:17-jre-alpine

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
# Make sure this matches the version in your pom.xml
COPY target/realtime-chat-system-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]