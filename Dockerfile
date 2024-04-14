FROM maven:3-eclipse-temurin-20 AS build
COPY ./ /home/app
RUN cd /home/app && mvn clean package -Dmaven.test.skip=true
FROM eclipse-temurin:20-alpine
COPY --from=build /home/app/target/deliverylist-0.0.1-SNAPSHOT.jar /usr/local/etc/demo.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/etc/demo.jar"]
