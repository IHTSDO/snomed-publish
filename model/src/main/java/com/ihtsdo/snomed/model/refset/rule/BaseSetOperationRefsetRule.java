package com.ihtsdo.snomed.model.refset.rule;

import com.ihtsdo.snomed.model.refset.BaseRule;
import com.ihtsdo.snomed.model.refset.Rule;
import com.ihtsdo.snomed.model.refset.Visitor;

public abstract class BaseSetOperationRefsetRule extends BaseRule {
    
    public static String LEFT_OPERAND  = "left";
    public static String RIGHT_OPERAND = "right";
    
    @Override
    public void accept(Visitor visitor){
        if (getIncomingRules().get(LEFT_OPERAND) != null){
            getIncomingRules().get(LEFT_OPERAND).accept(visitor);
        }
        if (getIncomingRules().get(RIGHT_OPERAND) != null){
            getIncomingRules().get(RIGHT_OPERAND).accept(visitor);
        }
        visitor.visit(this);
    }
    
    public BaseSetOperationRefsetRule setLeftRule(Rule rule){
        this.getIncomingRules().put(LEFT_OPERAND, rule);
        return this;
    }
    
    public BaseSetOperationRefsetRule setRightRule(Rule rule){
        this.getIncomingRules().put(RIGHT_OPERAND, rule);
        return this;
    }
    
    public Rule getLeft(){
        return getIncomingRules().get(LEFT_OPERAND);
    }
    
    public Rule getRight(){
        return getIncomingRules().get(RIGHT_OPERAND);
    }
}
