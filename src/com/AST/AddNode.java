/*
  Compiler
  Authors: Shayan Raouf & Josh Trygg
  CSS 448 - Compilers - Bernstein
  AddNode.java
 */

package com.AST;
import com.LexicalAnalysis.Token;


public class AddNode extends ExprNode {

    public AddNode(ExprNode left, Token addToken, ExprNode right) {
        super(addToken);
        addChild(left);
        addChild(right);
    }
    public int getEvalType() { // ...
        ExprNode left = (ExprNode)children.get(0);
        ExprNode right = (ExprNode)children.get(1);
        if ( left.getEvalType()==tINTEGER && right.getEvalType()==tINTEGER ) {
            return tINTEGER;
        }
        if ( left.getEvalType()==tVECTOR && right.getEvalType()==tVECTOR ) {
            return tVECTOR;
        }
        return tINVALID;
    }
}