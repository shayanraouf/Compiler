/*
  Lexical Analyzer
  Authors: Shayan Raouf & Josh Trygg
  CSS 448 - Compilers - Bernstein
  Lexer.java
 */

package com.company;
import java.io.*;
import java.util.*;


class Lexer implements Iterable<Token>{
    private Set<String> reservedKeyWords;
    private Map<Integer,String> symbolTable;
    private Reader in;
    private int row = 1;
    private int col = 0;
    private char current;
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
                    Token token = getToken();
                    return token;
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
        while(current == ' ' || current == '\r') current = (char)in.read();
        if(current == '\n') {
            current = (char)in.read();
            row++;
            col = 0;
        }
            col++;
            if(current == '/'){
                Token token = comments(current,row,col);
                if(token != null) return token;
            }
            if(isLetter(current))return identifierToken(current,row,col,sb);
            if(isDigit(current))return digitToken(current,row,col,sb);
            readCurrent = true;                // reset flag to true
            switch (current){
                case '(': return new LeftParanthesis(row,col);
                case ')': return new RightParanthesis(row,col);
                case '[': return new LeftBracket(row,col);
                case ']': return new RightBracket(row,col);
                case '{': return new LeftBrace(row,col);
                case '}': return new RightBrace(row,col);
                case ',': return new Comma(row,col);
                case ';': return new SemiColon(row,col);
                case '+': return OperatorFactory.createOp('+',row,col);
                case '-': return OperatorFactory.createOp('-',row,col);
                case '*': return OperatorFactory.createOp('*',row,col);
                case '~': return new Operator("~",row,col);
                case '^': return new Operator("^",row,col);
                case '=':
                    char lookAHead = (char)in.read();
                    if(lookAHead == '='){
                        return new Operator("==", row,col);
                    }
                    else{
                        current = lookAHead;
                        readCurrent = false;
                        return new Operator("=",row,col);
                    }

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
                case '<':
                    lookAHead = (char)in.read();
                    if(lookAHead == '='){
                        return new Operator("<=",row,col);
                    }
                    else if(lookAHead == '<'){
                        return new Operator("<<",row,col);
                    }
                    else{
                        current = lookAHead;
                        readCurrent = false;
                        return new Operator("<",row,col);
                    }

                case '!':
                    lookAHead = (char)in.read();
                    if(lookAHead == '='){
                        return new Operator("!=",row,col);
                    }
                    else{
                        current = lookAHead;
                        readCurrent = false;
                        return new Operator("!",row,col);
                    }

                case '&':
                    lookAHead = (char)in.read();
                    if(lookAHead == '&'){
                        return new Operator("&&",row,col);
                    }
                    else {
                        current = lookAHead;
                        readCurrent = false;
                        return new Operator("&", row, col);
                    }

                case '|':
                    lookAHead = (char)in.read();
                    if(lookAHead == '|'){
                        return new Operator("||",row,col);
                    }
                    else {
                        current = lookAHead;
                        readCurrent = false;
                        return new Operator("|", row, col);
                    }

                default:
                     break;
                     // TODO: 3/30/2017
                     // handle error
            }
        return null;
    }

    // returns null if comment or block comment
    private Token comments(char cur, int r, int c)throws IOException{
        char lookAHead = (char)in.read();
        //System.out.println("lookAHead " + lookAHead + " cur " + cur);

        if(lookAHead != '*' && lookAHead != '/'){           // division Operator
            current = lookAHead;
            readCurrent = false;
            return OperatorFactory.createOp('/',r,c);
        }

        if(lookAHead == '/'){                           // regular comments

            int next = in.read();
            lookAHead = (char)next;

            while(next != -1 && lookAHead != '\n'){     // terminates at the end of a line or end of a file
                next = in.read();
                lookAHead = (char)next;
            }
            row++;
            col = 0;
            readCurrent = true;
        }
        else if(lookAHead == '*'){                     // block comments
            int next = in.read();
            lookAHead = (char)next;
            while(true){
                while(lookAHead != '*'){
                    lookAHead = (char)in.read();
                }
                lookAHead = (char)in.read();
                if(lookAHead == '/') break;
            }
        }
        return null;
    }
    private Token identifierToken(char cur, int r, int c, StringBuilder sb) throws IOException{
        sb.append(cur);
        while(in.ready()){      // look a head operation
            char lookAHead = (char)in.read();
            if(!isLetter(lookAHead) && !isDigit(lookAHead)){
                current = lookAHead;
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
            char lookAHead = (char)in.read();
            if(!isDigit(lookAHead)){
                current = lookAHead;
                readCurrent = false;
                break;
            }
            /*
             // Need to implement logic for floating point numbers
            else if(lookAHead == '.')

               decimal = true;
             */

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

        //Adding all the operators
        reservedKeyWords.add("!");
        reservedKeyWords.add("~");
        reservedKeyWords.add("*");
        reservedKeyWords.add("/");
        reservedKeyWords.add("+");
        reservedKeyWords.add("-");

        reservedKeyWords.add("&");
        reservedKeyWords.add("|");
        reservedKeyWords.add("^");

        reservedKeyWords.add(">");
        reservedKeyWords.add("<");
        reservedKeyWords.add(">=");
        reservedKeyWords.add("<=");
        reservedKeyWords.add("==");
        reservedKeyWords.add("!=");


        reservedKeyWords.add("for");
        reservedKeyWords.add("this");

        reservedKeyWords.add("byte");
        reservedKeyWords.add("const");
        reservedKeyWords.add("else");
        reservedKeyWords.add("end");
        reservedKeyWords.add("exit");
        reservedKeyWords.add("float64");
        reservedKeyWords.add("for");
        reservedKeyWords.add("function");
        reservedKeyWords.add("if");
        reservedKeyWords.add("int32");
        reservedKeyWords.add("print");
        reservedKeyWords.add("record");
        reservedKeyWords.add("ref");
        reservedKeyWords.add("return");
        reservedKeyWords.add("static");
        reservedKeyWords.add("type");
        reservedKeyWords.add("var");
        reservedKeyWords.add("while");
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
        return super.toString();
    }

    
}

class OperatorFactory{
    public static Operator createOp(char ch, int r, int c){
        Operator operator = null;

        switch (ch){
            case '+':
                operator = new Addition(r,c);
                break;
            case '-':
                operator = new Subtraction(r,c);
                break;
            case '*':
                operator = new Multiplication(r,c);
                break;
            case '/':
                operator = new Division(r,c);
                break;
            default:
                throw new IllegalArgumentException("Not an Operator");
        }
        return operator;
    }
}

class Addition extends Operator{
    public Addition(int r, int c){
        super("+",r,c);
    }
}

class Subtraction extends Operator{
    public Subtraction(int r, int c){
        super("-",r,c);
    }
}

class Multiplication extends Operator{
    public Multiplication(int r, int c){
        super("*",r,c);
    }
}



class Division extends Operator{
    public Division(int r, int c){
        super("/",r,c);
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
    
    public Comma( int r, int c){
        super(",",r,c);
    }
}


class SemiColon extends Token{

    public SemiColon(int r, int c){
        super(";",r,c);
    }
}


class LeftParanthesis extends Token{
    public LeftParanthesis(int r, int c){
        super("(",r,c);
    }

}

class RightParanthesis extends Token{

    public RightParanthesis(int r, int c) {
        super(")",r,c);
    }
}

class LeftBracket extends Token{

    public LeftBracket(int r, int c){
        super("[",r,c);
    }
}

class RightBracket extends Token{
    
    public RightBracket(int r, int c){
        super("]",r,c);
    }
}


class LeftBrace extends Token{

    public LeftBrace(int r, int c){
        super("{",r,c);
    }
}

class RightBrace extends Token{
    
    public RightBrace(int r, int c){
        super("}",r,c);
    }
}

