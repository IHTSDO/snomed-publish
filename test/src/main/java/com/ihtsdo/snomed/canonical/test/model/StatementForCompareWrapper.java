package com.ihtsdo.snomed.canonical.test.model;

import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.canonical.model.Statement;


public class StatementForCompareWrapper {
    
    private Statement statement;
    
    int hashCode;
    
    public StatementForCompareWrapper(Statement statement) {
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
        return Longs.hashCode(getStatement().getSubject().getSerialisedId());
    }    
    
    @Override
    public boolean equals(Object o){
        //System.out.println("EQUALS");
        if (o instanceof StatementForCompareWrapper){
            StatementForCompareWrapper r = (StatementForCompareWrapper) o;
            return r.getStatement().getSubject().equals(this.getStatement().getSubject()) &&
                    (r.getStatement().getPredicate().equals(this.getStatement().getPredicate())) &&
                    r.getStatement().getObject().equals(this.getStatement().getObject());
                    
        }
        return false;
    }
    
    public String toString(){
        return "Wrapped: " + statement.toString();
    }
    
    public Statement getStatement(){
        return statement;
    }

}
