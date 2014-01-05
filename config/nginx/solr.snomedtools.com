server {

        listen 80;
        server_name solr.snomedtools.com;
        root /opt/solr/example/solr-webapp/webapp;
        rewrite ^/solr(.*)$ $1 last;


        location / {
            proxy_set_header X-Forwarded-Host $host;
            proxy_set_header X-Forwarded-Server $host;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_pass http://127.0.0.1:8983/solr/;
            proxy_redirect http://127.0.0.1:8983/solr/ /;

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
        
        #index index.html index.htm;

        # Make site accessible from http://localhost/
        #server_name localhost;
}
