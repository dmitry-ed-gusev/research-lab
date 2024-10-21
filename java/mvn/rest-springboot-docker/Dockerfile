# base image for building this one
FROM jeanblanchard/java:8

# current image author
MAINTAINER Dmitrii Gusev

# label - image metadata
LABEL type="REST"

# building image steps
COPY target/rest-springboot-docker-1.0.jar rest-springboot-docker-1.0.jar
CMD java -jar rest-springboot-docker-1.0.jar

# expose port(s) to external world (other containers)
EXPOSE 8080