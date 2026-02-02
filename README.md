# spring-boot-trades

Requirements:
- Java 21
- Maven 3.8+

Run:
mvn spring-boot:run

Swagger UI:
http://localhost:8080/swagger-ui.html

H2 Console:
http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:tradesdb

User: sa
Password: (empty)

Upload endpoint:
POST /api/quarter/upload
Form field: file (multipart/form-data)

Sample CSV:
src/main/resources/sample-quarter.csv

Notes:
- Upload returns UploadResult with persistedCount and sampleIds.
- Uses Lombok; enable Lombok plugin in your IDE.