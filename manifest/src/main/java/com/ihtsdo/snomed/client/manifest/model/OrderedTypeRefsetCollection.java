package com.ihtsdo.snomed.client.manifest.model;

import java.util.List;

public class OrderedTypeRefsetCollection extends BaseRefsetCollection{

    public OrderedTypeRefsetCollection() {
        super();
    }
    
    public OrderedTypeRefsetCollection(List<RefsetModule> refsetModules){
        super(refsetModules);
    }

}
