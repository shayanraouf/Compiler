package com.AbstractSyntaxTree;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public class ProcDeclaration extends Node {
    String              stringID;
    Formal              formals;
    TypeName            retType;
    Body                body;
    ProcDeclaration     next;
    int                 lexLevel;
}