package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by shayanraouf on 3/28/2017.
 */
class Lexer {

    public static void main(String[] args){
        Variable a = new Variable("foo");
        Variable b = new Variable("foo");
        System.out.println(a.hash());
        System.out.println(b.hash());
        System.out.println(a.hashCode());
        System.out.println(b.hashCode());
    }

    private String input;
    private List<Token> tokens;
    private Map<String, Token> reservedKeyWords;
    private Map<Integer,String> symbolTable;

    public Lexer(String input){
        this.input = input;
    }


    public Token run(){

        int k = 10;
        for(int i = 0; i < input.length(); i++){
            char ch = input.charAt(i);
            System.out.print(ch);
            if(i % 20 == 0){
                System.out.print("\n");
            }
        }

        return null;
    }



    public void initializeMap(){
        reservedKeyWords = new HashMap<>();
        symbolTable = new HashMap<>();
        tokens = new ArrayList<>();

        //Adding all the operators
        reservedKeyWords.put("!", new Operator("!"));
        reservedKeyWords.put("!=", new Operator("!="));
        reservedKeyWords.put("+", new Operator("+"));
        reservedKeyWords.put("-", new Operator("-"));


        reservedKeyWords.put("*", new Operator("*"));
        reservedKeyWords.put("/", new Operator("/"));

        reservedKeyWords.put("&", new Operator("&"));
        reservedKeyWords.put("|", new Operator("|"));
        reservedKeyWords.put("~", new Operator("~"));

        reservedKeyWords.put(">", new Operator(">"));
        reservedKeyWords.put("<", new Operator("<"));
        reservedKeyWords.put(">=", new Operator(">="));
        reservedKeyWords.put("<=", new Operator("<="));

        reservedKeyWords.put("for", new Keyword("for"));
        reservedKeyWords.put("this", new Keyword("this"));
        reservedKeyWords.put("if", new Keyword("if"));
        reservedKeyWords.put("else", new Keyword("else"));
        reservedKeyWords.put("null", new Keyword("null"));

    }



}




class Keyword implements Token{
    private String keyword;

    public Keyword(String s){
        keyword = s;
    }

    @Override
    public String toString(){
        return keyword;
    }
}

class Operator implements Token{
    private String operator;
    public Operator(String s){
        operator = s;
    }

    @Override
    public String toString(){
        return operator;
    }
}

class Variable implements Token{
    private String var;
    public Variable(String v){
        var = v;
    }
    @Override
    public String toString(){
        return var;
    }

    public int hash(){
        return Math.abs(var.hashCode());
    }

}

class Comma implements Token{
    @Override
    public String toString(){
        return ",";
    }
    public Comma(){}
}



class SemiColon implements Token{
    @Override
    public String toString(){
        return ";";
    }
    public SemiColon(){}
}


class LeftParanthesis implements Token{
    @Override
    public String toString(){
        return "(";
    }
    public LeftParanthesis(){}
}

class RightParanthesis implements Token{

    @Override
    public String toString(){
        return ")";
    }
    public RightParanthesis() {
    }
}

class LeftBracket implements Token{

    @Override
    public String toString(){
        return "[";
    }
    public LeftBracket(){}
}

class RightBracket implements Token{

    @Override
    public String toString(){
        return "]";
    }
    public RightBracket(){}
}


class LeftBrace implements Token{
    @Override
    public String toString(){
        return "{";
    }

    public LeftBrace(){}
}

class RightBrace implements Token{

    @Override
    public String toString(){
        return "}";
    }
    public RightBrace(){}
}
