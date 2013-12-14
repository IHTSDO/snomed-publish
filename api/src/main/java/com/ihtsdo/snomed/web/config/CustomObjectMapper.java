package com.ihtsdo.snomed.web.config;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component("jacksonObjectMapper")
public class CustomObjectMapper extends ObjectMapper {
    
    private static final long serialVersionUID = -3243435889443234525L;

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        //super.enable(SerializationFeature.WRAP_ROOT_VALUE);
        //super.enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);
        //super.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
    }
}
