# Yoav's Shoes - E-commerce Application

A full-stack e-commerce application for a shoe store built with Next.js frontend and Spring Boot backend.

## Project Structure

```
yoavsshoes/
├── yoavs_shoes_frontend/    # Next.js React frontend
├── yoavs_shoes_backend/     # Spring Boot Java backend
└── README.md               # This file
```

## Prerequisites

Before running this application, make sure you have the following installed:

### For Frontend

- **Node.js** (version 24 or higher)
- **npm** or **yarn** package manager

### For Backend

- **Java** (JDK 17 or higher)
- **Maven** (version 3.6 or higher)
- **PostgreSQL** (version 12 or higher)

## Getting Started

Follow these steps to run the application locally:

### 1. Frontend Setup (Run First)

Navigate to the frontend directory:

```bash
cd yoavs_shoes_frontend
```

Install dependencies:

```bash
npm install
# or
yarn install
```

Create environment configuration:

```bash
# Create .env.local file (if not exists)
cp .env.local.example .env.local
```

Edit `.env.local` and configure your environment variables:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
# Add other environment variables as needed
```

Start the development server:

```bash
npm run dev
# or
yarn dev
```

The frontend will be available at: **<http://localhost:3000>**

### 2. Backend Setup (Run Second)

Navigate to the backend directory:

```bash
cd yoavs_shoes_backend
```

#### Database Setup

1. **Install and start PostgreSQL**
2. **Create database:**

   ```sql
   CREATE DATABASE shoe_store_db;
   ```

3. **Create user:**

    Run this after connecting to the created database:

   ```sql
   CREATE USER shoe_store_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE shoe_store_db TO shoe_store_user;
   GRANT ALL PRIVILEGES ON SCHEMA public TO shoe_store_user;
   GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO shoe_store_user;
   ```

4. **Configure database connection** in `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/shoe_store_db
   spring.datasource.username=shoe_store_user
   spring.datasource.password=your_password
   ```

#### Build and Run Backend

Build the application:

```bash
./mvnw clean install
# On Windows:
mvnw.cmd clean install
```

Run the application:

```bash
./mvnw spring-boot:run
# On Windows:
mvnw.cmd spring-boot:run
```

The backend API will be available at: **<http://localhost:8080>**

## Application URLs

- **Frontend**: <http://localhost:3000>
- **Backend API**: <http://localhost:8080/api>
- **API Documentation (Swagger)**: <http://localhost:8080/swagger-ui.html>
- **API Examples**: <http://localhost:8080/api/docs/info>

## Default Test Accounts

The application comes with pre-configured test accounts:

### Admin Account

- **Email**: <admin@shoestore.com>
- **Password**: admin123

### Regular User Account

- **Email**: <user@example.com>
- **Password**: user1234

## Key Features

### Frontend Features

- **Product browsing** and search
- **User authentication** (login/register)
- **Shopping cart** functionality
- **Order management**
- **Admin dashboard** (for admin users)
- **Responsive design** with Tailwind CSS

### Backend Features

- **RESTful API** with Spring Boot
- **JWT-based authentication**
- **Role-based access control** (Admin/User)
- **Database persistence** with PostgreSQL
- **API documentation** with Swagger/OpenAPI
- **Comprehensive logging** and error handling

## Development Scripts

### Frontend Scripts

```bash
npm run dev          # Start development server
npm run build        # Build for production
npm run start        # Start production server
npm run lint         # Run ESLint
```

### Backend Scripts

```bash
./mvnw spring-boot:run    # Run development server
./mvnw clean install     # Build the application
./mvnw test              # Run tests
```

## Troubleshooting

### Common Issues

#### Frontend Issues

1. **Port 3000 already in use**:

   ```bash
   # Kill process using port 3000
   npx kill-port 3000
   # Or use different port
   npm run dev -- -p 3001
   ```

2. **Module not found errors**:

   ```bash
   # Clear npm cache and reinstall
   npm cache clean --force
   rm -rf node_modules package-lock.json
   npm install
   ```

#### Backend Issues

1. **Port 8080 already in use**:
   - Change port in `application.properties`:

   ```properties
   server.port=8081
   ```

2. **Database connection issues**:
   - Verify PostgreSQL is running
   - Check database credentials in `application.properties`
   - Ensure database exists

3. **Build failures**:

   ```bash
   # Clean and rebuild
   ./mvnw clean
   ./mvnw install -DskipTests
   ```

## Security Notes

- The application uses JWT tokens for authentication
- Admin endpoints are protected with role-based access control
- CORS is configured for frontend-backend communication
- Passwords are encrypted using BCrypt

## License

This project is for educational purposes as part of the Advanced Java Workshop course At The open university. It is not intended for commercial use.

---

**Note**: This application is designed for development and learning purposes. For production deployment, additional security measures and configurations should be implemented.
