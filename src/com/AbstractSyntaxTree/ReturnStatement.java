package com.AbstractSyntaxTree;

/**
 * Created by shayanraouf on 5/14/2017.
 */
public class ReturnStatement extends Statement{
    Expression expressionToReturn;

    public ReturnStatement(Expression expressionToReturn) {
        this.expressionToReturn = expressionToReturn;
    }
}
