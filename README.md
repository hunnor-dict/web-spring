[![Build Status](https://travis-ci.org/hunnor-dict/web-spring.svg?branch=master)](https://travis-ci.org/hunnor-dict/web-spring)

# HunNor Dictionary - Web Spring

The web application of the dictionary at https://dict.hunnor.net.

## Usage

The dictionary is a Spring Boot application. To run the application locally, build the source code with Maven (`mvn package`) and simply run the fat JAR file with `java -jar`.

Configuration parameters are supplied on the command line. For example, to specify the Solr server URL, use `--net.hunnor.dict.client.search.solr.url=http://solr:8983/solr`.

For a list of available parameters, other than those specified by Spring Boot, and their default values, see `src/main/resources/application.properties`.

## Docker

The repository includes a `Dockerfile` for creating an image with Java 8 and the web application installed. The official `hunnordict/web-spring` image is built with this file.

To supply configuration parameters, simply append the parameters to the `docker run` command:

    docker run \
    -p 8080:8080 \
    --net.hunnor.dict.client.search.solr.url=http://solr:8983/solr
