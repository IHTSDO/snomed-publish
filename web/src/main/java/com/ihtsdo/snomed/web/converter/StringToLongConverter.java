package com.ihtsdo.snomed.web.converter;

import org.springframework.core.convert.converter.Converter;

public class StringToLongConverter implements Converter<String, Long> {

    @Override
    public Long convert(String source) {
        if ((source == null) || (source.isEmpty()) || (source.equals("0"))){
            return 0L;
        }
        return new Long(source);
    }

}
