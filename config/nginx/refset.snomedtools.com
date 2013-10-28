server {

        listen 80;
        server_name refset.snomedtools.com;
        root /var/lib/tomcat7/webapps/ROOT;

        location / {
            proxy_set_header X-Forwarded-Host $host;
            proxy_set_header X-Forwarded-Server $host;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            #proxy_set_header Expires
            expires max;
            #proxy_set_header Cache-Control "max-age=31536000, must-revalidate";
            proxy_pass http://127.0.0.1:8080/refsets/;

#            proxy_read_timeout=600s;
#            proxy_send_timeout=600s;

            #proxy_redirect  http://127.0.0.1:8080/  http://browser.sparklingideas.co.uk/;
            proxy_redirect http://127.0.0.1:8080/refsets/ /;
            #proxy_redirect off;

            proxy_ignore_headers   Expires Cache-Control Set-Cookie;
            #proxy_cache            STATIC;
            #proxy_cache_valid      200  100d;
            #proxy_cache_use_stale  error timeout invalid_header updating
            #                       http_500 http_502 http_503 http_504;
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
