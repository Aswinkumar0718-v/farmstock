# FarmStock - Project Summary

## Project Overview
FarmStock is a Java Spring Boot backend application designed to help farmers track their produce inventory efficiently.

## Problem Statement
Small farmers often rely on paper records for inventory, which can lead to confusion about available stock and accidental overselling.

## Solution
FarmStock offers a REST API to record harvest batches and sales, automatically calculating real-time inventory levels to prevent overselling.

## Technology Stack
- Java 21
- Spring Boot, Spring Web, Spring Data JPA
- MySQL
- Maven
- JUnit 5 & Mockito

## Architecture
Layered architecture: Controller -> Service -> Repository -> MySQL.

## Database Design
- `crop`
- `harvest_batch`
- `sale`

## Entity Relationships
- `Crop` 1:N `HarvestBatch`
- `Crop` 1:N `Sale`

## Core Business Rules
- **Stock Calculation**: Current Stock = Total Harvested Quantity - Total Sold Quantity
- **Overselling Prevention**: A sale must never exceed the current available stock.

## REST APIs
- `/api/crops` (CRUD)
- `/api/harvests` (Manage Harvests)
- `/api/sales` (Manage Sales)
- `/api/stock` (View Inventory)
- `/api/reports/revenue` (Revenue Reports)
- `/api/dashboard` (Dashboard Stats)

## Validation
Jakarta Validation ensures data integrity (e.g., positive quantities, valid dates, non-blank names).

## Exception Handling
A `@RestControllerAdvice` handles exceptions like `ResourceNotFoundException` and `InsufficientStockException`.

## Testing
Comprehensive unit testing using JUnit 5 and Mockito, testing business rules like the overselling prevention logic.

## Dashboard
A simple static HTML/JS dashboard consumes the APIs to display real-time metrics.

## Future Enhancements
- User authentication and multi-tenant support for multiple farmers.
- More detailed reporting metrics.

---

## How to Explain This Project in a Viva / Review

### Q1. What problem does FarmStock solve?
**Answer**: It replaces paper-based produce inventory tracking with a centralized REST API that records harvests and sales and calculates current stock automatically.

### Q2. What are the main entities?
**Answer**: Crop, HarvestBatch, Sale.

### Q3. What is the relationship?
**Answer**: One Crop has many HarvestBatch records. One Crop has many Sale records.

### Q4. How is stock calculated?
**Answer**: Current Stock = Total Harvested Quantity - Total Sold Quantity

### Q5. Where is the overselling rule implemented?
**Answer**: SaleService. The service calculates available stock before saving a sale.

### Q6. What happens when a farmer tries to sell more than available?
**Answer**: The service throws `InsufficientStockException`. The Sale is not saved. The global exception handler returns a clear HTTP 400 response.

### Q7. Why not simply store stock in Crop?
**Answer**: Because stock is derived from transaction history. If stock is stored independently, it can become inconsistent with the actual harvest and sale records.

### Q8. Why use BigDecimal?
**Answer**: For accurate decimal calculations involving quantities and monetary values.

### Q9. Why use DTOs?
**Answer**: DTOs separate the API contract from the database entities and prevent exposing JPA relationships directly.

### Q10. Why use a service layer?
**Answer**: The service layer contains business rules and keeps controllers focused on HTTP handling and repositories focused on data access.

### Q11. Why use @Transactional for sales?
**Answer**: The stock validation and sale creation form one business operation that should execute consistently.

### Q12. How is revenue calculated?
**Answer**: Revenue = Quantity Sold × Price Per Unit. Only sales inside the requested date range are included.
