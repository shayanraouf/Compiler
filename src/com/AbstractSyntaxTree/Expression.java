package com.AbstractSyntaxTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public abstract class Expression extends Node{
    List<Expression> expressions = new ArrayList<>();

    public void addExpression(Expression e){
        expressions.add(e);
    }


}

