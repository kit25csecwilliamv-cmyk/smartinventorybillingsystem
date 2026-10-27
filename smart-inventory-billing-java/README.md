# Smart Inventory & Billing Management System

A beginner-friendly full-stack Java project for a small trading company selling electrical, electronic, and plumbing products.

## Features

- Dashboard with product count, stock units, low-stock count, and today's sales.
- Add, edit, delete, and search products.
- Automatic low-stock alerts.
- Create a bill with multiple products.
- Validate stock before sale and update stock automatically.
- Record sales and view recent bills.
- Responsive HTML/CSS/JavaScript frontend.
- Java 17, Spring Boot, Spring Data JPA, JDBC-backed persistence, and MySQL.

## Tech stack

- Java 17
- Spring Boot 3
- Spring Web / REST API
- Spring Data JPA
- MySQL
- HTML5, CSS3, JavaScript
- Maven

## Requirements

Install:
1. JDK 17 or newer
2. Maven 3.8+
3. MySQL 8+
4. Git (optional, for GitHub)

## Run locally

### 1. Create database

Open MySQL and run:

```sql
CREATE DATABASE smart_inventory;
```

### 2. Configure database credentials

Edit `src/main/resources/application.properties`, or set environment variables:

- `DB_URL` (default `jdbc:mysql://localhost:3306/smart_inventory?createDatabaseIfNotExist=true`)
- `DB_USERNAME` (default `root`)
- `DB_PASSWORD` (default blank)

Do not commit real passwords to GitHub. For a public repository, use environment variables or a local, untracked configuration file.

### 3. Start the app

From the project root:

```bash
mvn spring-boot:run
```

Open `http://localhost:8080` in your browser. Spring Boot serves the HTML, CSS, and JavaScript from `src/main/resources/static`.

## REST API

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/dashboard` | Dashboard metrics |
| GET | `/api/products` | List all products |
| GET | `/api/products?q=wire` | Search products |
| GET | `/api/products/low-stock` | List low-stock products |
| POST | `/api/products` | Add a product |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Delete a product |
| GET | `/api/sales` | Recent sales |
| POST | `/api/sales` | Create a sale and decrement stock |

### Example: add a product

```json
{
  "name": "LED Bulb 9W",
  "category": "Electrical",
  "purchasePrice": 70.00,
  "sellingPrice": 100.00,
  "stockQuantity": 40,
  "minStockLevel": 5
}
```

### Example: create a sale

```json
{
  "customerName": "Walk-in Customer",
  "items": [
    { "productId": 1, "quantity": 2 }
  ]
}
```

The backend calculates the total from the stored selling prices. It does not trust a total amount sent by the browser.

## Project structure

```text
smart-inventory-billing-java/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/com/annatraders/
        │   ├── SmartInventoryApplication.java
        │   ├── controller/
        │   ├── dto/
        │   ├── model/
        │   ├── repository/
        │   └── service/
        └── resources/
            ├── application.properties
            └── static/
                ├── index.html
                ├── styles.css
                └── app.js
```

## Important notes

- This is a college-project starter, not a production-ready POS system.
- Authentication/roles, GST rules, invoice PDF/printing, backups, audit logs, and supplier purchase screens can be added as future improvements.
- Before using in a real shop, add login/security, concurrency-safe stock updates, proper invoice/tax handling, tests, and database backups.
- Product and sale data are persisted in MySQL; do not use demo credentials for a public deployment.
