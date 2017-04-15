package com.AbstractSyntaxTree;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public class Argument extends Node {
    Argument    next;
    Expression  expression;
    int         mode;
    Node        location;
}