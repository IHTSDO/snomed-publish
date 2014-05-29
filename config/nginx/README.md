#Configuring Nginx

###Install Nginx with SPDY 3 support

- Following instructions [found here](https://bjornjohansen.no/install-latest-version-of-nginx-on-ubuntu), download and install Nginx with SPDY3 module enabled:

        curl http://nginx.org/keys/nginx_signing.key | apt-key add -
        echo -e "deb http://nginx.org/packages/mainline/ubuntu/ `lsb_release -cs` nginx\ndeb-src http://nginx.org/packages/mainline/ubuntu/ `lsb_release -cs` nginx" > /etc/apt/sources.list.d/nginx.list
        sudo apt-get update
        sudo apt-get install nginx
        
###Create and install SSL certificates
- [copy/create your SSL certificates](https://www.startssl.com/?app=42), and put them in `/etc/nginx/ssl/{site url}/.` 

###Install all the site configurations

- Edit /etc/nginx/nginx.conf and add the following

        http {
          ...
          include /etc/nginx/sites-enabled/*;
          ....
        }
    
- Create folders for your site configs

    mkdir /etc/nginx/sites-available
    mkdir /etc/nginx/sites-enabled

- We have the following apps:

  - **Browser** (Deprecated: Tomcat, MySQL, Java 7, Solr), 
  - **Search** (Deprecated: Tomcat, MySQL, Java 7, Solr), 
  - **Jenkins Build Server** (Java), 
  - **Refset Client** (NodeJS, Solr), 
  - **Solr** (Tomcat, MySQL, Java), 
  - **Refset API** (Tomcat, MySQL, Java 7), 
  - **Sparql API** (Fuseki, Java)

- Download the site configs for these apps from our [Github repository](https://github.com/IHTSDO/snomed-publish/tree/master/config/nginx) and place in /etc/nginx/sites-available

- Rename the files, and modify each config file, such that:

    - Assume the domain for the app you are trying to configure nginx for is called INSERT_SERVER_NAME_HERE
    - Rename each config file to INSERT_SERVER_NAME_HERE and put in /etc/nginx/sites-available/
    - Create a dynamic link to /etc/nginx/sites-enabled

        ln -s /etc/nginx/sites-available/INSERT_SERVER_NAME_HERE /etc/nginx/sites-enabled/INSERT_SERVER_NAME_HERE

    - Edit each config file, and replace INSERT_SERVER_NAME_HERE with the actual server name
    - Edit each config file, and replace INSERT_PORT_HERE with the actual port you are running the app under
    - Put your SSL certificate and key for this domain in /etc/nginx/ssl/INSERT_SERVER_NAME_HERE/
    

