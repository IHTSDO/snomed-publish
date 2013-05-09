package com.ihtsdo.snomed.service;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import com.ihtsdo.snomed.model.Statement;

public interface Serialiser {

    public void write (Writer w, Collection<Statement> statements) throws IOException;

}
