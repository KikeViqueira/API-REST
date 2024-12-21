# Movie Database Management System

## Project Overview

This project focuses on the development of a **Movie Database Management System** using modern technologies. The system leverages **MongoDB**, a NoSQL database, for data persistence and **Spring Boot** for building a robust Java-based backend. It incorporates a modular structure to represent and manage movie data, including titles, directors, and cast members, while showcasing the flexibility of MongoDB to store and retrieve data efficiently.

This project has been developed collaboratively in pairs, showcasing teamwork and collaboration skills.

---

## Features

- **MongoDB Integration**: 
  - Creation and management of collections and documents.
  - Advanced queries using JSON-like syntax to retrieve data.
  - Flexibility in handling data schema.

- **Spring Boot Framework**:
  - Dependency injection using annotations like `@Autowired` and `@Service`.
  - Repository pattern with `MongoRepository` for seamless database interactions.
  - Modular design with clear separation of concerns (`Controller`, `Service`, `Repository`).

- **Java Object Modeling**:
  - Data modeling with POJOs for movies, directors, and cast.
  - Implementation of `equals()`, `hashCode()`, and `toString()` methods for data consistency.
  - Use of the Builder Pattern for clean and readable code.

- **Command Line Integration**:
  - CommandLineRunner for executing tasks directly from the terminal.
  - Simple logging service for debugging and tracing.

---

## Technologies Used

1. **Programming Language**: 
   - Java 8+
   
2. **Frameworks and Tools**:
   - **Spring Boot**: A powerful Java framework for building enterprise applications.
   - **Gradle**: Dependency management and project build system.

3. **Database**:
   - **MongoDB**: A NoSQL database used for flexible and efficient data management.
   - **MongoDB Compass**: GUI client for database management.

4. **IDE**:
   - **IntelliJ IDEA Ultimate Edition**: Used for seamless Java development.

5. **JavaScript Runtime**:
   - **Node.js (via NVM)**: Installed as a prerequisite for UI development.

---

## Setup Instructions

### Prerequisites
- Java 8 or higher.
- MongoDB installed and running on `localhost:27017`.
- IntelliJ IDEA (Ultimate Edition recommended).

### Installation Steps

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-repository-name.git
   cd your-repository-name

2. **Run MongoDB: Ensure MongoDB is running locally on port 27017.**

3. **Start the Application:**

- Open the project in IntelliJ IDEA.
- Run the main() method in the Application class.
- Verify the logs to ensure a successful connection to MongoDB.
