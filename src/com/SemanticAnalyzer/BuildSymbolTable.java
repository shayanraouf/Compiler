package com.SemanticAnalyzer;

import com.AST.AST;
import com.LexicalAnalysis.Type;
import com.SemanticAnalyzer.Util.Symbol;
import com.SemanticAnalyzer.Util.SymbolTable;

/**
 * Created by shayanraouf on 5/19/2017.
 */
public class BuildSymbolTable {
    static boolean flag = true;
    AST tree;
    SymbolTable symbolTable = new SymbolTable();
    public BuildSymbolTable(AST tree) {
        this.tree = tree;
    }

    public void firstRun(){
        firstRun(symbolTable,tree);
    }

    public void firstRun(SymbolTable parentScope, AST treeNode){
        Symbol prevDefined = parentScope.resolve(treeNode.currentToken.getType());

        for(AST child: treeNode.children){
            firstPass(child);
        }
    }


    public void firstPass(){
        firstPass(tree);
    }

    private void firstPass(AST treeNode){
        if(treeNode != null){
            if(AST.isMatch(treeNode.currentToken,"=")){
                decorate(treeNode);
            }
            else if(AST.isMatch(treeNode.currentToken,"function")){
                treeNode.children.get(0).TYPE = Type.FUNCTION;
            }
            for(AST child: treeNode.children){
                firstPass(child);
            }
        }
    }

    private void decorate(AST treeNode){
        setType(treeNode.children.get(0),treeNode, true);
        flag = true;
    }
    private void setType(AST toSet, AST root, Boolean flagg){
        if(!flag) return;
        if(root == null) return;

        if(flag && AST.isMatch(root.currentToken,"float64")){
            toSet.TYPE = Type.FLOAT64;
            flag = new Boolean(false);
            return;
        }

        else if(flag && AST.isMatch(root.currentToken,"int32")){
            toSet.TYPE = Type.INT32;
        }
        if(root.children.size() > 0 && flag){

            setType(toSet, root.children.get(0), flag);
            if(root.children.size() > 1 && flag){
                setType(toSet, root.children.get(1), flag);
            }
        }
    }

}


/*


    public void firstPass(){
        firstPass(this);
    }

    private void firstPass(AST treeNode){
        if(treeNode != null){
            //System.out.println("first pass " + treeNode.currentToken);

            if(isMatch(treeNode.currentToken,"=")){
                //System.out.println();
                decorate(treeNode);
            }
            for(AST child: treeNode.children){
                firstPass(child);
            }
        }
    }
   static boolean flag = true;
    private void decorate(AST treeNode){
        setType(treeNode.children.get(0),treeNode, true);
    }

    private void setType(AST toSet, AST root, Boolean flagg){
        if(!flag) return;
        if(root == null) return;

        if(flag && isMatch(root.currentToken,"float64")){
            toSet.TYPE = Type.FLOAT64;
            flag = new Boolean(false);
            return;
        }

        else if(flag && isMatch(root.currentToken,"int32")){
            toSet.TYPE = Type.INT32;
        }
        if(root.children.size() > 0 && flag){

            setType(toSet, root.children.get(0), flag);
            if(root.children.size() > 1 && flag){
                setType(toSet, root.children.get(1), flag);
            }
        }
    }



 */
