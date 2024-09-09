# Connection Carousel

Connection Carousel is a load balancing application built using Java and Spring Boot. The application allows for dynamic management of server instances and load balancing strategies through a web-based control panel. It was developed using Test-Driven Development (TDD) principles to ensure a robust and well-tested codebase.

## Table of Contents

- [Project Overview](#project-overview)
- [TDD Approach](#tdd-approach)
- [Functionalities](#functionalities)
    - [Load Balancing Strategies](#load-balancing-strategies)
    - [Server Instances Management](#server-instances-management)
- [Getting Started](#getting-started)


## Project Overview

Connection Carousel is designed to manage and distribute traffic across multiple server instances using various load balancing strategies. The application supports adding, removing, and toggling the active state of server instances and strategies. The strategies include Round Robin and other load balancing algorithms that can be extended or modified as needed.

## TDD Approach

The development of Connection Carousel followed a strict Test-Driven Development (TDD) methodology:

1. **Red-Green-Refactor Cycle**: Each feature was developed by first writing a failing test (Red), then writing just enough code to pass the test (Green), and finally refactoring the code for optimization (Refactor).

2. **Incremental Development**: Features were added incrementally, with tests driving the development. This ensured that each feature was fully tested before moving on to the next.

3. **Focus on Clean Code**: By writing tests first, the code remained clean, modular, and easy to maintain. Refactoring was done continuously to improve the code structure without breaking functionality.

4. **Comprehensive Test Coverage**: The application has unit tests covering the core logic of load balancing strategies, server pool management, and controller actions.

## Functionalities

### Load Balancing Strategies

- **Round Robin**: Distributes requests evenly across all active instances.
- **Other Strategies**: Easily extendable to include additional load balancing strategies.

### Server Instances Management

- **Add/Remove Instances**: Dynamically add or remove server instances from the pool.
- **Toggle Instance State**: Activate or deactivate server instances as needed.
- **Health Checks**: Perform health checks on all instances to ensure they are up and running.

## Getting Started

To run Connection Carousel, you need to have Java and Maven installed on your system. The application is built using Spring Boot, which provides an embedded Tomcat server for running the application.

### Running the Application

To run the application:

```bash
mvn clean package
docker build -t connection-carousel .
docker run -p 8080:8080 connection-carousel
```


The application will start on http://localhost:8080

To access the control panel http://localhost:8080/admin/control-panel

