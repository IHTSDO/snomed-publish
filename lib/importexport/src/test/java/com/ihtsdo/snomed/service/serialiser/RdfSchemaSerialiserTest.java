package com.ihtsdo.snomed.service.serialiser;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.serialiser.SnomedSerialiserFactory.Form;

public class RdfSchemaSerialiserTest {

    private static final String EXPECTED_RESULT =
    		"<rdf:RDF\n" +
    		" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
    		" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" +
    		" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" +
    		" xmlns:sn=\"http://snomed.info/sct/term/\"\n" +
    		" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
    		">\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/term/effectiveTime\">\n" +
    		"<rdfs:label xml:lang=\"en-gb\">EffectiveTime</rdfs:label>\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/term/active\">\n" +
    		"<rdfs:label xml:lang=\"en-gb\">Active</rdfs:label>\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/term/status\">\n" +
    		"<rdfs:label xml:lang=\"en-gb\">Status</rdfs:label>\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/term/module\">\n" +
    		"<rdfs:label xml:lang=\"en-gb\">Module</rdfs:label>\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/term/group\">\n" +
    		"<rdfs:label xml:lang=\"en-gb\">Group</rdfs:label>\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/term/characteristictype\">\n" +
    		"<rdfs:label xml:lang=\"en-gb\">CharacteristicType</rdfs:label>\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/term/modifier\">\n" +
    		"<rdfs:label xml:lang=\"en-gb\">Modifier</rdfs:label>\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/term/description\">\n" +
    		"<rdfs:label xml:lang=\"en-gb\">Description</rdfs:label>\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/term/casesignificance\">\n" +
    		"<rdfs:label xml:lang=\"en-gb\">CaseSignificance</rdfs:label>\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/term/type\">\n" +
    		"<rdfs:label xml:lang=\"en-gb\">Type</rdfs:label>\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/version/1/concept/rdfs/100523006\">\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/2000/01/rdf-schema#Class\"/>\n" +
    		"<rdfs:label xml:lang=\"en-gb\">null</rdfs:label>\n" +
    		"<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">false</sn:active>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/version/1/concept/rdfs/100523007\">\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/2000/01/rdf-schema#Class\"/>\n" +
    		"<rdfs:label xml:lang=\"en-gb\">null</rdfs:label>\n" +
    		"<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">false</sn:active>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/version/1/concept/rdfs/116680003\">\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n" +
    		"<rdfs:label xml:lang=\"en-gb\">null</rdfs:label>\n" +
    		"<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">false</sn:active>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/version/1/statement/rdfs/0\">\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement\"/>\n" +
    		"<rdf:subject rdf:resource=\"http://snomed.info/sct/version/1/concept/rdfs/100523006\"/>\n" +
    		"<rdf:predicate rdf:resource=\"http://snomed.info/sct/version/1/concept/rdfs/116680003\"/>\n" +
    		"<rdf:object rdf:resource=\"http://snomed.info/sct/version/1/concept/rdfs/100523007\"/>\n" +
    		"<sn:group rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">0</sn:group>\n" +
    		"<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">false</sn:active>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/version/1/concept/rdfs/100523008\">\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/2000/01/rdf-schema#Class\"/>\n" +
    		"<rdfs:label xml:lang=\"en-gb\">null</rdfs:label>\n" +
    		"<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">false</sn:active>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/version/1/concept/rdfs/100523009\">\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/2000/01/rdf-schema#Class\"/>\n" +
    		"<rdfs:label xml:lang=\"en-gb\">null</rdfs:label>\n" +
    		"<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">false</sn:active>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/version/1/concept/rdfs/116680004\">\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n" +
    		"<rdfs:label xml:lang=\"en-gb\">null</rdfs:label>\n" +
    		"<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">false</sn:active>\n" +
    		"</rdf:Description>\n" +
    		"<rdf:Description rdf:about=\"http://snomed.info/sct/version/1/statement/rdfs/1\">\n" +
    		"<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement\"/>\n" +
    		"<rdf:subject rdf:resource=\"http://snomed.info/sct/version/1/concept/rdfs/100523008\"/>\n" +
    		"<rdf:predicate rdf:resource=\"http://snomed.info/sct/version/1/concept/rdfs/116680004\"/>\n" +
    		"<rdf:object rdf:resource=\"http://snomed.info/sct/version/1/concept/rdfs/100523009\"/>\n" +
    		"<sn:group rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">1</sn:group>\n" +
    		"<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">false</sn:active>\n" +
    		"</rdf:Description>\n" +
    		"</rdf:RDF>\n";

    private Collection<Statement> statements = new HashSet<Statement>();
    
    @Before
    public void setUp() throws Exception {
        Concept c1 = new Concept(100523006l);
        Concept c2 = new Concept(100523007);
        Concept c3 = new Concept(100523008l);
        Concept c4 = new Concept(100523009l);
        
        Concept cp1 = new Concept(116680003l);
        Concept cp2 = new Concept(116680004l);

        Statement r1 = new Statement(0, c1, cp1, c2, Statement.DEFINING_CHARACTERISTIC_TYPE, 0);
        Statement r2 = new Statement(1, c3, cp2, c4, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);

        statements.add(r1);
        statements.add(r2);
        
        OntologyVersion o = new OntologyVersion(1L);
        o.addStatement(r1);
        o.addStatement(r2);
    }

    @Test
    public void shouldPrintOntologyInSpecifiedFormat() throws IOException, ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos, "utf-8"))){
            SnomedSerialiserFactory.getSerialiser(Form.RDF_SCHEMA, pw).write(statements);
        }
        baos.flush();
        System.out.println(baos.toString());
        assertEquals(EXPECTED_RESULT, baos.toString());
    }
}
