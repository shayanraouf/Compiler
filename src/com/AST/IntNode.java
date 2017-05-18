package com.AST;

import com.LexicalAnalysis.Token;

/**
 * Created by shayanraouf on 5/14/2017.
 */
public class IntNode extends ExprNode {
    public IntNode(Token t) {
        super(t);
        evalType = tINTEGER;
    }
}