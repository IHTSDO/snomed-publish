package com.ihtsdo.snomed.service.manifest.model;

import java.util.List;

public class LanguageRefsetCollection extends BaseRefsetCollection{

    public LanguageRefsetCollection() {
        super();
    }
    
    public LanguageRefsetCollection(List<RefsetModule> refsetModules){
        super(refsetModules);
    }

}
