package com.AbstractSyntaxTree;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public class Variable extends LValue {
    String      name;
    String      type;
    Node        myDef;
    int         currentLevel;

    public Variable(String name, String type){
        this.name = name;
        this.type = type;
    }
}