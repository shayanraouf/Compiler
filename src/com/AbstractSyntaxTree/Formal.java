package com.AbstractSyntaxTree;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public class Formal extends Node {
    String      id;
    TypeName    typeName;
    Formal      next;
    int         lexLevel;
    int         offset;
}