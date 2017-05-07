package com.AbstractSyntaxTree;

import com.LexicalAnalysis.Token;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public abstract class Node{
    protected int row;
    protected int col;

    protected Token token;

    public void setToken(Token token){
        this.token = token;
    }
    public void setRowAndCol(int row, int col){
        this.row = row;
        this.col = col;
    }
}