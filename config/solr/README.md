Installing Solr in Tomcat 7
===========================

Installation instructions for Solr 4.8.1 running in Tomcat 7. Taken from the installation instructions [found here](http://stackoverflow.com/questions/23503116/cant-get-solr-4-8-working-with-tomcat-7-and-ubuntu-12-04).

- Download Solr

        wget http://mirror.nus.edu.sg/apache/lucene/solr/4.8.1/solr-4.8.1.tgz

- Untar and copy solr to /opt

        1. tar -zxvf solr-4.8.1.tgz
        2. mv solr-4.8.1 /opt
        3. ln -s /opt/solr-4.8.1 /opt/solr

- Copy solr.war to solr HOME folder
        
        cp /opt/solr-4.8.1/example/webapps/solr.war /opt/solr-4.8.1/example/solr/solr.war

- Copy Solr logging libraries to Tomcat 7

        cp -r /opt/solr-4.8.1/example/lib/ext/* /usr/share/tomcat7/lib
        
- Create /var/lib/tomcat7/conf/Catalina/localhost/solr.xml

        <Context docBase="/opt/solr/example/solr/solr.war" debug="0" crossContext="true">
          <Environment name="solr/home" 
                       type="java.lang.String" 
                       value="/opt/solr/example/solr" 
                       override="true" />
        </Context>

- Configure logging

        1. mkdir /var/log/solr
        2. edit /usr/share/tomcat7/lib/log4j.properties and set "solr.log=/var/log/solr"
        
- Configure port for Solr in /opt/solr/example/solr/solr.xml

        <int name="hostPort">8080</int>
        
- Set correct ownership of files and folders

        1. chown -R tomcat7 /opt/solr
        2. chown -R tomcat7 /var/log/solr
        3. chown -R tomcat7 /opt/solr-4.8.1
        
- Give tomcat enough RAM, or you will get this weird error: "Unknown character set index for field received from server"

        edit /etc/init.d/tomcat7 and add this line:
        
        > export CATALINA_OPTS="-Xmx1500m"
        

