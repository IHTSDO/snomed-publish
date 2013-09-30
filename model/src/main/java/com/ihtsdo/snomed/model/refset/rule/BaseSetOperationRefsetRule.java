package com.ihtsdo.snomed.model.refset.rule;

import com.ihtsdo.snomed.model.refset.BaseRefsetRule;
import com.ihtsdo.snomed.model.refset.RefsetRule;
import com.ihtsdo.snomed.model.refset.Visitor;

public abstract class BaseSetOperationRefsetRule extends BaseRefsetRule {
    
    public static String LEFT_OPERAND  = "left";
    public static String RIGHT_OPERAND = "right";
    
    @Override
    public void accept(Visitor visitor){
        getIncomingRules().get(LEFT_OPERAND).accept(visitor);
        getIncomingRules().get(RIGHT_OPERAND).accept(visitor);
        visitor.visit(this);
    }
    
    public BaseSetOperationRefsetRule setLeftRule(RefsetRule rule){
        this.getIncomingRules().put(LEFT_OPERAND, rule);
        return this;
    }
    
    public BaseSetOperationRefsetRule setRightRule(RefsetRule rule){
        this.getIncomingRules().put(RIGHT_OPERAND, rule);
        return this;
    }
    
    public RefsetRule getLeft(){
        return getIncomingRules().get(LEFT_OPERAND);
    }
    
    public RefsetRule getRight(){
        return getIncomingRules().get(RIGHT_OPERAND);
    }
}
