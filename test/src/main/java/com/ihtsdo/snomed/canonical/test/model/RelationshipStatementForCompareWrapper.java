package com.ihtsdo.snomed.canonical.test.model;

import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;


public class RelationshipStatementForCompareWrapper {
    
    private RelationshipStatement relationshipStatement;
    
    int hashCode;
    
    public RelationshipStatementForCompareWrapper(RelationshipStatement relationshipStatement) {
        this.relationshipStatement = relationshipStatement;
//        int x = (int)relationshipStatement.getSubject().getId();
//        int y = (int)relationshipStatement.getRelationshipType();
//        int z = (int)relationshipStatement.getObject().getId();
//        int result = (int) (x ^ (x >>> 32));
//        result = 31 * result + (int) (y ^ (y >>> 32));
//        result = 31 * result + (int) (z ^ (z >>> 32));
//        hashCode = result;
//        COUNTER++;
//        if (COUNTER % 10000 == 0){
//            System.out.println(COUNTER);
//        }
    }
    
//    public int hashCode(){
//        HashFunction hf = Hashing.goodFastHash(3);
//        HashCode hc = hf.newHasher()
//               .putLong(relationshipStatement.getSubject().getId())
//               .putLong(relationshipStatement.getRelationshipType())
//               .putLong(relationshipStatement.getObject().getId())
//               .hash();
//        //System.out.println("HASH");
//        
//        return hc.asInt();
//    }
    
    @Override
    public int hashCode()
    {
        return Longs.hashCode(getRelationshipStatement().getSubject().getSerialisedId());
    }    
    
    @Override
    public boolean equals(Object o){
        //System.out.println("EQUALS");
        if (o instanceof RelationshipStatementForCompareWrapper){
            RelationshipStatementForCompareWrapper r = (RelationshipStatementForCompareWrapper) o;
            return r.getRelationshipStatement().getSubject().equals(this.getRelationshipStatement().getSubject()) &&
                    (r.getRelationshipStatement().getPredicate().equals(this.getRelationshipStatement().getPredicate())) &&
                    r.getRelationshipStatement().getObject().equals(this.getRelationshipStatement().getObject());
                    
        }
        return false;
    }
    
    public String toString(){
        return "Wrapped: " + relationshipStatement.toString();
    }
    
    public RelationshipStatement getRelationshipStatement(){
        return relationshipStatement;
    }

}
