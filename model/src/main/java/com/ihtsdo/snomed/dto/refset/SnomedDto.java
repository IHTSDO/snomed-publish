package com.ihtsdo.snomed.dto.refset;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonRootName;

@XmlRootElement(name="snomed")
@JsonRootName("snomed")
public class SnomedDto {

    @Valid
    @NotNull
    private List<SnomedReleaseDto> releases;

    public List<SnomedReleaseDto> getReleases() {
        return releases;
    }

    public void setReleases(List<SnomedReleaseDto> releases) {
        this.releases = releases;
    }
    
}
