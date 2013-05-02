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
    
    public Group(int id){
        this.id = id;
    }

    public void addStatement(Statement statement){
        this.statements.add(new StatementWrapperForAttributeCompare(statement));
    }
    @Override
    public String toString(){
        String value = "Group " + id + ": {";
        for (StatementWrapperForAttributeCompare statement : statements){
            value += statement.getStatement().shortToString() + ", ";
        }
        value += "}";
        return value;
    }
    
    @Override
    public int hashCode(){
        return statements.size();
    }
    
    @Override
    public boolean equals (Object o){
        if (o instanceof Group){
            Group g = (Group) o;
            if (g.statements.size() != this.statements.size()){
                //System.out.println("Statement.size are different, returning false");
                return false;
            }
            
            if (this.statements.equals(g.statements)){
                //System.out.println("sets are different, returning false");
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
