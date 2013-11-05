package com.ihtsdo.snomed.service.manifest.model;

import java.util.List;

public class MetadataRefsetCollection extends BaseRefsetCollection{

    public MetadataRefsetCollection() {
        super();
    }
    
    public MetadataRefsetCollection(List<RefsetModule> refsetModules){
        super(refsetModules);
    }

}
