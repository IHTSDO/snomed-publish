package com.ihtsdo.snomed.exception;

import java.sql.Date;

import com.ihtsdo.snomed.model.SnomedFlavours.SnomedFlavour;

public class OntologyVersionNotFoundException extends Exception {

    private static final long serialVersionUID = -8503497279533972065L;
    
    private SnomedFlavour flavourPublicId;
    private Date releaseDate;

    public OntologyVersionNotFoundException(SnomedFlavour flavour, Date releaseDate) {
        this.flavourPublicId = flavour;
        this.releaseDate = releaseDate;
    }

    public OntologyVersionNotFoundException(SnomedFlavour flavour, Date releaseDate, String message) {
        super(message);
        this.flavourPublicId = flavour;
        this.releaseDate = releaseDate;
    }

    public OntologyVersionNotFoundException(SnomedFlavour flavour, Date releaseDate, Throwable cause) {
        super(cause);
        this.flavourPublicId = flavour;
        this.releaseDate = releaseDate;
    }

    public OntologyVersionNotFoundException(SnomedFlavour flavour, Date releaseDate, String message, Throwable cause) {
        super(message, cause);
        this.flavourPublicId = flavour;
        this.releaseDate = releaseDate;
    }

    public OntologyVersionNotFoundException(SnomedFlavour flavour, Date releaseDate, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.flavourPublicId = flavour;
        this.releaseDate = releaseDate;
    }

    public SnomedFlavour getFlavour() {
        return flavourPublicId;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }
}
