/*
  Lexical Analyzer
  Authors: Shayan Raouf & Josh Trygg
  CSS 448 - Compilers - Bernstein
  Lexer.java
 */

package com.company;


public class Token {
    protected int row,col;
    protected String text;
    public Token(String text, int row, int col){
        this.text = text;
        this.row = row;
        this.col = col;
    }

    public Token(int row, int col){
        this.row = row;
        this.col = col;
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
