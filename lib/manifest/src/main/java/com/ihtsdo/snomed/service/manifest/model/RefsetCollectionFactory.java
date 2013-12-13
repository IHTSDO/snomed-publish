package com.ihtsdo.snomed.service.manifest.model;

import javax.inject.Inject;
import javax.inject.Named;

import com.ihtsdo.snomed.exception.InvalidInputException;

@Named
public class RefsetCollectionFactory {
    
    @Inject
    MimetypeProperties mimetypeProperties;
    
    public BaseRefsetCollection getRefsetCollection(String mimetype) throws InvalidInputException{
        if (mimetypeProperties.isContentRefsetCollectionMimetype(mimetype)){
            return new ContentRefsetCollection();
        }
        else if (mimetypeProperties.isCrossmapRefsetCollectionMimetype(mimetype)){
            return new CrossmapRefsetCollection();
        }
        else if (mimetypeProperties.isLanguageRefsetCollectionMimetype(mimetype)){
            return new LanguageRefsetCollection();
        }
        else if (mimetypeProperties.isMetadataRefsetCollectionMimetype(mimetype)){
            return new MetadataRefsetCollection();
        }
        else if (mimetypeProperties.isOrderedTypeRefsetCollectionMimetype(mimetype)){
            return new OrderedTypeRefsetCollection();
        } 
        throw new InvalidInputException("mimetype '" + mimetype + "' is not a refset mimetype");
        
    }

}
