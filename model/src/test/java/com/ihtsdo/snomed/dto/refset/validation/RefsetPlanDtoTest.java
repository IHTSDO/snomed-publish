package com.ihtsdo.snomed.dto.refset.validation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.PlanDto;
import com.ihtsdo.snomed.dto.refset.RuleDto;
import com.ihtsdo.snomed.model.refset.BaseRule;
import com.ihtsdo.snomed.model.refset.BaseRule.RuleType;

public class RefsetPlanDtoTest {

    
    @Test
    public void shouldPassValidation(){
        RuleDto terminal = RuleDto.getBuilder()
                .id(2L)
                .type(RuleType.LIST)
                .concepts(Arrays.asList(
                        new ConceptDto(3l), 
                        new ConceptDto(4l)))                        
                .build();
        
        PlanDto plan = PlanDto.getBuilder().
                add(RuleDto.getBuilder()
                        .id(1L)
                        .type(BaseRule.RuleType.LIST)
                        .concepts(Arrays.asList(
                                new ConceptDto(1l), 
                                new ConceptDto(2l)))
                        .build()).
                add(terminal).
                add(RuleDto.getBuilder()
                        .id(3L)
                        .type(RuleType.UNION)
                        .left(1L)
                        .right(2L)
                        .build()).
                build();
        
        ValidationResult expected = ValidationResult.getBuilder().
                terminal(terminal).build();
        
        ValidationResult actual = plan.validate();
        
        assertEquals(expected, actual);
                                   
    }
    
    @Test
    public void shouldFailOnNullOrZeroId(){
        RuleDto terminal = RuleDto.getBuilder()
                .id(null)
                .type(RuleType.LIST)
                .concepts(Arrays.asList(
                        new ConceptDto(3l), 
                        new ConceptDto(4l)))                        
                .build();
        
        PlanDto plan = PlanDto.getBuilder().
                add(terminal).
                build();
        
        String defaultMessage = "Null or zero rule id";
        ValidationResult expected = ValidationResult.getBuilder().
                terminal(terminal).build().
                    addError(ValidationResult.Error.NULL_OR_ZERO_REFSET_RULE_ID, terminal, defaultMessage);
        
        assertEquals(expected, plan.validate());
        
        terminal.setId(0L);
        
        assertEquals(expected, plan.validate());                           
    }
    
    @Test
    public void shouldFailOnNullRuleReference(){
        RuleDto left = RuleDto.getBuilder()
                .id(1L)
                .type(RuleType.LIST)
                .concepts(Arrays.asList(
                        new ConceptDto(1l), 
                        new ConceptDto(2l)))                        
                .build();
        
        RuleDto right = RuleDto.getBuilder()
                .id(2L)
                .type(RuleType.LIST)
                .concepts(Arrays.asList(
                        new ConceptDto(3l), 
                        new ConceptDto(4l)))                        
                .build();
        
        RuleDto union = RuleDto.getBuilder()
                .id(3L)
                .type(RuleType.UNION)
                .left(null)
                .right(2L)
                .build();
        
        PlanDto plan = PlanDto.getBuilder().
                add(left).
                add(right).
                add(union).
                terminal(union.getId()).
                build();
        
        union.setLeft(null);
        
        ValidationResult expected = ValidationResult.getBuilder().
                terminal(union).build()
                    .addError(
                        GlobalValidationError.getBuilder(
                                ValidationResult.Error.MORE_THAN_ONE_UNREFERENCED_RULE_FOR_TERMINAL_CANDIDATE,
                                "Found more than one unreferenced rule. There can only be one unreferenced rule, as the terminal candidate. Unreferenced rules are: [1, 3]").
                            param(Arrays.asList(
                                    left.getId().toString(), 
                                    union.getId().toString()).
                                    toString()).
                            build())
                    .addError(
                        FieldValidationError.getBuilder(
                                ValidationResult.Error.UNCONNECTED_REFSET_RULE,
                                union,
                                "Left rule unconnected").
                            build());
        
        assertEquals(expected, plan.validate()); 
        
        union.setLeft(left.getId());
        union.setRight(null);
        
        expected = ValidationResult.getBuilder().
                terminal(union).build()
                .addError(
                    GlobalValidationError.getBuilder(
                            ValidationResult.Error.MORE_THAN_ONE_UNREFERENCED_RULE_FOR_TERMINAL_CANDIDATE,
                            "Found more than one unreferenced rule. There can only be one unreferenced rule, as the terminal candidate. Unreferenced rules are: [2, 3]").
                        param(Arrays.asList(
                                right.getId().toString(), 
                                union.getId().toString()).
                                toString()).
                        build())
                .addError(
                    FieldValidationError.getBuilder(
                            ValidationResult.Error.UNCONNECTED_REFSET_RULE,
                            union,
                            "Right rule unconnected").
                        build());        

        assertEquals(expected, plan.validate());
    }
    
    
    @Test
    public void shouldFailOnReferencingUndeclaredRule(){
        RuleDto left = RuleDto.getBuilder()
                .id(1L)
                .type(RuleType.LIST)
                .concepts(Arrays.asList(
                        new ConceptDto(1l), 
                        new ConceptDto(2l)))                        
                .build();
        
        RuleDto right = RuleDto.getBuilder()
                .id(2L)
                .type(RuleType.LIST)
                .concepts(Arrays.asList(
                        new ConceptDto(3l), 
                        new ConceptDto(4l)))                        
                .build();
        
        RuleDto union = RuleDto.getBuilder()
                .id(3L)
                .type(RuleType.UNION)
                .left(4L)
                .right(2L)
                .build();
        
        PlanDto plan = PlanDto.getBuilder().
                add(left).
                add(right).
                add(union).
                terminal(union.getId()).
                build();
        
        ValidationResult expected = ValidationResult.getBuilder().
                terminal(union).build()
                    .addError(
                        GlobalValidationError.getBuilder(
                                ValidationResult.Error.MORE_THAN_ONE_UNREFERENCED_RULE_FOR_TERMINAL_CANDIDATE,
                                "Found more than one unreferenced rule. There can only be one unreferenced rule, as the terminal candidate. Unreferenced rules are: [1, 3]").
                            param(Arrays.asList(
                                    left.getId().toString(), 
                                    union.getId().toString()).
                                    toString()).
                            build())
                    .addError(
                        FieldValidationError.getBuilder(
                                ValidationResult.Error.REFERENCING_UNDECLARED_RULE,
                                union,
                                "Referencing undeclared rule: Left rule 4 has not been declared").
                            param("4").
                            build());
        
        assertEquals(expected, plan.validate());        
        
        union.setLeft(1L);
        union.setRight(4L);
        
        expected = ValidationResult.getBuilder().
                terminal(union).build()
                    .addError(
                        GlobalValidationError.getBuilder(
                                ValidationResult.Error.MORE_THAN_ONE_UNREFERENCED_RULE_FOR_TERMINAL_CANDIDATE,
                                "Found more than one unreferenced rule. There can only be one unreferenced rule, as the terminal candidate. Unreferenced rules are: [2, 3]").
                            param(Arrays.asList(
                                    right.getId().toString(), 
                                    union.getId().toString()).
                                    toString()).
                            build())
                    .addError(
                        FieldValidationError.getBuilder(
                                ValidationResult.Error.REFERENCING_UNDECLARED_RULE,
                                union,
                                "Referencing undeclared rule: Right rule 4 has not been declared").
                            param("4").
                            build());
        
        assertEquals(expected, plan.validate());                           
    }
    
    
    @Test
    public void shouldFailOnLeftAndRightOperandReferenceSameRule(){
        RuleDto left = RuleDto.getBuilder()
                .id(1L)
                .type(RuleType.LIST)
                .concepts(Arrays.asList(
                        new ConceptDto(1l), 
                        new ConceptDto(2l)))                        
                .build();
        
        RuleDto union = RuleDto.getBuilder()
                .id(2L)
                .type(RuleType.UNION)
                .left(1L)
                .right(1L)
                .build();
        
        PlanDto plan = PlanDto.getBuilder().
                add(left).
                add(union).
                terminal(union.getId()).
                build();
        
        ValidationResult expected = ValidationResult.getBuilder().
                terminal(union).build()
                    .addError(
                        FieldValidationError.getBuilder(
                                ValidationResult.Error.LEFT_AND_RIGHT_OPERAND_REFERENCE_SAME_RULE,
                                union,
                                "Left and right operand rule references the same rule 1").
                            param("1").
                            build());
        
        assertEquals(expected, plan.validate()); 
        
    }
    
    @Test
    public void shouldFailOnSelfReferencingRule(){
        RuleDto left = RuleDto.getBuilder()
                .id(1L)
                .type(RuleType.LIST)
                .concepts(Arrays.asList(
                        new ConceptDto(1l), 
                        new ConceptDto(2l)))                        
                .build();
        
        RuleDto union = RuleDto.getBuilder()
                .id(2L)
                .type(RuleType.UNION)
                .left(1L)
                .right(2L)
                .build();
        
        PlanDto plan = PlanDto.getBuilder().
                add(left).
                add(union).
                terminal(union.getId()).
                build();
        
        ValidationResult expected = ValidationResult.getBuilder().
                terminal(union).build()
                    .addError(
                        GlobalValidationError.getBuilder(
                                ValidationResult.Error.NO_UNREFERENCED_RULE_FOR_TERMINAL_CANDIDATE,
                                "Unabled to find unreferenced rule to act as terminal candidate")
                            .build())
                    .addError(
                        FieldValidationError.getBuilder(
                                ValidationResult.Error.SELF_REFERENCING_RULE,
                                union,
                                "Right operand references itself")
                            .build());                
        
                
        
        assertEquals(expected, plan.validate()); 
        
    }    
    
    @Test
    public void shouldFailOnRuleReferencedMoreThanOnce(){
        RuleDto r1 = RuleDto.getBuilder()
                .id(1L)
                .type(RuleType.LIST)
                .concepts(Arrays.asList(
                        new ConceptDto(1l), 
                        new ConceptDto(2l)))                        
                .build();
        
        RuleDto r2 = RuleDto.getBuilder()
                .id(2L)
                .type(RuleType.LIST)
                .concepts(Arrays.asList(
                        new ConceptDto(3l), 
                        new ConceptDto(4l)))                        
                .build();
        
        RuleDto r3 = RuleDto.getBuilder()
                .id(3L)
                .type(RuleType.LIST)
                .concepts(Arrays.asList(
                        new ConceptDto(5l), 
                        new ConceptDto(6l)))                        
                .build();        
        
        RuleDto union1 = RuleDto.getBuilder()
                .id(4L)
                .type(RuleType.UNION)
                .left(r1.getId())
                .right(r2.getId())
                .build();
        
        RuleDto union2 = RuleDto.getBuilder()
                .id(5L)
                .type(RuleType.UNION)
                .left(r3.getId())
                .right(r1.getId())
                .build();        

        RuleDto union3 = RuleDto.getBuilder()
                .id(6L)
                .type(RuleType.UNION)
                .left(union1.getId())
                .right(union2.getId())
                .build();           
        
        PlanDto plan = PlanDto.getBuilder().
                add(r1).
                add(r2).
                add(r3).
                add(union1).
                add(union2).
                add(union3).
                terminal(union3.getId()).
                build();
        
        ValidationResult expected = ValidationResult.getBuilder().
                terminal(union3).build()
                    .addError(
                        FieldValidationError.getBuilder(
                                ValidationResult.Error.RULE_REFERENCED_MORE_THAN_ONCE,
                                union2,
                                "Right reference has already been referenced by rule " + r1.getId()).
                            param(r1.getId().toString()).
                            build());
        
        assertEquals(expected, plan.validate()); 
        
    }    

    @Test
    public void shouldFailOnEmptyConceptsList(){
        
        RuleDto left = RuleDto.getBuilder()
                .id(1L)
                .type(RuleType.LIST)
                .concepts(new ArrayList<ConceptDto>())                        
                .build();
        
        RuleDto right = RuleDto.getBuilder()
                .id(2L)
                .type(RuleType.LIST)
                .concepts(Arrays.asList(
                        new ConceptDto(3l), 
                        new ConceptDto(4l)))                        
                .build();
        
        RuleDto union = RuleDto.getBuilder()
                .id(3L)
                .type(RuleType.UNION)
                .left(left.getId())
                .right(right.getId())
                .build();
        
        PlanDto plan = PlanDto.getBuilder().
                add(left).
                add(right).
                add(union).
                terminal(union.getId()).
                build();
        
        ValidationResult expected = ValidationResult.getBuilder().
                terminal(union).build()
//                    .addError(
//                        GlobalValidationError.getBuilder(
//                                ValidationResult.Error.MORE_THAN_ONE_UNREFERENCED_RULE_FOR_TERMINAL_CANDIDATE,
//                                "Found more than one unreferenced rule. There can only be one unreferenced rule, as the terminal candidate. Unreferenced rules are: [1, 3]").
//                            param(Arrays.asList(
//                                    left.getId().toString(), 
//                                    union.getId().toString()).
//                                    toString()).
//                            build())
                    .addError(
                        FieldValidationError.getBuilder(
                                ValidationResult.Error.EMPTY_CONCEPT_LIST,
                                left,
                                "No concepts found").
                            build());
        
        assertEquals(expected, plan.validate());        
                                                   
    }
    
    @Test
    public void shouldFailOnIllegalConceptId(){
        
        RuleDto left = RuleDto.getBuilder()
                .id(1L)
                .type(RuleType.LIST)
                .concepts(Arrays.asList(
                        new ConceptDto(-1l), 
                        new ConceptDto(2l)))                        
                .build();
        
        RuleDto right = RuleDto.getBuilder()
                .id(2L)
                .type(RuleType.LIST)
                .concepts(Arrays.asList(
                        new ConceptDto(3l), 
                        new ConceptDto(4l)))
                .build();
        
        RuleDto union = RuleDto.getBuilder()
                .id(3L)
                .type(RuleType.UNION)
                .left(left.getId())
                .right(right.getId())
                .build();
        
        PlanDto plan = PlanDto.getBuilder().
                add(left).
                add(right).
                add(union).
                terminal(union.getId()).
                build();
        
        ValidationResult expected = ValidationResult.getBuilder().
                terminal(union).build()
                    .addError(
                        FieldValidationError.getBuilder(
                                ValidationResult.Error.INVALID_CONCEPT_ID,
                                left,
                                "Found concept id with illegal identifier <= 0").
                            build());
        
        assertEquals(expected, plan.validate());        
                                                   
    }    
}
