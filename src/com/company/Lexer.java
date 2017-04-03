package com.company;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
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
    private Reader in;
    private int row = 1;
    private int col = 0;
    private char current;
    private char lookAHead;
    private boolean readCurrent = true;
    private StringBuilder sb = new StringBuilder();

    public Lexer(String input){

        try {
            FileInputStream fis = new FileInputStream(input);
            InputStreamReader isr = new InputStreamReader(fis, "UTF8");
            in = new BufferedReader(isr);
            initializeMap();
        }
        catch (IOException e) {
            e.printStackTrace();

        }

    }
    public void setCurrent(char c){
        current = c;
    }
    public void setLookAHead(char c){
        lookAHead = c;
    }
    @Override
    public Iterator<Token> iterator(){
        Iterator<Token> it = new Iterator<Token>(){
            @Override
            public boolean hasNext(){
                try{

                    return in.ready();

                }
                catch (IOException e){
                    e.printStackTrace();
                    return false;
                }
            }
            @Override
            public Token next(){
                try{

                    return getToken();
                }
                catch (IOException e){
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }
    private Token getToken() throws IOException{
            if(readCurrent) current = (char)in.read();
            while(current == ' ' || current == '\n' || current == '\r' || current == '\t' || current == '\b') current = (char)in.read();
            col++;
            if(isLetter(current))return identifierToken(current,row,col,sb);
            if(isDigit(current))return digitToken(current,row,col,sb);
            readCurrent = true;                // reset flag to true
            switch (current){
                case '(': return new Operator("(",row,col);
                case ')': return new Operator(")",row,col);
                case ' ': col--;
                          break;
                case '\n': col = 0;
                           row++;
                           break;
                case '[': return new Operator("[",row,col);
                case ']': return new Operator("]",row,col);
                case '{': return new Operator("{",row,col);
                case '}': return new Operator("}",row,col);
                case ',': return new Operator(",",row,col);
                case ';': return new Operator(";",row,col);
                case '+': return new Operator("+",row,col);
                case '-': return new Operator("-",row,col);
                case '*': return new Operator("*",row,col);
                case '/': return new Operator("/",row,col);
                // TODO: 4/2/2017 comments 
                case '~': return new Operator("~",row,col);
                case '=': return new Operator("=",row,col);
                case '^': return new Operator("^",row,col);
                case '>':
                    lookAHead = (char)in.read();
                    if(lookAHead == '='){
                        return new Operator(">=",row,col);
                    }
                    else if(lookAHead == '>'){
                        return new Operator(">>",row,col);
                    }
                    else{
                        current = lookAHead;
                        readCurrent = false;
                        return new Operator(">",row,col);
                    }
                    // case for >=
                case '<':
                    // TODO: 3/30/2017
                    // case for <=
                case '!':
                    // TODO: 3/30/2017
                    // case for !=
                case '&':
                    // TODO: 4/2/2017
                    break;
                case '|':
                    // TODO: 4/2/2017
                    break;
                 default:
                     break;
                     // TODO: 3/30/2017
                     // handle error
            }
        return null;
    }
    
    private void comments(){
        // TODO: 4/2/2017 handles comments and block comments  
    }

    private Token identifierToken(char cur, int r, int c, StringBuilder sb) throws IOException{
        sb.append(cur);
        while(in.ready()){      // look a head operation
            lookAHead = (char)in.read();
            if(!isLetter(lookAHead) && !isDigit(lookAHead)){
                setCurrent(lookAHead);
                readCurrent = false;
                break;
            }
            sb.append(lookAHead);
        }
        String word = sb.toString();
        Token token;
        if(reservedKeyWords.contains(word)){
            token = new Keyword(sb.toString(),r,c);
        } else{
            token = new Identifier(sb.toString(),r,c);
        }
        clearStringBuilder(sb);
        return token;
    }

    private Token digitToken(char cur, int r, int c, StringBuilder sb) throws IOException{
        sb.append(cur);
        while(in.ready()){
            lookAHead = (char)in.read();
            if(!isDigit(lookAHead)){
                setCurrent(lookAHead);
                readCurrent = false;
                break;
            }
            sb.append(lookAHead);
        }
        Token token = new Number(sb.toString(),r,c);
        clearStringBuilder(sb);
        return token;
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

