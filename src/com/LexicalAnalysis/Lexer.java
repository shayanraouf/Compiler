/*
  Lexical Analyzer
  Authors: Shayan Raouf & Josh Trygg
  CSS 448 - Compilers - Bernstein
  Lexer.java
 */

package com.LexicalAnalysis;


import java.io.*;
import java.util.*;

import com.Util.Intern;


public class Lexer implements Iterable<Token>{
    private Set<String> reservedKeyWords;
    private Reader in;
    private int row, col;
    private char current;
    private boolean readCurrent;
    private StringBuilder sb = new StringBuilder();

    public Lexer(String input){

        try {
            FileInputStream fis = new FileInputStream(input);
            InputStreamReader isr = new InputStreamReader(fis, "UTF8");
            in = new BufferedReader(isr);
            row = 1;
            col = 0;
            readCurrent = true;
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
                    while(in.ready() && token == null){
                        token = getToken();
                    }
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
        while(current == '\n') {
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
                case '~': return OperatorFactory.createOp('~',row,col);
                case '^': return OperatorFactory.createOp('^',row,col);
                case '=':
                    char lookAhead = (char)in.read();
                    if(lookAhead == '='){
                        return new Operator("==", row,col);
                    }
                    else{
                        current = lookAhead;
                        readCurrent = false;
                        return new Operator("=",row,col);
                    }

                case '>':
                    lookAhead = (char)in.read();
                    if(lookAhead == '='){
                        return new Operator(">=",row,col);
                    }
                    else if(lookAhead == '>'){
                        return new Operator(">>",row,col);
                    }
                    else{
                        current = lookAhead;
                        readCurrent = false;
                        return new Operator(">",row,col);
                    }
                case '<':
                    lookAhead = (char)in.read();
                    if(lookAhead == '='){
                        return new Operator("<=",row,col);
                    }
                    else if(lookAhead == '<'){
                        return new Operator("<<",row,col);
                    }
                    else{
                        current = lookAhead;
                        readCurrent = false;
                        return new Operator("<",row,col);
                    }

                case '!':
                    lookAhead = (char)in.read();
                    if(lookAhead == '='){
                        return new Operator("!=",row,col);
                    }
                    else{
                        current = lookAhead;
                        readCurrent = false;
                        return new Operator("!",row,col);
                    }

                case '&':
                    lookAhead = (char)in.read();
                    if(lookAhead == '&'){
                        return new Operator("&&",row,col);
                    }
                    else {
                        current = lookAhead;
                        readCurrent = false;
                        return new Operator("&", row, col);
                    }

                case '|':
                    lookAhead = (char)in.read();
                    if(lookAhead == '|'){
                        return new Logical_OR(row, col);
                    }
                    else {
                        current = lookAhead;
                        readCurrent = false;
                        return new Operator("|",row,col);
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
        char lookAhead = (char)in.read();
        //System.out.println("lookAhead " + lookAhead + " cur " + cur);

        if(lookAhead != '*' && lookAhead != '/'){           // division Operator
            current = lookAhead;
            readCurrent = false;
            return OperatorFactory.createOp('/',r,c);
        }

        if(lookAhead == '/'){                           // regular comments

            int next = in.read();
            lookAhead = (char)next;

            while(next != -1 && lookAhead != '\n'){     // terminates at the end of a line or end of a file
                next = in.read();
                lookAhead = (char)next;
            }
            row++;
            col = 0;
            readCurrent = true;
        }
        else if(lookAhead == '*'){                     // block comments
            int next = in.read();
            lookAhead = (char)next;
            while(true){
                while(lookAhead != '*'){
                    lookAhead = (char)in.read();
                }
                lookAhead = (char)in.read();
                if(lookAhead == '/') break;
            }
        }
        return null;
    }
    private Token identifierToken(char cur, int r, int c, StringBuilder sb) throws IOException{
        sb.append(cur);
        while(in.ready()){      // look a head operation
            char lookAhead = (char)in.read();
            if(!isLetter(lookAhead) && !isDigit(lookAhead)){
                current = lookAhead;
                readCurrent = false;
                break;
            }
            col++;
            sb.append(lookAhead);
        }
        String word = sb.toString();
        Token token;
        if(reservedKeyWords.contains(word)){
            token = new Keyword(sb.toString(),r,c);
        } else if(Intern.internTable.get(word) != null){
            token = Intern.internTable.get(word);
        }
        else{
            token = new Identifier(word,r,c);
            Intern.internTable.put(word,token);
        }
        clearStringBuilder(sb);
        return token;
    }

    private Token digitToken(char cur, int r, int c, StringBuilder sb) throws IOException{
        boolean floatSighting = false;
        sb.append(cur);
        while(in.ready()){
            char lookAhead = (char)in.read();
            if(!isDigit(lookAhead) && lookAhead != '.'){

                current = lookAhead;
                readCurrent = false;
                break;
            }
            if (lookAhead == '.')
            {
                if (floatSighting)
                {
                    current = lookAhead;
                    readCurrent = false;
                    break;
                }
                floatSighting = true;
            }

            col++;
            sb.append(lookAhead);
        }

        if (floatSighting){

            Token token = new Float64(sb.toString(),r,c);
            clearStringBuilder(sb);
            return token;
        }

        Token token = new Int32(sb.toString(),r,c);
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
        reservedKeyWords.add("void");
    }
}