package com.AbstractSyntaxTree;

import java.util.List;

public class AST {
    private List<Line> lines;

    public AST(List<Line> lines) {
        this.lines = lines;
    }

    public void display(){
        for(Line line: lines){
            //display2(line.getStatement());
            System.out.println(line);
        }
    }

    public void display2(Statement statement){
        while(statement != null){

            if(statement instanceof Function){
                Function func = (Function)statement;
                displayFunction(func);
            }

            statement = statement.next;
        }
    }

    public void displayFunction(Function func){
        if(func == null) return;
        System.out.println(func);
        //System.out.println("(" + func.row + ", " + func.col +  ") function declaration");
        //System.out.println(func.functionName);
        Statement statement = func.statements;
        if(statement instanceof Function){
            displayFunction((Function)statement);
        }

    }
}






