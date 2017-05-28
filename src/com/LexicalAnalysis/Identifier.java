package com.LexicalAnalysis;

import com.LexicalAnalysis.Token;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public class Identifier extends Token {
    public Identifier(String s,int r, int c){
        super(s,r,c);
    }

    @Override
    public String toString(){
        return super.toString() + " IDENTIFIER (" + text + ")";
    }


}