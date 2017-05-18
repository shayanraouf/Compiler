package com.AbstractSyntaxTree;

/**
 * Created by shayanraouf on 5/14/2017.
 */
public class ExpressionNode extends Expression{
    OperatorUtil operator;

    public ExpressionNode(OperatorUtil operator){
        this.operator = operator;
    }

    public ExpressionNode(OperatorUtil operator, Expression left, Expression right) {
        super(left, right);
        this.operator = operator;
    }
}
