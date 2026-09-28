# FarmStock — Farmer's Produce Inventory Tracker

## Project Overview
Small farmers harvest produce in batches but often track inventory and sales on paper. This makes it difficult to know how much produce is currently available for sale and can lead to selling more produce than is actually available. 
FarmStock provides a REST API that allows a farmer to manage crops, record harvest batches, record sales, calculate current inventory, prevent overselling, and generate revenue reports.

## Features
- Manage Crops (CRUD)
- Record Harvest Batches
- Record Sales
- Calculate Current Inventory
- Prevent Overselling
- Generate Revenue Reports
- Dashboard Summary

## Technology Stack
- Java 21
- Spring Boot
- Maven
- MySQL
- Spring Data JPA
- Hibernate
- Jakarta Validation
- Springdoc OpenAPI / Swagger UI
- JUnit 5
- Mockito

## Architecture
Controller ↓ Service ↓ Repository ↓ MySQL

## Database Relationships
- Crop 1:N HarvestBatch
- Crop 1:N Sale

## Stock Formula
`Current Stock = Total Harvested - Total Sold`

## Overselling Rule
The `SaleService` validates available stock before saving a sale.

## Setup
1. Install Java 21.
2. Install Maven.
3. Install MySQL.
4. Create database `farmstock_db`.
5. Configure credentials in `application.properties`.
6. Run the application.

## Run
```bash
mvn clean install
mvn spring-boot:run
```

## Tests
```bash
mvn clean test
```

## Swagger
http://localhost:8080/swagger-ui.html

## Health
http://localhost:8080/actuator/health

## Postman
Import the `postman/FarmStock.postman_collection.json` file into Postman.

## Docker
```bash
docker-compose up --build -d
```
