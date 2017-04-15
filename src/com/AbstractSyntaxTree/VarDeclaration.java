package com.AbstractSyntaxTree;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public class VarDeclaration extends Node {
    String stringID;
    TypeName typeName;
    Expression expression;
    VarDeclaration next;
    int lexLevel;
    int offset;
}