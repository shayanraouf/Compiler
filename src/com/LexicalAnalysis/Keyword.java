package com.LexicalAnalysis;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public class Keyword extends Token{

    public Keyword(String s, int r, int c){
        super(s,r,c);
    }

    @Override
    public String toString(){
        return super.toString() + " Keyword (" + text + ")";
    }

}