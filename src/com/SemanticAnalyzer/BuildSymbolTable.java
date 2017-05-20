package com.SemanticAnalyzer;

import com.AST.AST;

/**
 * Created by shayanraouf on 5/19/2017.
 */
public class BuildSymbolTable {
    AST tree;
    SymbolTable symbolTable;
    public BuildSymbolTable(AST tree) {
        this.tree = tree;
    }

    public SymbolTable build(){
        symbolTable = new SymbolTable();
        build(tree);
        return symbolTable;
    }

    public void build(AST root){
        for(AST child: root.children){
            String type = child.currentToken.getType();
            if(type.equals("function")){
                symbolTable.declareSymbol(child.currentToken);
                symbolTable.push();
                System.out.println("function:" + child.currentToken);
                build(child);
                symbolTable.pop();
            }
            else{
                System.out.println(child.currentToken);
                symbolTable.declareSymbol(child.currentToken);
            }
        }
    }
}
