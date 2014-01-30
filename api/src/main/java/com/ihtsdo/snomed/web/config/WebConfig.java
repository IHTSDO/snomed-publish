package com.ihtsdo.snomed.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import com.ihtsdo.snomed.model.refset.Refset;

@Configuration
public class WebConfig extends RepositoryRestMvcConfiguration {

	  @Override protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		  config.addResourceMappingForDomainType(Refset.class)
          .addResourceMappingFor("title")
          .setPath("title"); 
	  }

}
