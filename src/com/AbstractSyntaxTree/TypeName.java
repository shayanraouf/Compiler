package com.AbstractSyntaxTree;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public class TypeName extends Node {
    String       stringID;
    CompoundType compoundType;
    public TypeName(String stringID, CompoundType compoundType){
        this.stringID = stringID;
        this.compoundType = compoundType;
    }

    public TypeName(String stringID){
        this.stringID = stringID;
    }
}