FROM maven:3.9.6-amazoncorretto-21-al2023 AS build 
WORKDIR /app 
COPY . . 
RUN mvn clean package 
FROM tomcat:9.0.84-jdk8-corretto 
COPY --from=build /app/target/project-0.1.war /usr/local/tomcat/webapps/ 

# docker build --progress=plain -t project .
# docker run -p 8080:8080 project