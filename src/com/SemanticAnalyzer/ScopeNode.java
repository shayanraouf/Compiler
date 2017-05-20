package com.SemanticAnalyzer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shayanraouf on 5/19/2017.
 */
public class ScopeNode {

    private ScopeNode parentPtr = null;
    Map<String, Symbol> table = new HashMap<>();

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

    public ScopeNode getParent(){
        return parentPtr;
    }
}
