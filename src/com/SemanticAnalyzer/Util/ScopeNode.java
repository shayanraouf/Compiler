/*
  Compiler
  Authors: Shayan Raouf & Josh Trygg
  CSS 448 - Compilers - Bernstein
  ScopeNode.java
 */

package com.SemanticAnalyzer.Util;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ScopeNode{

    public ScopeNode parentPtr = null;
    public Map<String, Symbol> table = new HashMap<>();

    public ScopeNode(){
        this.parentPtr = null;
    }

    public ScopeNode(ScopeNode parentPtr){
        this.parentPtr = parentPtr;
    }

    public void define(Symbol symbol){
        table.put(symbol.name,symbol);
    }

    public Symbol lookup(String str){
        return table.get(str);
    }

    public ScopeNode getEnclosingScope(){
        return parentPtr;
    }
}
