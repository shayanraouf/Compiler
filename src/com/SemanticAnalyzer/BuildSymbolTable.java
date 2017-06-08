package com.SemanticAnalyzer;

import com.AST.AST;
import com.AST.ExprNode;
import com.LexicalAnalysis.Number;
import com.LexicalAnalysis.Type;
import com.SemanticAnalyzer.Util.ScopeNode;
import com.SemanticAnalyzer.Util.Symbol;
import com.SemanticAnalyzer.Util.SymbolTable;

/**
 * Created by shayanraouf on 5/19/2017.
 */
public class BuildSymbolTable {

    AST tree;
    SymbolTable symbolTable = new SymbolTable();
    public BuildSymbolTable(AST tree) {
        this.tree = tree;
    }


    public void decorateFirstPass(){
        decorateFirstPass(tree);
    }

    public void decorateFirstPass(AST treeNode){
        if(treeNode == null) return;

        if(is_assignment(treeNode)){    // = operator?
//            System.out.println(treeNode.childAt(0).currentToken);
//            System.out.println(treeNode.childAt(1).currentToken);
            treeNode.children.get(0).TYPE = decorate_assignment(treeNode.children.get(1));
        }

        for(AST child: treeNode.children){
            decorateFirstPass(child);
        }
    }


    private Type decorate_assignment(AST treeNode){
        if(isLeaf(treeNode)) return treeNode.TYPE; // check for if Variable
        Type left = decorate_assignment(treeNode.children.get(0));
        Type right = decorate_assignment(treeNode.children.get(1));
        Type final_type = determine_type(left, right);
        treeNode.TYPE = final_type;
        return final_type;
    }


    private Type determine_type(Type left,Type right){
        if(left == null || right == null) return null;
        if(left == Type.FLOAT64 || right == Type.FLOAT64) return Type.FLOAT64;
        return Type.INT32;
    }


    //precondition: treeNode != null
    private boolean isLeaf(AST treeNode){
        return treeNode.children.size() == 0;
    }

    private boolean is_assignment(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"=");
    }

    private boolean is_function_declaration(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"function");
    }

    private boolean is_print_statement(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"print-statement");
    }

    private boolean is_variable_declaration(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"variable-declaration");
    }

    private String left_child_type(AST currentNode){
        return currentNode.childAt(0).token_type();
    }

    private String left_left_child_type(AST currentNode){
        return currentNode.childAt(0).childAt(0).token_type();
    }

    private Type left_left_enum_type(AST currentNode){
        return currentNode.childAt(0).childAt(0).TYPE;
    }

    private Type left_enum_type(AST currentNode){
        return currentNode.childAt(0).TYPE;
    }

    public void buildTable(){
        buildTable(tree);
    }

    public void buildTable(AST currentNode){

        for(AST child: currentNode.children){
            if(child == null) continue;

            if(is_variable_declaration(child)){
                System.out.println(child.token_type());
                variable_decl_scope(child);
            }
            else if(is_function_declaration(child)){
                System.out.println(child.token_type());
                function_scope(child);
            }
            else if(is_assignment(child)){
                System.out.println("assignment_operator (init)");
                init(child);
            }
            else if(is_print_statement(child)){
                call_print(child);
            }
        }
    }

    private void call_print(AST child) {
        String exp_id = left_child_type(child);
        Symbol symbol = symbolTable.resolve(exp_id);
        if(symbol == null){
            System.err.println("Error (lookup): symbol '" + exp_id + "' not declared.");
            System.exit(1);
        }
        resolveType(child.childAt(0));
    }

    private void init(AST child){
        String exp_id = left_child_type(child);
        Symbol symbol = symbolTable.resolve(exp_id);
        if(symbol == null){
            System.err.println("Error (lookup): symbol '" + exp_id + "' not declared.");
            System.exit(1);
        }
        System.out.println(symbol.TYPE);
        Type rightHandSide = resolveType(child.childAt(1));
        if(symbol.TYPE == Type.INT32 && rightHandSide == Type.FLOAT64){
            System.err.println("Invalid Casting (" + exp_id + ")");
            System.exit(1);
        }
        if(symbol.TYPE == Type.FLOAT64){
            child.childAt(0).TYPE = Type.FLOAT64;
        }
        else{
            child.childAt(0).TYPE = rightHandSide;
        }


//        if(child.childAt(0).TYPE == null){
//
//            child.childAt(0).TYPE = resolveType(child.childAt(1));
//            if(symbol.TYPE == Type.FLOAT64){
//
//                //child.childAt(0).TYPE = Type.FLOAT64;
//            }
//        }

        //symbolTable.declareSymbol(exp_id,left_enum_type(child));
    }

    private void function_scope(AST child){

        String function_id = left_child_type(child);
        Symbol symbol = symbolTable.resolve(function_id);
        if(symbol != null){ // if-not-null, function already defined
            System.err.println("Error: redeclaring symbol " + symbol);
        }
        else{ // all good - add to table
            symbolTable.declareSymbol(function_id, left_enum_type(child));
        }
        symbolTable.push();
        System.out.println("Push");
        for(AST sub_child: child.children){
            //System.out.println("ll");
            if(sub_child != null && sub_child.children.size() > 0){
                buildTable(sub_child);
            }

        }
        symbolTable.pop();
        System.out.println("Pop");

    }

    private void variable_decl_scope(AST child){
        String variable_id = left_left_child_type(child);
        child.childAt(0).childAt(0).TYPE = resolveType(child.childAt(0).childAt(1));
//        if(child.childAt(0).childAt(0).TYPE == null){
//            child.childAt(0).childAt(0).TYPE = resolveType(child.childAt(0).childAt(1));
//
//        }
        symbolTable.declareSymbol(variable_id, left_left_enum_type(child));
    }

    private Type resolveType(AST ast) {
        if(isLeaf(ast)){
            if(ast.currentToken instanceof Number){
                return ast.TYPE;
            }
            else{
                Symbol symbol = symbolTable.resolve(ast.currentToken.getType());
                ast.TYPE = symbol.TYPE;
                return ast.TYPE;
            }
        }

        Type left = resolveType(ast.children.get(0));
        Type right = resolveType(ast.children.get(1));
        Type final_type = determine_type(left, right);
        ast.TYPE = final_type;
        return final_type;
    }


    // -------------------------------------- OLD SHIT ----------------------------------------------------------
    public void firstRun(){
        firstRun(symbolTable,tree);
    }

    public void firstRun(SymbolTable parentScope, AST treeNode){
        //System.out.println(treeNode.currentToken.getType());
        //Symbol prevDefined = parentScope.resolve(treeNode.currentToken.getType());

        for(AST child: treeNode.children){
            AST currentNode = child;

            if(is_variable_declaration(currentNode)){
                try{
                    System.out.println(currentNode.childAt(0).childAt(0).token_type());
                    parentScope.resolve(currentNode.childAt(0).childAt(0).token_type());
                    System.err.println("~Britney aint happy aka already defined variable " + currentNode.childAt(0).childAt(0).token_type());
                    //now we have a problem

                }catch (Exception e){
                   String name =  currentNode.children.get(0).children.get(0).currentToken.getType();
                   Type t =  currentNode.children.get(0).children.get(0).TYPE;
                   parentScope.declareSymbol(name,t);

                }

            }
            else if(is_function_declaration(currentNode)){
                try{

                    parentScope.resolve(currentNode.children.get(0).currentToken.getType());
                    System.err.println("~Britney aint happy aka already defined function " + currentNode.children.get(0).currentToken.getType());


                }catch (Exception e){

                    String name =  currentNode.children.get(0).currentToken.getType();
                    Type t =  currentNode.children.get(0).TYPE;
                    parentScope.declareSymbol(name,t);
                }
            }

        }
    }

}