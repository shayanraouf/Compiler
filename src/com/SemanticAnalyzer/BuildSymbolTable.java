package com.SemanticAnalyzer;

import com.AST.AST;
import com.AST.ExprNode;
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
        //System.out.println(treeNode.currentToken.getType());
        //Symbol prevDefined = parentScope.resolve(treeNode.currentToken.getType());

        for(AST child: treeNode.children){
            AST currentNode = child;

            if(AST.isMatch(currentNode.currentToken,"variable-declaration")){
                try{
                    System.out.println(currentNode.children.get(0).children.get(0).currentToken.getType());
                    parentScope.resolve(currentNode.children.get(0).children.get(0).currentToken.getType());
                    System.err.println("~Britney aint happy aka already defined variable " + currentNode.children.get(0).children.get(0).currentToken.getType());
                    //now we have a problem

                }catch (Exception e){
                   String name =  currentNode.children.get(0).children.get(0).currentToken.getType();
                   Type t =  currentNode.children.get(0).children.get(0).TYPE;
                   parentScope.declareSymbol(name,t);
                    //System.out.println(name + " type->" + t);
                    //System.out.println("an exception was thrown, oops, i did it again ~Britney SPears");
                }

            }
            else if(AST.isMatch(currentNode.currentToken,"function")){
                try{
                    //System.out.println(currentNode.children.get(0).currentToken.getType());
                    parentScope.resolve(currentNode.children.get(0).currentToken.getType());
                    System.err.println("~Britney aint happy aka already defined function " + currentNode.children.get(0).currentToken.getType());
                    //System.out.println(currentNode.children.get(0).currentToken.getType());
                    //System.err.println("~Britney aint happy aka already defined variable " + currentNode.children.get(0).children.get(0).currentToken.getType());
                    //now we have a problem

                }catch (Exception e){

                    String name =  currentNode.children.get(0).currentToken.getType();
                    Type t =  currentNode.children.get(0).TYPE;
                    parentScope.declareSymbol(name,t);
                }
            }

            //parentScope.resolve(currentNode.currentToken.getType());
            //System.out.println(currentNode.currentToken.getType());
            //firstPass(child);
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
