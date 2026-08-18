# BuyFurn Backend - Spring Boot Application

Welcome to the backend server for **BuyFurn**, an online platform specializing in quality, comfortable furniture that blends style and functionality. This project is built using Spring Boot, Java 17, and PostgreSQL, with features supporting user profiles, shopping carts, product cataloging, order creation, automated OTP email generation, and payment processing with Razorpay.

---

## 🛠️ Technology Stack

* **Framework:** Spring Boot (v3.3.1)
* **Java Version:** 17
* **Build Tool:** Maven
* **Database:** PostgreSQL
* **ORM / JPA:** Spring Data JPA with Hibernate
* **Security:** Spring Security (HTTP Basic Authentication)
* **API Documentation:** Springdoc OpenAPI / Swagger UI (accessible at `/swagger-ui.html`)
* **Payment Integration:** Razorpay Java SDK
* **Utility Libraries:** Lombok, Jackson, org.json
* **Email System:** Spring Boot Starter Mail (Gmail SMTP)
* **Containerization:** Docker (Multi-stage build)

---

## 📁 Project Directory Structure

```
buyfurnbackend/
├── src/
│   ├── main/
│   │   ├── java/com/buyfurn/Buyfurn/
│   │   │   ├── configuration/  # Security, User Details, and Mail configuration
│   │   │   ├── controller/     # REST Endpoints
│   │   │   ├── model/          # JPA Entities and DTOs
│   │   │   ├── repository/     # Data Access Objects (Spring Data JPA)
│   │   │   └── service/        # Business Logic Layers
│   │   └── resources/          # Configuration files (application.properties)
│   └── test/                   # Unit & Integration Tests
├── DockerFile                  # Multi-stage Docker deployment config
├── mvnw / mvnw.cmd             # Maven wrapper scripts
└── pom.xml                     # Maven project dependencies
```

---

## 🛢️ Database Configuration & Models

The application connects to a PostgreSQL database named `buyfurn`.
* **DDL Strategy:** `spring.jpa.hibernate.ddl-auto=update` (automatically synchronizes Java entities with the database tables).
* **Open-in-View:** Disabled (`spring.jpa.open-in-view=false`) for optimized database sessions.

### Key Entities ([model/](file:///src/main/java/com/buyfurn/Buyfurn/model/))

1. **[User.java](file:///src/main/java/com/buyfurn/Buyfurn/model/User.java):** Contains user credentials, roles, address, and profile image.
2. **[Address.java](file:///src/main/java/com/buyfurn/Buyfurn/model/Address.java):** Embeddable component for billing/shipping addresses (`address`, `pincode`, `city`, `state`).
3. **[Product.java](file:///src/main/java/com/buyfurn/Buyfurn/model/Product.java):** Details furniture items including `title`, `description`, `price`, `warranty`, `category`, `color`, `material`, `seatingCapacity`, `weight`, `careAndMaintenance`, `stockStatus`, and list of product images.
4. **[ProductImages.java](file:///src/main/java/com/buyfurn/Buyfurn/model/ProductImages.java) / [UserImage.java](file:///src/main/java/com/buyfurn/Buyfurn/model/UserImage.java):** Handlers for storing file image byte arrays (`picByte`) and content types.
5. **[Cart.java](file:///src/main/java/com/buyfurn/Buyfurn/model/Cart.java):** Connects a product to a user with a specific purchase quantity.
6. **[OrderDetails.java](file:///src/main/java/com/buyfurn/Buyfurn/model/OrderDetails.java):** Stores billing user info, shipping address, transaction ID, order status, order amount, and product references.
7. **[TransactionDetails.java](file:///src/main/java/com/buyfurn/Buyfurn/model/TransactionDetails.java):** Model containing payment identifiers from Razorpay (`orderId`, `currency`, `amount`, `key`).

---

## 🔒 Security & CORS

* **Configuration File:** [SecurityConfiguration.java](file:///src/main/java/com/buyfurn/Buyfurn/configuration/SecurityConfiguration.java)
* **Authentication Method:** HTTP Basic Authentication (`http.httpBasic(Customizer.withDefaults())`).
* **CSRF:** Disabled.
* **Role-Based Authorization:**
  * Admin endpoints `/api/admin/**` require the `ADMIN` role.
  * User endpoints `/api/user/**` require authentication.
  * Public endpoints `/api/**` (e.g., registration, logins, view products) are open.
* **CORS Mapping:** Configured to accept incoming requests from all origins (`*`) and HTTP methods (`GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`) to allow communication with frontend projects.

---

## 📮 Key API Endpoints

### 1. User Management ([UserController.java](file:///src/main/java/com/buyfurn/Buyfurn/controller/UserController.java))
* `POST /api/register` - Create a new user account.
* `POST /api/updatepassword` - Change the authenticated user's password.
* `GET /api/login` - Authenticate the user and retrieve their profile data.
* `POST /api/generate-otp` - Generate an OTP for verification.
* `POST /api/verify-otp` - Validate user's OTP input.
* `POST /api/user/updateuser` - Update profile details and upload image.

### 2. Product Management ([ProductController.java](file:///src/main/java/com/buyfurn/Buyfurn/controller/ProductController.java))
* `POST /api/admin/addproduct` - Upload a new product with image attachments (Admin only).
* `POST /api/admin/updateproduct` - Edit product specifications and images (Admin only).
* `DELETE /api/admin/deletebyid/{id}` - Delete product by ID (Admin only).
* `GET /api/getallproducts` - Retrieve products with pagination and search/category filters.
* `GET /api/getbyid/{id}` - Retrieve details of a single product.
* `GET /api/latest` - Fetch newly added products.

### 3. Shopping Cart ([CartController.java](file:///src/main/java/com/buyfurn/Buyfurn/controller/CartController.java))
* `GET /api/user/addToCart/{productId}/{quantity}` - Add a furniture item to cart.
* `GET /api/user/getCartDetails` - Fetch all items in user's cart.
* `DELETE /api/user/deleteCartProduct/{cartId}` - Remove an item from the cart.

### 4. Orders & Payments ([OrderDetailsController.java](file:///src/main/java/com/buyfurn/Buyfurn/controller/OrderDetailsController.java))
* `POST /api/user/placeOrder/{isSingleProductCheckout}` - Complete checkout process.
* `GET /api/user/createTransaction/{amount}` - Connect with Razorpay API to generate a transaction signature.
* `GET /api/user/myOrders` - Retrieve order history for the active user.
* `GET /api/admin/allOrders/{status}` - Retrieve all system orders matching a status filter (Admin only).
* `PUT /api/admin/markAsDelivered/{orderId}` - Mark order delivery status as complete (Admin only).

---

## 🔌 Integrations

### 1. Razorpay Payment Gateway
Integrated within [OrderDetailService.java](file:///src/main/java/com/buyfurn/Buyfurn/service/OrderDetailService.java) using the official `com.razorpay:razorpay-java` SDK. It initializes a Razorpay client, builds orders dynamically, and returns transaction credentials.

### 2. Spring Email Service
Integrated within [EmailService.java](file:///src/main/java/com/buyfurn/Buyfurn/service/EmailService.java). SMTP mail sending details (Gmail server `smtp.gmail.com:587`, TLS enabled, auth credentials) are configured as a `@Bean` in [SecurityConfiguration.java](file:///src/main/java/com/buyfurn/Buyfurn/configuration/SecurityConfiguration.java).

---

## ⚙️ Running the Project

### Environment Profiles
1. **Dev Profile (`dev`):** Runs on port `8080`, database pointing to `jdbc:postgresql://localhost:5432/buyfurn`. Allowed origins: `http://localhost:4200/` (Angular default).
2. **Prod Profile (`prod`):** Runs on dynamic port (`${PORT:8080}`), database pointing to external Railway PostgreSQL cluster. Allowed origins configured dynamically via environment variables.

### Build and Run with Maven Wrapper
```bash
# Build the project
./mvnw clean package

# Run the Spring Boot Application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Run using Docker
```bash
# Build docker image
docker build -t buyfurn-backend .

# Run docker container
docker run -p 8080:8089 buyfurn-backend
```
