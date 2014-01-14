package com.ihtsdo.snomed.service.serialiser;

import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.util.Collection;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;

public class Rf2Serialiser extends BaseSnomedSerialiser {
    
    public Rf2Serialiser(Writer writer) throws IOException {
        super(writer);
    }

    @Override
    public SnomedSerialiser header() throws IOException {
        writer.write("id"+ DELIMITER + "effectiveTime" + DELIMITER + "active" + DELIMITER + "moduleId" + DELIMITER + "definitionStatusId" + LINE_ENDING);
        return this;
    }

    @Override
    public SnomedSerialiser footer() throws IOException {
        return this;
    }

    @Override
    public void write(Ontology o, Collection<Statement> statements) throws IOException, ParseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(Statement statement) throws IOException, ParseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(final Concept c) throws IOException, ParseException {
        writer.write(
                Long.toString(c.getSerialisedId()) + DELIMITER +
                Long.toString(c.getEffectiveTime()) + DELIMITER +
                (c.isActive() ? '1' : '0') + DELIMITER +
                ((c.getModule() != null) ? Long.toString(c.getModule().getSerialisedId()) : '0') + DELIMITER +
                Integer.toString(c.getStatusId()) + DELIMITER +
                LINE_ENDING);
    }

    @Override
    public void write(Description d) throws IOException, ParseException {
        throw new UnsupportedOperationException();
    }

}
