server {

        listen 80;
        server_name search.snomedtools.com;
        root /var/lib/tomcat7/webapps/web;

 # Enable gzip compression.
  gzip on;
  gzip_http_version 1.1;
  gzip_vary on;
  gzip_comp_level 6;
  gzip_proxied any;
  gzip_buffers 16 8k;
  gzip_disable "MSIE [1-6]\.(?!.*SV1)";

        location / {
            proxy_set_header X-Forwarded-Host $host;
            proxy_set_header X-Forwarded-Server $host;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            #proxy_set_header Expires
            expires max;
            #proxy_set_header Cache-Control "max-age=31536000, must-revalidate";
            proxy_pass http://127.0.0.1:8080/web/version/1/search;

#            proxy_read_timeout=600s;
#            proxy_send_timeout=600s;

            #proxy_redirect  http://127.0.0.1:8080/  http://browser.sparklingideas.co.uk/;
            proxy_redirect http://127.0.0.1:8080/web/version/1/search /;
            #proxy_redirect off;

            proxy_ignore_headers   Expires Cache-Control Set-Cookie;
            #proxy_cache            STATIC;
            #proxy_cache_valid      200  100d;
            #proxy_cache_use_stale  error timeout invalid_header updating
            #                       http_500 http_502 http_503 http_504;
  # Proxy headers.
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-Proto $scheme;
    #proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header Host $host;
    proxy_set_header X-NginX-Proxy true;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
    proxy_cache_bypass $http_upgrade;
    proxy_http_version 1.1;

    # Gateway timeout.
    proxy_read_timeout 20s;
    proxy_send_timeout 20s;
    # Buffer settings.
    proxy_buffers 8 32k;
    proxy_buffer_size 64k;

        }

        location ~ (\.js$|\.css$|\.ico|\.jpg|\.png) {
            add_header  Cache-Control  public;
            expires  100d;
        }

        #root /usr/share/nginx/www;
        index index.html index.htm;

        # Make site accessible from http://localhost/
#        server_name localhost;
}
