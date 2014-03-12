package com.ihtsdo.snomed.model.xml;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Statement;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class UrlBuilder {

    private static final String ontologyVersion_id_KEY = "__ontologyVersion_id__";
    private static final String CONCEPT_ID_KEY = "__CONCEPT_ID__";
    private static final String DESCRIPTION_ID_KEY = "__DESCRIPTION_ID__";
    private static final String STATEMENT_ID_KEY = "__STATEMENT_ID__";

	static {
		APP_URL = readProperties("/url.properties").getProperty("appUrl");
	}
	private static String APP_URL;
	private static final String ONTOLOGY_URL = APP_URL + ontologyVersion_id_KEY;
	private static final String CONCEPT_URL = APP_URL + ontologyVersion_id_KEY + "/concept/xml/" + CONCEPT_ID_KEY;
	private static final String STATEMENT_URL = APP_URL + ontologyVersion_id_KEY + "/triple/xml/" + STATEMENT_ID_KEY;
	private static final String DESCRIPTION_URL = APP_URL + ontologyVersion_id_KEY + "/description/xml/" + DESCRIPTION_ID_KEY;
	public static URL createOntologyUrl(Concept c) throws MalformedURLException {
        return new URL(ONTOLOGY_URL.replace(ontologyVersion_id_KEY, Long.toString(c.getOntology().getId())));
    }


    public static URL createOntologyUrl(Statement s) throws MalformedURLException {
        return new URL(ONTOLOGY_URL.replace(ontologyVersion_id_KEY, Long.toString(s.getOntologyVersion().getId())));
    }

    public static URL createOntologyUrl(Description d) throws MalformedURLException {
        return new URL(ONTOLOGY_URL.replace(ontologyVersion_id_KEY, Long.toString(d.getOntology().getId())));
    }

    public static URL createDescriptionUrl(Description d) throws MalformedURLException {
        return new URL(DESCRIPTION_URL
                .replace(ontologyVersion_id_KEY, Long.toString(d.getOntology().getId()))
                .replace(DESCRIPTION_ID_KEY, Long.toString(d.getSerialisedId())));
    }

    public static URL createConceptUrl(Concept c) throws MalformedURLException {
        return new URL(CONCEPT_URL
                .replace(ontologyVersion_id_KEY, Long.toString(c.getOntology().getId()))
                .replace(CONCEPT_ID_KEY, Long.toString(c.getSerialisedId())));
    }

    public static URL createStatementUrl(Statement s) throws MalformedURLException {
        return new URL(STATEMENT_URL
                .replace(ontologyVersion_id_KEY, Long.toString(s.getOntologyVersion().getId()))
                .replace(STATEMENT_ID_KEY, Long.toString(s.getSerialisedId())));
    }

	private static Properties readProperties(String fileName) {
		Properties properties = new Properties();
		try {
			properties.load(UrlBuilder.class.getResourceAsStream(fileName));
			return properties;
		} catch (IOException e) {
			throw new RuntimeException("Failed to load url.properties containing application url.", e);
		}
	}

}
