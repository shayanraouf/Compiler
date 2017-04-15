package com.AbstractSyntaxTree;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public abstract class Body extends Node {
    TypeDeclaration typeDeclaration;
    ProcDeclaration procDeclaration;
    VarDeclaration varDeclaration;
    Statement statements;
    int frameSize;
}