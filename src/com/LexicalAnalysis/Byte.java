package com.LexicalAnalysis;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public class Byte extends Number{
    public Byte(String s, int r, int c){
        super(s,r,c);
    }

    @Override
    public String toString(){
        return super.toString() + " byte (" + text + ")";
    }
}