package com.SemanticAnalyzer.Util;

import com.LexicalAnalysis.Token;
import com.LexicalAnalysis.Type;

/**
 * Created by shayanraouf on 5/19/2017.
 */
public class Symbol {
    public Type TYPE;
    String name;
    String info;
    Token token;
    Scope scope;
    String genType;
    String codeGenType;


    public Symbol(Token token){
        this.name = token.getClass().getSimpleName();
    }

    public Symbol(String str,Type t){
        //this.info = token.getClass().getSimpleName();
        this.TYPE = t;
        this.name = str;
        //this.name = token.getClass().getSimpleName();
    }

    public Symbol(String codeLabel, String codeType, Type t){
        this.name = codeLabel;
        this.codeGenType = codeType;
        TYPE = t;
    }
    public Symbol(String code, String type){
        this.codeGenType = type;
        this.name = code;
    }
    public Symbol(Token token, Type t){
        this.name = token.getClass().getSimpleName();
        TYPE = t;
    }

    public String toString(){
        return "<" + name + " : " + TYPE + ">";
    }

    public String getGenType(){
        return this.codeGenType;
    }
    public String getName(){
        return this.name;
    }
}
