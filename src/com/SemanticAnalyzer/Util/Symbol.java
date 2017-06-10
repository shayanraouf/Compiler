/*
  Compiler
  Authors: Shayan Raouf & Josh Trygg
  CSS 448 - Compilers - Bernstein
  Symbol.java
 */

package com.SemanticAnalyzer.Util;

import com.LexicalAnalysis.Token;
import com.LexicalAnalysis.Type;

import java.util.ArrayList;
import java.util.List;

public class Symbol {
    public Type TYPE;
    public String alias;
    String name;
    String genType;
    String codeGenType;
    public List<String> function_param_ids = new ArrayList<>();

    public Symbol(Token token){
        this.name = token.getClass().getSimpleName();
    }

    public Symbol(String str,Type t){
        this.TYPE = t;
        this.name = str;
    }

    public Symbol(String codeLabel, String codeType, Type t){
        this.name = codeLabel;
        this.codeGenType = codeType;
        TYPE = t;
        this.alias = codeLabel;
    }

    public Symbol(String codeLabel, String codeType, Type t, String alias){
        this.name = codeLabel;
        this.codeGenType = codeType;
        TYPE = t;
        this.alias = alias;
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
