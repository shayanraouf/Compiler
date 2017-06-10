/*
  Compiler
  Authors: Shayan Raouf & Josh Trygg
  CSS 448 - Compilers - Bernstein
  Node.java
 */

package com.AST;

import com.LexicalAnalysis.Token;

public class Node extends ExprNode{
    public Node(Token payload) {
        super(payload);
    }
}
