package com.ihtsdo.snomed.web.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource("classpath:spring.properties")
public class SparqlTemplateConfig {
//  @Value("${rest.client.login}")
//  private String restClientLogin;
//
//  @Value("${rest.client.password}")
//  private String restClientPassword;

  //@Value("${rest.client.connectionTimeoutMillis}")
  private int restClientConnectionTimeoutMillis=60000;

  //@Value("${rest.client.readTimeoutMillis}")
  private int restClientReadTimeoutMillis=120000;

  //@Value("${rest.client.maxConnectionsPerHost}")
  private int restClientMaxConnectionsPerHost=5;

  //@Value("${rest.client.maxTotalConnections}")
  private int restClientMaxTotalConnections=100;

  @Bean
  public HttpClient getHttpClient() {
      final PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
      connectionManager.setDefaultMaxPerRoute(restClientMaxConnectionsPerHost);
      connectionManager.setMaxTotal(restClientMaxTotalConnections);
      final DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager);
      //HttpRequestInterceptor interceptor = new HttpBasicAuthInterceptor(new UsernamePasswordCredentials(restClientLogin, restClientPassword));
      //httpClient.addRequestInterceptor(interceptor);
      return httpClient;
  }

  @Bean
  public ClientHttpRequestFactory getClientHttpRequestFactory() {
      HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(getHttpClient());
      factory.setConnectTimeout(restClientConnectionTimeoutMillis);
      factory.setReadTimeout(restClientReadTimeoutMillis);
      return factory;
  } 

  @Bean
  public RestTemplate getRestTemplate() {
      RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
      return restTemplate;
  }

//
//  public class HttpBasicAuthInterceptor implements HttpRequestInterceptor {
//      private UsernamePasswordCredentials creds;
//  
//      public HttpBasicAuthInterceptor(UsernamePasswordCredentials creds) {
//          this.creds = creds;
//      }
//  
//      @Override
//      public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
//          request.addHeader(new BasicScheme().authenticate(creds, request, context));
//      }
//  }
}
