package com.SemanticAnalyzer.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by shayanraouf on 5/19/2017.
 */
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
        //symbol.scope = this; // track the scope in each symbol

    }

    public Symbol lookup(String str){
        return table.get(str);
    }

    public ScopeNode getEnclosingScope(){
        return parentPtr;
    }




    /** Look up name in this scope or in enclosing scope if not here */
//    public Symbol resolve(String name){
//        Symbol s = table.get(name);
//        if ( s!=null ) return s;
//        // if not here, check any enclosing scope
//        if ( parentPtr != null ) return parentPtr.resolve(name);
//        return null; // not found
//    }
}
