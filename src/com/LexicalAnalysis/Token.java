/*
  Lexical Analyzer
  Authors: Shayan Raouf & Josh Trygg
  CSS 448 - Compilers - Bernstein
  Lexer.java
 */

package com.LexicalAnalysis;


public class Token {
    public static final int INVALID_TOKEN_TYPE = 0;
    public static final int PLUS = 1; // token types
    public static final int INT = 2;

    protected int row,col;
    protected String text;
    public int type;

    public Token(String text, int row, int col){
        this.text = text;
        this.row = row;
        this.col = col;
    }

    public Token(int type, String text) {
        this.type = type; this.text = text;
    }
    public Token(int type) { this.type = type; }
    public Token(int row, int col){
        this.row = row;
        this.col = col;
    }

    public Token(String text){
        this.text = text;
    }
    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }
    public String getType(){
        return text;
    }

    @Override
    public String toString(){
        String name = TokenName.names.get(text);
        if(name != null){
            return "[" + row + "," + col + "]" + " " + TokenName.names.get(text);
        }
        else{
            return "[" + row + "," + col + "]";
        }

    }

}
