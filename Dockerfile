FROM tomcat:9.0
EXPOSE 8080
ADD target/vilkipalki.war /usr/local/tomcat/webapps
CMD catalina.sh run