FROM openjdk:17
EXPOSE 1993
ADD target/income-details.jar income-details.jar
ENTRYPOINT ["java","-jar","income-details.jar"]