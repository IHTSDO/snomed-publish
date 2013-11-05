package com.ihtsdo.snomed.service.manifest.model;

import java.util.List;

public class CrossmapRefsetCollection extends BaseRefsetCollection{

    public CrossmapRefsetCollection() {
        super();
    }
    
    public CrossmapRefsetCollection(List<RefsetModule> refsetModules){
        super(refsetModules);
    }

}
