# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.timetracker-api.timetracker-api' is invalid and this project uses 'com.timetracker.timetrackerapi' instead.

# Getting Started
## Default port
8083

## Endpoints
### 1. Save a tracker record
`$ curl -X POST --data "email=test1@gmail.com" --data "start=2021-07-07T09:00" --data "end=2021-07-07T17:00" http://localhost:8083/api/save`

### 2. Get tracker records based on given Email ID
`$ curl http://localhost:8083/api/details?email=test1@gmail.com`

### 3. Get the top 10 tracker records
`$ curl http://localhost:8083/api/details`

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.2/maven-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.5.2/reference/htmlsingle/#using-boot-devtools)

