package com.ihtsdo.snomed.model.refset;

public interface Visitable {

    public void accept(Visitor visitor);

}
