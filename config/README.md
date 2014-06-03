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
        
- Log out of your shell and log back in again, to load your java environment variables        

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

        sudo service jenkins restart
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
        
        wget -P /opt/solr/example/solr/concept/conf/ https://raw.githubusercontent.com/IHTSDO/snomed-publish/master/config/solr/data-config.xml

- Add schema.xml

        rm /opt/solr/example/solr/concept/conf/schema.xml
        wget -P /opt/solr/example/solr/concept/conf/ https://raw.githubusercontent.com/IHTSDO/snomed-publish/master/config/solr/schema.xml

- Ignore all the other files in github [config/solr](https://github.com/IHTSDO/snomed-publish/tree/master/config/solr)
  
- Edit /opt/solr/example/solr/concept/conf/data-config.xml and set the correct mysql database password

- make sure that all the Solr files are readable/writable by your tomcat7 user

        chown -R tomcat7 /opt/solr

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
    
###Setup Fuseki and initialise the Snomed SPARQL endpoint
- [Download Apache Jena](https://jena.apache.org/download/index.cgi) and unzip the contents to /opt

        wget http://mirror.sdunix.com/apache//jena/binaries/apache-jena-2.11.1.tar.gz
        tar -zxvf apache-jena-2.11.1.tar.gz
        mv apache-jena-2.11.1 /opt
        ln -s /opt/apache-jena-2.11.1 /opt/jena
- [Download Fuseki](https://jena.apache.org/download/index.cgi) and unzip the contents to /opt
    
        wget http://psg.mtu.edu/pub/apache//jena/binaries/jena-fuseki-1.0.1-distribution.tar.gz
        tar -zxvf jena-fuseki-1.0.1-distribution.tar.gz
        mv jena-fuseki-1.0.1 /opt
        ln -s /opt/jena-fuseki-1.0.1 /opt/fuseki
- [Download the RDF Convert tool](https://bitbucket.org/jeenbroekstra/rdf-syntax-convert/downloads) and unzip the contents to /opt

        wget https://bitbucket.org/jeenbroekstra/rdf-syntax-convert/downloads/rdfconvert-0.3.2-bin.zip
        unzip rdfconvert-0.3.2-bin.zip
        mv rdfconvert-0.3.2 /opt
        ln -s /opt/rdfconvert-0.3.2 rdfconvert
- Get all relevant Snomed releases and unzip. You will need the Snapshot RF2 Release(s) files (x3)
- Do these steps for every snapshot release you wish to import into Fuseki:
        
  - In your snomed-publish repo that you downloaded and built, find the RDFS Export tool and generate the RDF Schema file, following [these instructions](../client/rdfs-export-main)
  - Use the rdfconvert tool to transform your RDF Schema file into an n-quads format (modify the filename/locations in these examples to something more snensible)

          cd /opt/rdfconvert/bin
          ./rdfconvert.sh -i RDF/XML -o N-Quads snomed.snapshot.release.rdf snomed.snapshot.release.nq
  - Use sed to give a name to the graph for this release, e.g. `http://snomedtools.info/snomed/20130731`

          sed 's/\ \./\ <http:\/\/snomedtools\.info\/snomed\/20130731>\ \./' snomed.20130731.nq > snomed.20130731.fixed.nq
- Transform all of these string-replaced n-quad files into the Jena database format `tdb` in one go

        /opt/jena/bin/
        ./tdbloader2.sh -loc snomed.tdb snomed.20130731.fixed.nq snomed.20140131.fixed.nq ... etc.
- Start fuseki and point it to the generated jena database file from the previous step
    
        nohup /opt/fuseki/fuseki-server --loc=snomed.tdb /snomed &
