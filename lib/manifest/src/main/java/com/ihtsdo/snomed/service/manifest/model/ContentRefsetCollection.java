package com.ihtsdo.snomed.service.manifest.model;

import java.util.List;

public class ContentRefsetCollection extends BaseRefsetCollection{

    public ContentRefsetCollection() {
        super();
    }
    
    public ContentRefsetCollection(List<RefsetModule> refsetModules){
        super(refsetModules);
    }
}
