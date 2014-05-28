# How to deploy Snomed-Publish apps


### Install Nginx with SPDY 3 support

- Following instructions [found here](https://bjornjohansen.no/install-latest-version-of-nginx-on-ubuntu), download and install Nginx with SPDY3 module enabled:

        curl http://nginx.org/keys/nginx_signing.key | apt-key add -
        echo -e "deb http://nginx.org/packages/mainline/ubuntu/ `lsb_release -cs` nginx\ndeb-src http://nginx.org/packages/mainline/ubuntu/ `lsb_release -cs` nginx" > /etc/apt/sources.list.d/nginx.list
        sudo apt-get install spdy

- Then [copy/create your SSL certificates](https://www.startssl.com/?app=42), and put them in `/etc/nginx/ssl/{site url}/.` 
- Download the site configs from our [Github repository](https://github.com/IHTSDO/snomed-publish/tree/master/config/nginx)

- You will need to change the filenames / server names / SSL certificate location and name to match the URL the app(s) will be deployed to

- We have the following apps:

  - **Browser** (Deprecated: Tomcat, MySQL, Java 7, Solr), 
  - **Search** (Deprecated: Tomcat, MySQL, Java 7, Solr), 
  - **Jenkins Build Server** (Java), 
  - **Refset Client** (NodeJS, Solr), 
  - **Solr** (Tomcat, MySQL, Java), 
  - **Refset API** (Tomcat, MySQL, Java 7), 
  - **Sparql API** (Fuseki, Java)

- You might want to deploy these on to different servers, as they are standalone apps


###Install Java 7
- Install Oracle Java 7 using PPA, following [these instructions](http://community.linuxmint.com/tutorial/view/1414)

        sudo add-apt-repository ppa:webupd8team/java
        sudo apt-get update
        sudo apt-get install oracle-java7-installer
        
- To later update your java version, run

        sudo update-java-alternatives -s java-7-oracle

- Configure Java enironment variables:

        sudo apt-get install oracle-java7-set-default

* [Fix old Java Bug](http://docs.oracle.com/cd/E13209_01/wlcp/wlss30/configwlss/jvmrand.html), or Tomcat will never start: 

        edit $JAVA_HOME/jre/lib/security/java.security and set
        > securerandom.source=file:/dev/./urandom
        
        NB! Note the special '/./' notation, which is _required_ [sic]

###Install Apache Tomcat 7
- Use apt-get to install
      
        apt-get install tomcat7

- The following apps run in Apache Tomcat 7, you may chose to run this in one instance, or on different servers
    - **Browser** (deprecated)
    - **Search** (deprecated)
    - **Refset Api**
    - **Jenkins Build Server**
    - **Solr**
    
- Make sure you give tomcat enough memory

        edit /etc/init.d/tomcat7 and add:
        > export CATALINA_OPTS="-Xmx6000m"

- Tomcat JAVA Memory requirements

    Application  | Optimal Stack Size
    ------------ | -------------
    Solr | 1Gb
    Jenkins | 1.5Gb
    Refset API | 500m and up (depend on cache size)
    Browser | ???
    Search | ???

###Install MySQL
Install using apt

    sudo apt-get install mysql-server mysql-client
    
###Install Git
Install using apt

    sudo apt-get install git

###Install maven 3
Install using apt

    sudo apt-get install maven

###Clone the snomed-publish repo, build, install, and package

    git clone https://github.com/IHTSDO/snomed-publish
    cd snomed-publish
    mvn install
    cd client
    mvn package

###Initialise MySQL with Snomed releases

- Log into mysql and create a new empty database called 'snomed'
- Import all your Snomed snapshot(!) releases into your database, following [these instructions](../client/import-main)

###Install Solr
Follow [these instructions](solr)

###Index all Snomed releases in Solr
- Create index for Concept

        mv /opt/solr/example/solr/collection1 /opt/solr/example/solr/concept
        edit /opt/solr/example/solr/concept/core.properties and set
        > name=concept

- Configure Concept
  
        edit /opt/solr/example/solr/concept/conf/solrconfig.xml and add this:

            <updateRequestProcessorChain>
               <processor class="solr.UUIDUpdateProcessorFactory">
                <str name="fieldName">uuid</str>
               </processor>
               <processor class="solr.LogUpdateProcessorFactory" />
               <processor class="solr.RunUpdateProcessorFactory" />
            </updateRequestProcessorChain>

- Add data-config.xml
        
        wget -P /opt/solr/example/solr/concept/conf/ https://github.com/IHTSDO/snomed-publish/tree/master/config/solr/data-config.xml

- Add schema.xml
  
        wget -P /opt/solr/example/solr/concept/conf/ https://github.com/IHTSDO/snomed-publish/tree/master/config/solr/schema.xml 

- Ignore all the other files in github [config/solr](https://github.com/IHTSDO/snomed-publish/tree/master/config/solr)
  
- Edit /opt/solr/example/solr/concept/conf/data-config.xml and set the correct mysql database password

- make sure that all the Solr files are readable/writable by your tomcat7 user

        chown -R tomcat7 /opt/solr


- remove title, id from schema.xml

        Edit /opt/solr/example/solr/concept/conf/schema.xml and comment out these lines:
            
          <fields>
            ...
            <!--field name="id" 
                      type="string" 
                      indexed="true" 
                      stored="true" 
                      required="true" /-->
            ...
            <!--field name="title" 
                      type="text_general" 
                      indexed="true"
                      stored="true"
                      multiValued="true"/-->
            ...
          </fields>

- Add data import extension libraries to Solr 
  
        cp /opt/solr/dist/solr-dataimporthandler-4.8.1.jar /opt/solr/example/solr/lib

- Add mysql driver to Solr
      
        1. Download driver from http://dev.mysql.com/downloads/connector/j/
        2. Copy driver jar to /opt/solr/example/solr/lib

- Modify /opt/solr/example/solr/concept/conf/solrconfig.xml
    
        1. Add

            <requestHandler name="/dataimport" 
                            class="org.apache.solr.handler.dataimport.DataImportHandler">
                <lst name="defaults">
                    <str name="config">data-config.xml</str>
                </lst>
            </requestHandler>
    
        2. Add

        <lib dir="../lib" />
            
- restart tomcat

        service tomcat7 restart
    
