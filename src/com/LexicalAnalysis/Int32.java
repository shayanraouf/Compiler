package com.LexicalAnalysis;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public class Int32 extends Number{
    public Int32(String s, int r, int c){
        super(s,r,c);
    }

    @Override
    public String toString(){
        return super.toString() + " int32 (" + text + ")";
    }
}