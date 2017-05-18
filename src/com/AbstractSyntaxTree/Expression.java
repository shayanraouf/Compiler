package com.AbstractSyntaxTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public abstract class Expression extends Node{

    Expression left, right;

    public Expression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public Expression(){
        left = right = null;
    }
}

