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
@DiscriminatorValue("union")
public class UnionRefsetRule extends BaseSetOperationRefsetRule{
    @Override
    protected Set<Concept> apply(Map<String, Set<Concept>> inputs) {
        assert(inputs.size() == 2);
        return Sets.union(
                inputs.get(LEFT_OPERAND) == null ?  new HashSet<Concept>() : inputs.get(LEFT_OPERAND),
                inputs.get(RIGHT_OPERAND) == null ?  new HashSet<Concept>() : inputs.get(RIGHT_OPERAND)).
            copyInto(new HashSet<Concept>());
    }
    
    @Override
    protected BaseSetOperationRefsetRule cloneSet() {
        return new UnionRefsetRule();
    }    
    
//    public String toString(){
//        String set1 = getIncomingRules().get(LEFT_OPERAND) == null ? "empty" : "not empty";
//        String set2 = getIncomingRules().get(SET_2) == null ? "empty" : "not empty";
//        return LEFT_OPERAND + "[" + set1 + "]" +
//               " UNION " +
//               SET_2 + "[" + set2 + "]";
//    }
    
}
