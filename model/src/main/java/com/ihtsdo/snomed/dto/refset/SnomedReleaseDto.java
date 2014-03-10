package com.ihtsdo.snomed.dto.refset;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ihtsdo.snomed.model.OntologyVersion;

public class SnomedReleaseDto {

    private Long id;
	private Date releaseDate;

	public SnomedReleaseDto(){}
	
	public SnomedReleaseDto(Long id){
	    this.id = id;
	}
	
	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }	
	
    public static Builder getBuilder(Long id, Date releaseDate) {
        return new Builder(id, releaseDate);
    }
    
    public static class Builder {
        private SnomedReleaseDto built;

        Builder(Long id, Date releaseDate) {
            built = new SnomedReleaseDto();
            built.setId(id);
            built.setReleaseDate(releaseDate);
        }
        
        public SnomedReleaseDto build(){
            return built;
        }
    }

    public static SnomedReleaseDto parse(OntologyVersion o) {
        SnomedReleaseDto snomedRelease = new SnomedReleaseDto();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        snomedRelease.setId(o.getId());
        try {
            snomedRelease.setReleaseDate(new java.sql.Date(df.parse("20130731").getTime()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return snomedRelease;
    }    
	
}
