/*
  Compiler
  Authors: Shayan Raouf & Josh Trygg
  CSS 448 - Compilers - Bernstein
  IntNode.java
 */

package com.AST;

import com.LexicalAnalysis.Token;

public class IntNode extends ExprNode {
    public IntNode(Token t) {
        super(t);
        evalType = tINTEGER;
    }
}