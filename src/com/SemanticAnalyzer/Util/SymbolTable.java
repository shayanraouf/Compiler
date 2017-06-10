/*
  Compiler
  Authors: Shayan Raouf & Josh Trygg
  CSS 448 - Compilers - Bernstein
  SymbolTable.java
 */

package com.SemanticAnalyzer.Util;
import com.LexicalAnalysis.Type;

public class SymbolTable{

    public ScopeNode globalScope = new ScopeNode();
    public ScopeNode localScope = globalScope;

    public ScopeNode push() {
        localScope = new ScopeNode(localScope);
        return localScope;
    }
    public ScopeNode pop() {
        localScope = localScope.getEnclosingScope();
        return localScope;
    }

    public void declareSymbol(String name, Type t) {
        Symbol symbol = new Symbol(name, t);
        if (localScope.lookup(symbol.name) != null) {
            System.err.println("Error: redeclaring symbol " + symbol);
            System.exit(1);
        }
        localScope.define(symbol);
    }

    public void declareSymbol(Symbol symbol) {
        localScope.define(symbol);
    }

    public Symbol resolve(String string){

        ScopeNode lookupScope = localScope;
        Symbol value = lookupScope.lookup(string);
        while (value == null) { // while we haven't found the symbol

            lookupScope = lookupScope.getEnclosingScope(); // gets the parent ptr
            if (lookupScope == null) {
                return null;
            }
            value = lookupScope.lookup(string);
        }
        return value;
    }

}
