package com.SemanticAnalyzer;

import com.LexicalAnalysis.Token;

/**
 * Created by shayanraouf on 5/19/2017.
 */
public class SymbolTable{

    ScopeNode globalScope = new ScopeNode();
    ScopeNode localScope = globalScope;

    public ScopeNode push() {
        localScope = new ScopeNode(localScope);
        return localScope;
    }
    public ScopeNode pop() {
        localScope = localScope.getParent();
        return localScope;
    }

    public void declareSymbol(Token token) {
        Symbol symbol = new Symbol(token);
        if (localScope.lookup(symbol.name) != null) {
            System.err.println("Error: redeclaring symbol " + symbol);
            System.exit(1);
        }
        localScope.define(symbol);
    }


    public Symbol lookupSymbol(String string) {
        ScopeNode lookupScope = localScope;
        Symbol value = lookupScope.lookup(string);
        while (value == null) {
            lookupScope = lookupScope.getParent();
            if (lookupScope == null) {
                System.err.println("Error (lookup): symbol '" + string + "' not declared.");

                System.exit(1);
                return null;
            }
            value = lookupScope.lookup(string);
        }
        return value;
    }


//    public void updateSymbol(String symbol, Integer initValue) {
//        // find the scope where the symbol was declared
//        SymbolTableScope lookupScope = currentScope;
//        Integer value = lookupScope.lookupSymbol(symbol);
//        // if not in current scope search up the stack
//        while (value == null) {
//            lookupScope = lookupScope.getParentScope();
//            if (lookupScope == null) {
//                // no parent scope, symbol not found
//                System.err.println("Error (update): symbol '"+symbol+"' not declared.");
//                // could do some more intelligent recovery here.
//                System.exit(1);
//            }
//            value = lookupScope.lookupSymbol(symbol);
//        }
//        // we found a scope where symbol is defined, update it
//        lookupScope.enterSymbol(symbol,initValue);
//    }
//}
}
