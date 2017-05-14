package com.AbstractSyntaxTree;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public class ForStatement extends Statement {
    LValue              lValue;
    Expression          expr1;
    Expression          expr2;
    Expression          expr3;
    BlockStatement      statements;
    String              exitLabel;

    public ForStatement(Expression expr1, Expression expr2, Expression expr3, BlockStatement statements) {
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.expr3 = expr3;
        this.statements = statements;
    }
}