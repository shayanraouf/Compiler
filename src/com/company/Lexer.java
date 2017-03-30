package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by shayanraouf on 3/28/2017.
 */
public class Lexer {
    private Scanner scanner;
    private Token token;
    private Map<String, Token> map;

    public Lexer(String filename){
        try{
            scanner = new Scanner(new File(filename));
        }
        catch (FileNotFoundException e){
            System.err.println("File Not Found " + e);
            System.exit(-1);
        }
    }

    public Lexer(Scanner inputScanner){
        scanner = inputScanner;
    }

    public Token run(){



        return token;
    }

    public void initializeMap(){

    }



}

interface Token{

}


class Keyword {
    private String keyword;

    public Keyword(String s){
        keyword = s;
    }

    @Override
    public String toString(){
        return keyword;
    }
}

class Operator{
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
