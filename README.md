
# Employee Salary Management System

A full-stack web application for managing employee records, salaries, and department information.

Built with Spring Boot, Angular, and PostgreSQL.

## Tech Stack

- **Backend:** Java 21, Spring Boot, Spring Data JPA, Maven
- **Frontend:** Angular, TypeScript, HTML, CSS
- **Database:** PostgreSQL
- **API:** RESTful APIs
- **Version Control:** Git

## Features

- Create new employee records
- View all employees
- Update employee information
- Delete employee records
- Search employees by name, email, or department
- Filter employees by department
- Sort employees by salary
- Paginate employee records
- View total employees, total salary, and average salary
- Validate employee information
- Display meaningful error and success messages

## Project Structure

```text
.
├── demo/                    # Spring Boot backend
├── salary-management-ui/    # Angular frontend
├── README.md
└── .gitignore
```

## Prerequisites

Install the following:

- Java 21
- Node.js and npm
- PostgreSQL
- Git

## Database Setup

Create a PostgreSQL database named:

```sql
CREATE DATABASE salary_management;
```

The backend uses these database settings:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/salary_management
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
```

Set the `DB_PASSWORD` environment variable to your local PostgreSQL password before starting the backend.

## Run the Backend

Open a terminal in the `demo` directory.

On Windows PowerShell:

```powershell
$env:DB_PASSWORD = Read-Host "Enter PostgreSQL password"
.\mvnw.cmd spring-boot:run
```

The backend runs at:

http://localhost:8080

## Run the Frontend

Open a separate terminal in the `salary-management-ui` directory.

```bash
npm install
npm start
```

Open the application at:

http://localhost:4200

The Angular development server proxies API requests to the Spring Boot backend.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/employees | Retrieve employees |
| GET | /api/employees/{id} | Retrieve an employee |
| POST | /api/employees | Create an employee |
| PUT | /api/employees/{id} | Update an employee |
| DELETE | /api/employees/{id} | Delete an employee |

## Testing

Run backend tests from the `demo` directory:

```powershell
.\mvnw.cmd test
```

The tests require the configured PostgreSQL database and `DB_PASSWORD` environment variable.

## Production Build

Build the Angular application:

```bash
npm run build
```

The production files are generated in the frontend's `dist` directory.

## Validation and Error Handling

The application validates required employee information, email formatting, and positive salary values.

The backend also handles invalid requests and missing employee records with meaningful error responses.

## Notes

- Employee data is stored in PostgreSQL.
- The application supports server-side pagination, searching, department filtering, and salary sorting through Spring Boot REST APIs. The Angular frontend sends query parameters to the backend and displays paginated results returned from PostgreSQL.
- Database credentials are supplied through an environment variable rather than committed to source control.

## Video Demo

Watch the deployed Employee Salary Management System in action:

[Watch Demo Video](https://drive.google.com/file/d/1LOY8mwXuQ8wAuJoKAzN8QT2AudgUpUr8/view?usp=sharing)