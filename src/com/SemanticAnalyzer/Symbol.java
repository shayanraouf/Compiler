package com.SemanticAnalyzer;

import com.LexicalAnalysis.Token;

/**
 * Created by shayanraouf on 5/18/2017.
 */
public class Symbol {
    public static enum TYPE {ARG, VAR, STATIC, FIELD, NONE};
    private String name;
    private String info;
    private Token token;
    private TYPE type;
    long location;

    public Symbol(Token t){
        this.info = t.getClass().getSimpleName();
        this.name = t.getType();
        this.token = t;
    }

    public String toString(){
        return "Symbol: " + this.name + " " + this.info;
    }




}
