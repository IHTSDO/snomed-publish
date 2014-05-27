Installing Solr
===============

Installation instructions for Solr 4.8.8 running in Tomcat 7. We are following the installation instructions [found here](http://stackoverflow.com/questions/23503116/cant-get-solr-4-8-working-with-tomcat-7-and-ubuntu-12-04).
    
- Download Solr
        wget http://mirror.nus.edu.sg/apache/lucene/solr/4.8.1/solr-4.8.1.tgz
- Untar and put in /opt
        tar -zxvf solr-4.8.1.tgz
        mv solr-4.8.1 /opt
        ln -s /opt/solr-4.8.1 /opt/solr
- Install in Tomcat 7
        cd /opt/solr-4.8.1
        cp example/webapps/solr.war example/solr/solr.war
        cp -r example/lib/ext/* /usr/share/tomcat7/lib
- Create /var/lib/tomcat7/conf/Catalina/localhost/solr.xml and add:
      <Context docBase="/opt/solr/example/solr/solr.war" debug="0" crossContext="true">
        <Environment name="solr/home" 
                     type="java.lang.String" 
                     value="/opt/solr/example/solr" 
                     override="true" />
      </Context>

- Configure logging
        mkdir /var/log/solr
        edit /usr/share/tomcat7/lib/log4j.properties and set solr.log=/var/log/solr
- Configure port for Solr links in /opt/solr/example/solr/solr.xml
        <int name="hostPort">8080</int>
- Set correct ownership of files and folders
        chown -R tomcat7 /opt/solr
        chown -R tomcat7 /var/log/solr
        chown -R tomcat7 /opt/solr-4.8.1
- Give tomcat enough RAM, or you will get this weird error: "Unknown character set index for field received from server"

        edit /etc/init.d/tomcat7 and add: export CATALINA_OPTS="-Xmx1500m"
        

