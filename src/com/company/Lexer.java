package com.company;

import java.security.Key;
import java.util.*;

/**
 * Created by shayanraouf on 3/28/2017.
 */
class Lexer implements Iterable<Token>{

    private String input;
    private List<Token> tokens;
    private Set<String> reservedKeyWords;
    private Map<Integer,String> symbolTable;


    public Lexer(String input){
        this.input = input;
        tokenize();
    }


    @Override
    public Iterator<Token> iterator(){
        Iterator<Token> it = new Iterator<Token>(){
            private int elementIndex = 0;

            @Override
            public boolean hasNext() {
                return elementIndex < tokens.size();
            }

            @Override
            public Token next() {
                return tokens.get(elementIndex++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }



    private void tokenize(){
        initializeMap();
        StringBuilder sb = new StringBuilder();
        StringBuilder sb_digit = new StringBuilder();
        int row = 1;
        int col = 0;


        boolean lookAHead = true;

        for(int i = 0; i < input.length(); i++){ // for loop
            col++;
            char current = input.charAt(i);
            if(i + 1 >= input.length()) lookAHead = false;


            if(isLetter(current)){
                letterTokenizer(current,lookAHead,i,row,col,sb);
                continue;
            }
            
            if(isDigit(current)){

                if(sb.length() != 0){                                            // digit is a part of an identifier
                    letterTokenizer(current,lookAHead,i,row,col,sb);
                }
                else{                                                            // digit is a number
                    digitTokenizer(current,lookAHead,i,row,col,sb_digit);
                }

                continue;
            }


            switch (current){

                case '(': tokens.add(new Operator("(",row,col));
                          break;
                case ')': tokens.add(new Operator(")",row,col));
                          break;
                case ' ': col--;
                          break;
                case '\n': col = 0;
                           row++;
                           break;
                case '[': tokens.add(new Operator("[",row,col));
                          break;
                case ']': tokens.add(new Operator("]",row,col));
                          break;
                case '{': tokens.add(new Operator("{",row,col));
                          break;
                case '}': tokens.add(new Operator("}",row,col));
                          break;
                case ',': tokens.add(new Operator(",",row,col));
                          break;
                case ';': tokens.add(new Operator(";",row,col));
                          break;

                case '+': tokens.add(new Operator("+",row,col));
                          break;
                case '-': tokens.add(new Operator("-",row,col));
                          break;
                case '*': tokens.add(new Operator("*",row,col));
                          break;
                case '/': tokens.add(new Operator("/",row,col));
                          break;
                case '~': tokens.add(new Operator("~",row,col));
                          break;
                case '=': tokens.add(new Operator("=",row,col));
                          break;
                case '^': tokens.add(new Operator("^",row,col));
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
                    if(lookAHead){
                        char next = input.charAt(i + 1);
                        if(next == '&'){
                            tokens.add(new Operator("&&",row,col));
                            i++;
                        }
                        else{
                            tokens.add(new Operator("&",row,col));
                        }
                    }
                    else{
                        tokens.add(new Operator("&",row,col));

                    }
                    break;
                case '|':
                    if(lookAHead){
                        char next = input.charAt(i + 1);
                        if(next == '|'){
                            tokens.add(new Operator("||",row,col));
                            i++;
                        }
                        else{
                            tokens.add(new Operator("|",row,col));
                        }
                    }
                    else{
                        tokens.add(new Operator("|",row,col));
                    }
                    break;
                 default:
                     break;
                     // TODO: 3/30/2017
                     // handle error

            }

        }
    }

    private void letterTokenizer(char current, boolean lookAHead,int i, int row, int col, StringBuilder sb){
        sb.append(current);
        if(lookAHead){

            char nextChar = input.charAt(i + 1);            // if next char is a letter
            if(!isLetter(nextChar) && !isDigit(nextChar)){

                String word = sb.toString();
                if(reservedKeyWords.contains(word)){        // keyword
                    tokens.add(new Keyword(word,row,col));
                }
                else{                                       // identifier
                    tokens.add(new Identifier(word,row,col));
                }

                clearStringBuilder(sb);                     // reset the string builder to empty
            }

        }
        else{
            // at the very last character
            String word = sb.toString();
            if(reservedKeyWords.contains(word)){            // keyword
                tokens.add(new Keyword(word,row,col));
            }
            else{                                           // identifier
                tokens.add(new Identifier(word,row,col));
            }
            clearStringBuilder(sb);                     // reset the string builder to empty
        }

    }


    private void digitTokenizer(char current, boolean lookAHead,int i, int row, int col, StringBuilder sb){

        sb.append(current);
        if(lookAHead){
            char next = input.charAt(i + 1);
            if(!isDigit(next)){
                String number = sb.toString();
                tokens.add(new Number(number,row,col));

                clearStringBuilder(sb);                     // reset the string builder to empty
            }
        }
        else{
            String number = sb.toString();
            tokens.add(new Number(number,row,col));

            clearStringBuilder(sb);                     // reset the string builder to empty

        }
    }

    private void clearStringBuilder(StringBuilder sb){
        sb.setLength(0);
    }

    private boolean isLetter(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c){
        return (c >= '0' && c <= '9');
    }




    public void initializeMap(){
        reservedKeyWords = new HashSet<>();
        symbolTable = new HashMap<>();
        tokens = new ArrayList<>();

        //Adding all the operators
        reservedKeyWords.add("!");
        reservedKeyWords.add("!=");
        reservedKeyWords.add("+");
        reservedKeyWords.add("-");


        reservedKeyWords.add("*");
        reservedKeyWords.add("/");

        reservedKeyWords.add("&");
        reservedKeyWords.add("|");
        reservedKeyWords.add("~");

        reservedKeyWords.add(">");
        reservedKeyWords.add("<");
        reservedKeyWords.add(">=");
        reservedKeyWords.add("<=");

        reservedKeyWords.add("for");
        reservedKeyWords.add("this");
        reservedKeyWords.add("if");
        reservedKeyWords.add("else");
        reservedKeyWords.add("null");

    }

}


class Number extends Token{

    public Number(String s, int r, int c){
        super(s,r,c);
    }

    @Override
    public String toString(){
        return super.toString() + " Number (" + text + ")";
    }

}



class Keyword extends Token{

    public Keyword(String s, int r, int c){
        super(s,r,c);
    }

    @Override
    public String toString(){
        return super.toString() + " Keyword (" + text + ")";
    }

}

class Operator extends Token{

    public Operator(String s, int r, int c){
        super(s,r,c);

    }

    @Override
    public String toString(){
        return super.toString() + " " + TokenName.names.get(super.text);
    }

    
}

class Identifier extends Token{
    public Identifier(String s,int r, int c){
        super(s,r,c);
    }

    @Override
    public String toString(){
        return super.toString() + " IDENTIFIER (" + text + ")";
    }


}

class Comma extends Token{
    
    public Comma(String s, int r, int c){
        super(s,r,c);
    }
    
}



class SemiColon extends Token{

    public SemiColon(String s, int r, int c){
        super(s,r,c);
    }
}


class LeftParanthesis extends Token{
    public LeftParanthesis(String s, int r, int c){
        super(s,r,c);
    }

}

class RightParanthesis extends Token{

    public RightParanthesis(String s, int r, int c) {
        super(s,r,c);
    }
}

class LeftBracket extends Token{

    public LeftBracket(String s, int r, int c){
        super(s,r,c);
    }
}

class RightBracket extends Token{
    
    public RightBracket(String s, int r, int c){
        super(s,r,c);
    }
}


class LeftBrace extends Token{

    public LeftBrace(String s, int r, int c){
        super(s,r,c);
    }
}

class RightBrace extends Token{
    
    public RightBrace(String s, int r, int c){
        super(s,r,c);
    }
}

