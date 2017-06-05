package com.CodeGeneration;

import com.AST.AST;
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
    private ArrayList<String> lines;
    private Path file;
    private Map<String, String> labelmap;

    public Generate(AST tree)
    {
        this.tree = tree;
        lines = new ArrayList<>();
        labelmap = new HashMap<>();
        file = Paths.get("test.txt");
    }

    public void firstPass(){
        firstPass(tree);

        try{
            addLabels();
            Files.write(file, lines, Charset.forName("UTF-8"));
        }
        catch(IOException e){
            System.out.println("Error during file write, exiting program");
            System.exit(1);
        }
    }

    private void firstPass(AST treeNode){
        if(treeNode == null) return;

        if(is_assignment(treeNode)){    // = operator?
            //lines.add(treeNode.childAt(0).currentToken.getType());
        }
        if(is_variable_declaration(treeNode)){    // = operator?
            store_declaration(treeNode.childAt(0));
        }

        for(AST child: treeNode.children){
            firstPass(child);
        }
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
            lines.add(pair.getKey() + ":");
            lines.add("    " + pair.getValue());
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

        if(treeNode.TYPE.name().equals("INT32")){
            val += "int_literal ";
        }
        else if(treeNode.TYPE.name().equals("FLOAT64")){
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


    /*
        Put the variable declaration into the map
    */
    private void store_declaration(AST treeNode){
        String key = treeNode.childAt(0).currentToken.getType();
        String value = getValue(treeNode.childAt(1));
        labelmap.put(key, value);
    }


    private boolean is_variable_declaration(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"variable-declaration");
    }

    private boolean is_assignment(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"=");
    }
}
