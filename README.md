# Crewmeister Test Assignment - Java Backend Developer

## Approach
In this approach, I used couple of best practices that are common for spring boot to increase the quality of the code
1. Testing. I covered code with tests using Junit and Mocking
2. Container-based approach. I wrapped the code into container so that it's isolated in its environment
3. API versioning. I used version to give flexibility for future changes
4. Logging. In the exception and incoming requests, I used logging
5. Dependency Injection. I used lombok to inject the dependencies using constructor-based approach
6. Web-client. I used webclient instead of resttemplate so that application is asynchronous, non-blocking I/O to increase concurrency
7. Swagger. I used swagger to document all APIs for better maintenance
8. Actuator. I used actuator to monitor its health or to provide the information

## Instructions
1. docker build -t currency-service:latest .

2. docker run -p 8080:8080 currency-service:latest