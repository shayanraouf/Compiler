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
    char[] ASCII = {'(', ')','[',']'};


    public Lexer(String input){
        this.input = input;
    }

    public void print(){
       for(Token t: tokens){
           System.out.println(t);
       }
    }


    public void run(){
        tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int row = 1;
        int col = 1;
        for(int i = 1; i < input.length(); i++){ // for loop
            col++;
            char current = input.charAt(i);
            char lookAHead = input.charAt(i);
            
            if(isLetter(current)){
                // TODO: 3/30/2017  
                continue;
            }
            
            if(isDigit(current)){
                // TODO: 3/30/2017  
                continue;
            }
            
            
            switch (current){

                case '(':
                    tokens.add(new Operator("(",row,col));
                    break;
                case ')':
                    tokens.add(new Operator(")",row, col));
                    break;
                case ' ':
                    continue;

                case '\n':
                    col = 1;
                    row++;
                    continue;
                case '[':
                case ']':
                case '{':
                case '}':
                case ',':
                case ';':

                case '+':
                case '-':
                case '*':
                case '/':
                case '~':
                case '=':
                case '>':
                    // TODO: 3/30/2017
                    // case for >=

                case '<':
                    // TODO: 3/30/2017
                    // case for <=

                case '!':
                    // TODO: 3/30/2017
                    // case for !=
                case '&':
                    // TODO: 3/30/2017
                case '|':
                    // TODO: 3/30/2017

                 // TODO: 3/30/2017
                 // case letter

                // TODO: 3/30/2017
                // case letter


                 default:
                     break;
                     // TODO: 3/30/2017
                     // handle error

            }

        }


    }

    private void clearStringBuilder(StringBuilder sb){
        sb.setLength(0);
    }

    private boolean isLetter(char c){
        return false;
    }

    private boolean isDigit(char c){
        return false;
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
    private int row;
    private int col;
    public Operator(String s, int r, int c){
        operator = s;
        row = r;
        col = c;
    }

    public Operator(String s){
        operator = s;
    }


    @Override
    public String toString(){
        return "[" + row + "," + col + "]" + " " + operator;
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
