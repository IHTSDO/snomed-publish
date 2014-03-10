package com.ihtsdo.snomed.model;

import java.util.HashMap;
import java.util.Map;

public class SnomedFlavours {

    public enum PublicId {INTERNATIONAL}
    
    public static final Map<PublicId, SnomedFlavour> SnomedFlavourMap = new HashMap<>();
    public static final SnomedFlavour INTERNATIONAL = new SnomedFlavour(PublicId.INTERNATIONAL, "Snomed International Release");
    
    static{
      SnomedFlavourMap.put(PublicId.INTERNATIONAL, INTERNATIONAL);
    }
    
    public static SnomedFlavour getFlavour(String publicId){
        return SnomedFlavourMap.get(SnomedFlavours.PublicId.valueOf(publicId.toUpperCase()));
    }
    
    public static SnomedFlavour getFlavour(PublicId publicId){
        return SnomedFlavourMap.get(publicId);
    }    
    
    public static class SnomedFlavour{
        private PublicId publicId;
        private String label;
        
        public SnomedFlavour(PublicId publicId, String label){
            this.publicId = publicId;
            this.label = label;
        }

        public PublicId getPublicId() {
            return publicId;
        }
        
        public String getPublicIdString() {
            return publicId.toString();
        }        

        public String getLabel() {
            return label;
        }
    }
}
