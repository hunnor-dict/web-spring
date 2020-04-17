![Build](https://github.com/hunnor-dict/web-spring/workflows/Build/badge.svg)
[![Known Vulnerabilities](https://snyk.io/test/github/hunnor-dict/web-spring/badge.svg)](https://snyk.io/test/github/hunnor-dict/web-spring)

The web application of the dictionary at https://dict.hunnor.net.

# Usage

The dictionary is a Spring Boot application. To run the application locally, package the source code with Maven and simply run the fat JAR file with `java -jar`.

Configuration parameters are supplied on the command line. For example, to specify the Solr server URL, use `--net.hunnor.dict.client.search.solr.url=http://solr:8983/solr`.

For a list of available parameters, other than [those specified by Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html), and their default values, see `src/main/resources/hunnor.properties`.

To run the application with Docker, either build the image locally or use the official hunnordict/web-spring image. To supply configuration parameters, simply append the parameters to the `docker run` command:

`docker run --name web-spring --publish 8080:8080 hunnordict/web-spring --net.hunnor.dict.client.search.solr.url=http://solr:8983/solr`
