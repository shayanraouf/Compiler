package com.AbstractSyntaxTree;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public class ForStatement extends Statement {
    LValue              lValue;
    Expression          expr1;
    Expression          expr2;
    Expression          expr3;
    Statement           statements;
    String              exitLabel;
    public ForStatement(){}
}