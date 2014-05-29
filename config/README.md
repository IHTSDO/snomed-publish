# How to deploy Snomed-Publish apps


### Install Nginx and all the site configs
Follow the instructions [found here](nginx)

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

###Install Jenkins Build Server
- Use apt-get to install, following [these instructions](https://wiki.jenkins-ci.org/display/JENKINS/Installing+Jenkins+on+Ubuntu)

        wget -q -O - http://pkg.jenkins-ci.org/debian/jenkins-ci.org.key | sudo apt-key add -
        sudo sh -c 'echo deb http://pkg.jenkins-ci.org/debian binary/ > /etc/apt/sources.list.d/jenkins.list'
        sudo apt-get update
        sudo apt-get install jenkins
- To later upgrade your Jenkins install, run

        sudo apt-get update
        sudo apt-get install jenkins
- Set the Jenkins port number, make sure it does not clash with Tomcat

        Edit /etc/default/jenkins
        Set the port number to e.g. 9080
- Make sure Jenkins is running

        sudo service nginx start
- Visit the Jenkins URL with your browser to complete install. **Important:** Please make sure you access Jenkins using port 80 and the server name you defined in your nginx site config, and NOT the internal Jenkins port


###Install Apache Tomcat 7
- Use apt-get to install
      
        apt-get install tomcat7

- The following apps run in Apache Tomcat 7, you may chose to run this in one instance, or on different servers
    - **Browser** (deprecated)
    - **Search** (deprecated)
    - **Refset Api**
    - **Solr**
    
- Make sure you give tomcat enough memory

        edit /etc/init.d/tomcat7 and add:
        > export CATALINA_OPTS="-Xmx6000m"

- Tomcat JAVA Memory requirements

    Application  | Optimal Stack Size
    ------------ | -------------
    Solr | 1Gb
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
    
