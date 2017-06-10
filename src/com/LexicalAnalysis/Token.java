/*
  Lexical Analyzer
  Authors: Shayan Raouf & Josh Trygg
  CSS 448 - Compilers - Bernstein
  Token.java
 */

package com.LexicalAnalysis;


public class Token {

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
    public String getType(){
        return text;
    }

    @Override
    public String toString(){
        String name = TokenName.names.get(text);
        if(name != null){
            if(row == 0 && col == 0) return TokenName.names.get(text);
            return "[" + row + "," + col + "]" + " " + TokenName.names.get(text);
        }
        else{
            return "[" + row + "," + col + "]";
        }
    }
}
