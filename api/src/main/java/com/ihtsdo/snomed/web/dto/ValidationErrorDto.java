package com.ihtsdo.snomed.web.dto;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorDto {

    private List<RefsetErrorDto> fieldErrors = new ArrayList<>();

    public void addFieldError(String path, String message) {
        RefsetErrorDto error = new RefsetErrorDto(message);
        fieldErrors.add(error);
    }

}
