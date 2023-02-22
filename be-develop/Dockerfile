# Docker Build Stage 
FROM registry.hpicorp.tk/base/maven:3.8.2-mdms-cache as build-stage
LABEL maintainer="NeroWu <amdcctan@gmail.com>"
WORKDIR /usr/src/mymaven
COPY . .
RUN mvn clean package -Dmaven.test.skip=true

# Production Stage
FROM openjdk:8-jdk-oraclelinux7
ENV ENV=""
RUN groupadd -g 2000 tomcat
RUN useradd -u 2000 -g 2000 -r -s /sbin/nologin tomcat
RUN mkdir -p /opt/sims/logs \
    && mkdir -p /opt/sims/upload \
    && chown -R tomcat:tomcat /opt/sims
COPY --from=build-stage /usr/src/mymaven/target/surgicalInstrument.jar /opt/sims
RUN chown tomcat:tomcat /opt/sims/surgicalInstrument.jar
VOLUME [ "/opt/sims/upload" ]
VOLUME [ "/opt/sims/logs" ]
USER tomcat:tomcat
EXPOSE 8080 
CMD java -jar -Xms2048m -Xms2048m -Dspring.profiles.active=${ENV} /opt/sims/surgicalInstrument.jar
STOPSIGNAL SIGQUIT