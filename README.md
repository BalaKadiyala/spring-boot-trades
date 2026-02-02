# 📘 spring-boot-trades

A Spring Boot 3.x application demonstrating:

- CSV upload and parsing
- JPA + Native SQL queries
- H2 **file‑based** database (persistent)
- Global request header validation (`X-Client-Id`)
- Swagger/OpenAPI documentation
- Optional Docker support

---

## 🚀 Requirements

- **Java 21**
- **Maven 3.8+**

---

## ▶️ Run the Application

```bash
mvn spring-boot:run
```

Application starts at:

```
http://localhost:8080
```

---

## 📄 API Documentation (Swagger UI)

```
http://localhost:8080/swagger-ui.html
```

All APIs require the header:

```
X-Client-Id: abc123
```

Swagger UI shows this header globally.

---

## 🗄️ H2 Database Console

```
http://localhost:8080/h2-console
```

### **H2 is file‑based (persistent)**
Your database is stored on disk, not in memory.

**JDBC URL:**
```
jdbc:h2:file:/data/tradesdb
```

**User:** `sa`  
**Password:** *(empty)*

### 📁 Database File Location

When running locally, H2 stores files under:

```
/data/tradesdb.mv.db
/data/tradesdb.trace.db
```

These files persist across application restarts.

---

## 🔄 Resetting the H2 Database

If you want a clean database:

1. Stop the application
2. Delete the files:

```
/data/tradesdb.mv.db
/data/tradesdb.trace.db
```

3. Start the application again — Spring Boot will recreate the schema.

---

## 📂 CSV Upload API

### **Bulk Insert Stock Records**

**POST** `/api/stock-data/bulk-insert`  
**Consumes:** `multipart/form-data`  
**Header:** `X-Client-Id: abc123`  
**Form field:** `file` (CSV file)

Example (curl):

```bash
curl -X POST "http://localhost:8080/api/stock-data/bulk-insert" \
  -H "X-Client-Id: abc123" \
  -F "file=@src/main/resources/sample-quarter.csv"
```
---
## 📊 Query APIs

### **1. Get records by stock (JPA)**

**GET** `/api/stock-data/{stock}`  
**Header:** `X-Client-Id: abc123`

Example:

```
GET /api/stock-data/AAPL
```

---

### **2. Get records by stock (Native SQL)**

**GET** `/api/stock-data/{stock}/native`  
**Header:** `X-Client-Id: abc123`

Example:

```
GET /api/stock-data/GOOG/native
```

---

### **3. Add a single stock record**

**POST** `/api/stock-data`  
**Header:** `X-Client-Id: abc123`  
**Body:** JSON

Example:

```json
{
  "stock": "AAPL",
  "quarter": 1,
  "year": 2020,
  "openPrice": 120.5,
  "closePrice": 130.2
}
```

---

## 🔐 Client ID Validation

All `/api/**` endpoints require:

```
X-Client-Id: abc123
```

Requests missing this header — or containing any other value — return:

```
401 Unauthorized
Invalid X-Client-Id
```

Validation is implemented using a custom `OncePerRequestFilter`.

---
## 🐳 Docker Support

### **Dockerfile**

A simple Dockerfile for packaging the Spring Boot app:

```dockerfile
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/spring-boot-trades.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build the image:

```bash
mvn clean package -DskipTests
docker build -t spring-boot-trades .
```

Run the container:

```bash
docker run -p 8080:8080 -v tradesdb:/data spring-boot-trades
```

### Why mount a volume?

Because your H2 database is stored at:

```
/data/tradesdb
```

Mounting a volume ensures persistence:

```
-v tradesdb:/data
```

---

## 🐳 docker-compose Support

Create a `docker-compose.yml`:

```yaml
services:
  trades-app:
    image: spring-boot-trades
    container_name: trades-app
    ports:
      - "8080:8080"
    volumes:
      - tradesdb:/data
    environment:
      - SPRING_DATASOURCE_URL=jdbc:h2:file:/data/tradesdb
      - SPRING_DATASOURCE_USERNAME=sa
      - SPRING_DATASOURCE_PASSWORD=

volumes:
  tradesdb:
```

Start with:

```bash
docker-compose up --build
```

---

## 📝 Notes

- CSV parsing uses **OpenCSV**.
- Swagger/OpenAPI is powered by **springdoc-openapi 2.5.0**.
- H2 is **file‑based**, so data persists across restarts.
- Lombok is used — ensure your IDE has Lombok enabled.
- Docker volume ensures persistent H2 storage.  