server {

        listen 80;
        server_name sparql.snomedtools.com;

        location / {
            proxy_set_header X-Forwarded-Host $host;
            proxy_set_header X-Forwarded-Server $host;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_pass http://127.0.0.1:8080/web/version/1/sparql;
            proxy_redirect http://127.0.0.1:8080/web/version/1/sparql /;
        }
        
        #index index.html index.htm;

        # Make site accessible from http://localhost/
        #server_name localhost;
}
