package com.AST;

/**
 * Created by shayanraouf on 5/14/2017.
 */
import com.LexicalAnalysis.Token;

import java.util.List;
public class VectorNode extends ExprNode {
    public VectorNode(Token t, List<ExprNode> elements) {
        super(t); // track vector token; likely to be imaginary token
        evalType = tVECTOR;
        for (ExprNode e : elements) { addChild(e); } // add as kids
    }
}