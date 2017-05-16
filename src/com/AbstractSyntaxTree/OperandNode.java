package com.AbstractSyntaxTree;

import com.LexicalAnalysis.Token;

/**
 * Created by shayanraouf on 5/14/2017.
 */
public class OperandNode extends Expression{
    Token tokenOperator;
    public OperandNode(Token token){
        tokenOperator = token;
    }

}
