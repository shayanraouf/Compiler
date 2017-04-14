package com;
import java.util.List;

public class AST {
    private List<Line> lines;

    public AST(List<Line> lines) {
        this.lines = lines;
    }
}


    //-------------------------------------------------------
    abstract class Node{
        int line_number;
    }
    //-------------------------------------------------------
    class Function extends DeclStatement{
        String              functionName;
        TypeName            typeName;
        Argument            args;
        AssignStatement     statement;
        //ProcDeclaration     myDef;

    }
    //-------------------------------------------------------
    abstract class Body extends Node{
        TypeDeclaration     typeDeclaration;
        ProcDeclaration     procDeclaration;
        VarDeclaration      varDeclaration;
        Statement           statements;
        int                 frameSize;
    }
    //-------------------------------------------------------
    abstract class Expression extends Node{

    }
    //-------------------------------------------------------
    abstract class CompoundType extends Node { }


    //-------------------------------------------------------
    abstract class Statement extends Node {
        Statement     next;
    }

    abstract class DeclStatement extends Statement {
        Statement     next;
    }
    //-------------------------------------------------------
    class Formal extends Node {
        String      id;
        TypeName    typeName;
        Formal      next;
        int         lexLevel;
        int         offset;
    }

    //-------------------------------------------------------
    class ForStatement extends Statement {
        LValue              lValue;
        Expression          expr1;
        Expression          expr2;
        Expression          expr3;
        Statement           statements;
        String              exitLabel;
        public ForStatement(){}
    }


    //-------------------------------------------------------
    class AssignStatement extends Statement {
        LValue      lValue;
        Expression  expression;
    }

    //-------------------------------------------------------
    class ValueOf extends Expression {
        LValue         lValue;
    }

    abstract class LValue extends Node { }


    //-----------------------------------------------------
    class Variable extends LValue {
        String      name;
        String      type;
        Node        myDef;
        int         currentLevel;

        public Variable(String name, String type){
            this.name = name;
            this.type = type;
        }
    }

    //-------------------------------------------------------
    class CallStatement extends Statement {
        String              stringID;
        Argument            args;
        ProcDeclaration     myDef;
    }

    //-------------------------------------------------------
    class ProcDeclaration extends Node {
        String              stringID;
        Formal              formals;
        TypeName            retType;
        Body                body;
        ProcDeclaration     next;
        int                 lexLevel;
    }
    //-------------------------------------------------------
    class VarDeclaration extends Node {
        String              stringID;
        TypeName            typeName;
        Expression          expression;
        VarDeclaration      next;
        int                 lexLevel;
        int                 offset;
    }

    //-------------------------------------------------------
    class TypeDeclaration extends Node {
        String              stringID;
        CompoundType        compoundType;
        TypeDeclaration     next;
    }

    //-------------------------------------------------------
    class TypeName extends Node {
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

    //-------------------------------------------------------
    class Argument extends Node {
        Argument    next;
        Expression  expression;
        int         mode;
        Node        location;
    }




