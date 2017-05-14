package com.AbstractSyntaxTree;

/**
 * Created by shayanraouf on 5/14/2017.
 */
public class IfStatement extends Statement {
    Expression expression;
    BlockStatement blockStatement;
    BlockStatement elseBlockStatement;

    public IfStatement(Expression expression, BlockStatement blockStatement, BlockStatement elseBlockStatement) {
        this.expression = expression;
        this.blockStatement = blockStatement;
        this.elseBlockStatement = elseBlockStatement;
    }
}
