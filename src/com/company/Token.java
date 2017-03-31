package com.company;

/**
 * Created by shayanraouf on 3/29/2017.
 */
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

    @Override
    public String toString(){
        return "[" + row + "," + col + "]";
    }

}
