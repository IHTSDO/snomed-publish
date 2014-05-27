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

- Tomcat Memory requirements

Application  | Optimal Stack Size
------------ | -------------
Solr | 1Gb
Jenkins | ???
Refset API | ???
Browser | ???
Search | ???
