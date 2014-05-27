# How to deploy Snomed-Publish apps


### Install Nginx with SPDY 3 support
Following instructions [found here](https://bjornjohansen.no/install-latest-version-of-nginx-on-ubuntu):

    > $ curl http://nginx.org/keys/nginx_signing.key | apt-key add -
    
    > echo -e "deb http://nginx.org/packages/mainline/ubuntu/ `lsb_release -cs` nginx\ndeb-src http://nginx.org/packages/mainline/ubuntu/ `lsb_release -cs` nginx" > /etc/apt/sources.list.d/nginx.list
    
    > sudo apt-get install spdy

