package com.ihtsdo.snomed.canonical.model;

import java.util.HashSet;
import java.util.Set;

import com.google.common.primitives.Longs;

public class Group {
    
    private final Set<StatementWrapperForAttributeCompare> statements = new HashSet<StatementWrapperForAttributeCompare>();
    private final int id;
    
    public Group(Statement statement){
        this.statements.add(new StatementWrapperForAttributeCompare(statement));
        this.id = statement.getGroup();
    }

    public void addStatement(Statement statement){
        this.statements.add(new StatementWrapperForAttributeCompare(statement));
    }
    
    @Override
    public int hashCode(){
        return id;
//        if (statements.size() == 0){
//            return -1;
//        }
//        else{
//            return Longs.hashCode(((Statement)statements.iterator().next().getStatement()).getSubject().getSerialisedId());
//        }
    }
    
    @Override
    public boolean equals (Object o){
        if (o instanceof Group){
            Group g = (Group) o;
            
//            if (this.id == g.id){
//                return true;
//            }
            
            if (g.statements.size() != this.statements.size()){
                return false;
            }
            
            if (this.statements.equals(g.statements)){
                return true;
            }
            
        }
        return false;
    }
    
    private class StatementWrapperForAttributeCompare{
        Statement statement;
        
        public StatementWrapperForAttributeCompare(Statement statement){
            this.statement = statement;
        }
        
        @Override
        public boolean equals(Object o){
            if (o instanceof StatementWrapperForAttributeCompare){
                StatementWrapperForAttributeCompare r = (StatementWrapperForAttributeCompare) o;            
                if (r.statement.getPredicate().equals(this.statement.getPredicate()) &&
                    r.statement.getObject().equals(this.statement.getObject()))
                {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public int hashCode(){
            return Longs.hashCode(statement.getPredicate().getSerialisedId());
        }
        
        public String toString(){
            return "wrapped: " + statement.toString();
        }
        
        public Statement getStatement(){
            return statement;
        }
    }    
}
