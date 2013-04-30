package com.ihtsdo.snomed.canonical.test.model;

import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.canonical.model.Statement;


public class RelationshipStatementForCompareWrapper {
    
    private Statement statement;
    
    int hashCode;
    
    public RelationshipStatementForCompareWrapper(Statement statement) {
        this.statement = statement;
//        int x = (int)statement.getSubject().getId();
//        int y = (int)statement.getRelationshipType();
//        int z = (int)statement.getObject().getId();
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
//               .putLong(statement.getSubject().getId())
//               .putLong(statement.getRelationshipType())
//               .putLong(statement.getObject().getId())
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
        return "Wrapped: " + statement.toString();
    }
    
    public Statement getRelationshipStatement(){
        return statement;
    }

}
