package com.AST;

import com.LexicalAnalysis.Token;

import java.util.List;

/**
 * Created by shayanraouf on 5/14/2017.
 */
public class ListNode extends ExprNode {
    public ListNode(Token t, List<ExprNode> elements) {
        super(t); // track vector token; most likely it's an imaginary token
        evalType = tVECTOR;
        for (ExprNode e : elements) { addChild(e); } // all elements as kids
    }
}