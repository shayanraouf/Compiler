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

/**
 * Created by jtryg on 6/4/2017.
 */
public class Generate
{
    private AST tree;
    private ArrayList<String> codelines;
    private Path file;
    private Map<String, String> labelmap;
    private Map<String, String> operations;

    public Generate(AST tree)
    {
        this.tree = tree;
        codelines = new ArrayList<>();
        labelmap = new HashMap<>();
        init_operations();
        labelmap.put("newline", "int_literal 10");
        file = Paths.get("test.txt");
    }

    public void init_operations(){
        operations = new HashMap<>();
        operations.put("+", "add");
        operations.put("-", "sub");
        operations.put("*", "mul");
        operations.put("/", "div");
    }

    /*
        Put the variable declaration into the map
    */
    private void store_assignment(AST treeNode, String type){
        if (treeNode.children.size() == 0){
            if (is_identifier(treeNode)){
                codelines.add("load_label " + treeNode.currentToken.getType());
                type = getType(treeNode);
                codelines.add("load_mem_" + type);
            }
            // TODO - need to handle literal numbers
            return;
        }

        type = getType(treeNode);

        // assembly instructions
        store_assignment(treeNode.childAt(0), type);
        store_assignment(treeNode.childAt(1), type);

        String op = treeNode.currentToken.getType();
        if (type.equals("int")){
            codelines.add(operations.get(op));
        }
        else{
            codelines.add(operations.get(op) + "_f");
        }
    }

    public void firstPass(){
        for(AST child: tree.children){
            firstPass(child);
        }

        try{
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


    private void firstPass(AST treeNode){
        if(treeNode == null) return;

        if(is_assignment(treeNode)){    // = operator?
            store_assignment(treeNode.childAt(1), null);
        }
        if(is_variable_declaration(treeNode)){    // = operator?
            store_declaration(treeNode.childAt(0));
        }
    }


    /*
        Put the variable declaration into the map
    */
    private void store_declaration(AST treeNode){
        String label = treeNode.childAt(0).currentToken.getType();
        String type = getType(treeNode.childAt(0));
        // assembly instructions


        // store into labelmap
        String key = label;
        String value = getValue(treeNode.childAt(1));
        labelmap.put(key, value);
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
            codelines.add(pair.getKey() + ":");
            codelines.add("    " + pair.getValue());
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

    private String getType(AST treeNode){
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
}
