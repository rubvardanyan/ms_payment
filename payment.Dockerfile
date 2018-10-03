FROM tomcat:8.0-jre8
RUN rm -rf /usr/local/tomcat/webapps/ROOT/* && \
    rm -rf /usr/local/tomcat/webapps/docs && \
    rm -rf /usr/local/tomcat/webapps/examples && \
    rm -rf /usr/local/tomcat/webapps/host-manager && \
    rm -rf /usr/local/tomcat/webapps/manager
COPY api/api_internal/api_internal_rest/target/api_internal_rest-1.0/ /usr/local/tomcat/webapps/ROOT/
EXPOSE 8080
EXPOSE 50000
CMD ["catalina.sh", "run"]