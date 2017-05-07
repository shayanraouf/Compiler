package com.AbstractSyntaxTree;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public class Function extends DeclStatement{


    String              functionName;
    String              returnType;
    //TypeName            typeName;
    Argument            args;
    Statement           statements;

    //ProcDeclaration     myDef;

    public Function(String functionName, Argument args, Statement statements, String returnType) {
        this.returnType = returnType;
        this.functionName = functionName;
        this.args = args;
        this.statements = statements;
    }

}