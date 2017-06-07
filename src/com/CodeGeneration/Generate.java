package com.CodeGeneration;

import com.AST.AST;
import com.LexicalAnalysis.Type;

import java.util.ArrayList;
import java.nio.file.*;
import java.nio.charset.*;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.SemanticAnalyzer.Util.Symbol;
import com.SemanticAnalyzer.Util.SymbolTable;

public class Generate
{
    private AST tree;
    private ArrayList<String> codelines;
    private Path file;
    private Map<String, Symbol> labelmap;
    private Map<String, String> operations;
    private SymbolTable symbolTable;

    static boolean verbose = true;

    public Generate(AST tree)
    {
        this.tree = tree;
        symbolTable = new SymbolTable();
        codelines = new ArrayList<>();
        labelmap = new HashMap<>();
        init_operations();
        file = Paths.get("test.txt");
    }

    public void init_operations(){
        operations = new HashMap<>();
        operations.put("+", "add");
        operations.put("-", "sub");
        operations.put("*", "mul");
        operations.put("/", "div");
        operations.put("=", "store_mem");
    }

    public void GenCode(){
        try{
            labelmap.put("utility", new Symbol("int_literal 0", "int"));
            GenCode(tree);

            labelmap.put("newline", new Symbol("int_literal 10", "int"));
            codelines.add("");
            codelines.add("exit");
            codelines.add("");
            addLabels();
            Files.write(file, codelines, Charset.forName("UTF-8"));
        }
        catch(IOException e){
            System.out.println("Error during file write, exiting program");
            System.exit(1);
        }
    }

    private void GenCode(AST currentNode){
        for(AST child: currentNode.children)
        {
            if (child == null) continue;

            if (is_assignment(child)){
                //store_assignment(child.childAt(1), null);
                store_assignment(child, null);
            }
            else if (is_variable_declaration(child)){
                store_declaration(child.childAt(0));
            }
            else if (is_function_declaration(child)){
                function_scope(child);
            }
        }
    }

    /*
        Put the variable declaration into the map
    */
    private void function_scope(AST child){

        String function_id = child.childAt(0).currentToken.getType();

        Symbol symbol = symbolTable.resolve(function_id);
        symbolTable.declareSymbol(function_id, Type.FUNCTION);
        symbolTable.push();
        System.out.println("Push");
        for(AST sub_child: child.children){
            if(sub_child != null && sub_child.children.size() > 0){
                GenCode(sub_child);
            }

        }
        symbolTable.pop();
        System.out.println("Pop");
    }


    /*
       Put the variable declaration into the map
   */
    private void store_assignment(AST treeNode, String type){
        if (treeNode.children.size() == 0){
            if (is_identifier(treeNode))
            {
                String name = treeNode.currentToken.getType();
                codelines.add("load_label " + name);
                String gentype = labelmap.get(name).getGenType();
                codelines.add("load_mem_" + gentype);
            }
            else if (is_number(treeNode)){
                String name = treeNode.currentToken.getType();
                //String name = treeNode
                codelines.add("load_label " + name);
                if (getEnumType(treeNode) == Type.FLOAT64){
                    codelines.add("store_mem_float");
                }
                else{
                    codelines.add("store_mem_int");
                }
            }
            //}
            return;
        }
        //type = getCodeType(treeNode);

        if(!treeNode.currentToken.getType().equals("=")){   // equals sign is special case
            store_assignment(treeNode.childAt(0), type);    // traverse left
        }
        else{
            type = getCodeType(treeNode.childAt(0));        // get gencode type of LHS identifier
        }

        store_assignment(treeNode.childAt(1), type);        // traverse right

        if (treeNode.currentToken.getType().equals("=")){
            handle_equals_sign(treeNode);
        }
        else if (type.equals("int")){        // integer arithmetic
            handle_integer(treeNode);
        }
        else{
            handle_float(treeNode);  // float arithmetic
        }
    }

    public void handle_equals_sign(AST treeNode)
    {
        if (treeNode.childAt(0).TYPE == Type.INT32)
        {
            handle_integer(treeNode);
        } else
        {
            handle_float(treeNode);
        }
    }

    public void handle_integer(AST treeNode){
        String var = treeNode.childAt(0).currentToken.getType();
        String op = treeNode.currentToken.getType();
        if (op.equals("=")){
            codelines.add("load_label " + var);
            codelines.add("store_mem_int");
            if (verbose){ printInt(var); }
        }
        else{
            //codelines.add("load_label " + treeNode.currentToken.getType());
            codelines.add(operations.get(op));
        }
    }

    public void handle_float(AST treeNode){
        String var = treeNode.childAt(0).currentToken.getType();
        String op = treeNode.currentToken.getType();
        if (op.equals("=")){
            codelines.add("load_label " + var);
            codelines.add("store_mem_float");
            if (verbose){ printFloat(var); }
        }
        else{
            //codelines.add("load_label " + treeNode.currentToken.getType());
            codelines.add(operations.get(op) + "_f");
        }
    }


    public void printInt(String var){
        codelines.add("");
        codelines.add("load_label " + var);
        codelines.add("load_mem_int");
        codelines.add("print_int");
        codelines.add("");
    }
    public void printFloat(String var){
        codelines.add("");
        codelines.add("load_label " + var);
        codelines.add("load_mem_float");
        codelines.add("print_float");
        codelines.add("");
    }

    /*
        Put the variable declaration into the map
    */
    private void store_declaration(AST treeNode){
        String codeLabel = treeNode.childAt(0).currentToken.getType();
        String codeType = getCodeType(treeNode.childAt(0));
        String codeSnip = getValue(treeNode.childAt(1));
        //System.out.println(codeLabel +  "  " + codeType + "  " + codeSnip);

        Symbol symbol = new Symbol(codeLabel, codeType, getEnumType(treeNode.childAt(0)));
        labelmap.put(codeLabel, new Symbol(codeSnip, codeType));   // store as a label
        symbolTable.declareSymbol(symbol);
    }

    /*
        Iterate through Map that stores all the labels and write to file
     */
    private void addLabels()
    {
        Iterator it = labelmap.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry) it.next();
            Object key = pair.getKey();  // get the key
            codelines.add(key + ":");
            Symbol temp = labelmap.get(key);
            codelines.add("    " + temp.getName());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    /*
        Determine the key that is to be placed in the Map
     */
    private String getKey(AST treeNode){

        return null;
    }

    /*
        Determine the value that is to be placed in the Map
     */
    private String getValue(AST treeNode){
        String val = "";

        if(treeNode.TYPE == Type.INT32){
            val += "int_literal ";
        }
        else if(treeNode.TYPE == Type.FLOAT64){
            val += "float_literal ";
        }

        if(treeNode.children.size() == 0){
            val += treeNode.currentToken.getType();
            return val;
        }
        else
        {
            val += "0";
            return val;
        }
    }

    private boolean is_variable_declaration(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"variable-declaration");
    }

    private Type getEnumType(AST treeNode){
        return treeNode.TYPE;
    }

    private String getCodeType(AST treeNode){
        if (treeNode.TYPE == null) return null;

        if (treeNode.TYPE == Type.FLOAT64){
            return "float";
        }
        else{
            return "int";
        }
    }

    private boolean is_assignment(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"=");
    }
    private boolean is_identifier(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"id");
    }
    private boolean is_function_declaration(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"function");
    }
    private boolean is_number(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"basic-type");
    }
}