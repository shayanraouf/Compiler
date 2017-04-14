package com.LexicalAnalysis;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public class Float64 extends Number{
    public Float64(String s, int r, int c){
        super(s,r,c);
    }

    @Override
    public String toString(){
        return super.toString() + " float64 (" + text + ")";
    }
}