package com.SemanticAnalyzer;

import com.LexicalAnalysis.Token;

/**
 * Created by shayanraouf on 5/19/2017.
 */
public class Symbol {
    public static enum TYPE {VARIABLE,STATIC,FIELD, ARGUMENT,NONE}
    String name;
    String info;
    Token token;
    long location;

    public Symbol(Token token){
        this.name = token.getClass().getSimpleName();
    }

    public String toString(){
        return "<" + name + " : " + info + ">";
    }
}
