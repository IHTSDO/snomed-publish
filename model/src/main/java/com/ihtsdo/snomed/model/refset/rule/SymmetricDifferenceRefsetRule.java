package com.ihtsdo.snomed.model.refset.rule;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.google.common.collect.Sets;
import com.ihtsdo.snomed.model.Concept;

/*https://code.google.com/p/guava-libraries/wiki/CollectionUtilitiesExplained#Sets*/
@Entity
@DiscriminatorValue("symmetricDifference")
public class SymmetricDifferenceRefsetRule extends BaseSetOperationRefsetRule{
    @Override
    protected Set<Concept> apply(Map<String, Set<Concept>> inputs) {
        assert(inputs.size() == 2);
        return Sets.symmetricDifference(inputs.get(LEFT_OPERAND), inputs.get(RIGHT_OPERAND)).
                copyInto(new HashSet<Concept>());     
    }
    
    @Override
    protected BaseSetOperationRefsetRule cloneSet() {
        return new SymmetricDifferenceRefsetRule();
    }    
    
}
