package com.CodeGeneration;

import com.AST.AST;
import com.LexicalAnalysis.Number;
import com.LexicalAnalysis.Type;

import java.util.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.io.*;

import com.SemanticAnalyzer.Util.Symbol;
import com.SemanticAnalyzer.Util.SymbolTable;

public class Generate
{
    private AST tree;
    //private Stack<ArrayList> codestack;
    private ArrayList<String> globalcode;
    private ArrayList<String> functioncode;
    private Path file;
    private Map<String, Symbol> labelmap;
    private Map<String, String> operations;
    //private Map<String, >
    private SymbolTable symbolTable;
    int num = 0;
    static boolean verbose = true;

    public Generate(AST tree)
    {
        this.tree = tree;
        //codestack = new Stack<>();
        symbolTable = new SymbolTable();
        globalcode = new ArrayList<>();
        functioncode = new ArrayList<>();
        //codestack.push(new ArrayList<String>());
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
            GenCode(tree, globalcode);
            globalcode.add("");
            globalcode.add("load0");
            globalcode.add("exit");
            globalcode.add("");
            addLabels(functioncode);
            for (String line : functioncode){
                globalcode.add(line);
            }
            Files.write(file, globalcode, Charset.forName("UTF-8"));
        }
        catch(IOException e){
            System.out.println("Error during file write, exiting program");
            System.exit(1);
        }
    }

    private void GenCode(AST currentNode, ArrayList<String> localcode){
        for(AST child: currentNode.children)
        {
            if (child == null) continue;

            if (is_assignment(child)){
                store_assignment(child, localcode);     // m = 3    or   m = m * x * n
            }
            else if (is_variable_declaration(child)){
                store_declaration(child.childAt(0),localcode);    // var m = 24;
            }
            else if (is_function_declaration(child)){
                function_scope(child, localcode);       // function poo() {}
            }
        }
    }

    /*
        Put the function declaration into the map
    */
    private void function_scope(AST treeNode, ArrayList<String> localcode){

        String function_id = treeNode.childAt(0).currentToken.getType();

        symbolTable.declareSymbol(function_id, Type.FUNCTION);
        symbolTable.push();

        localcode.add("load_label " + function_id);   // load function label and branch
        localcode.add("branch");

        // new list of codelines for upcoming function scope
        ArrayList<String> currscope = new ArrayList<>();
        currscope.add(function_id + ":");

        // cycle through the children of the current function
        for(AST child: treeNode.children){
            if(child != null && child.children.size() > 0){
                GenCode(child, currscope);
            }
        }

        // TODO --> returning from function isn't quite working as it should
        /*
                Any values calculated after the return aren't working
                Has to do with Stack pointer (i think)
         */

        currscope.add("return");  // returning from function
        functioncode.add("");

        // add the lines gathered from the latest scope to the master function call scope
        for(String line : currscope){
            functioncode.add(line);
        }
        symbolTable.pop();
        System.out.println("Pop");
    }

    /*
    Put the variable declaration into the map
*/
    private void store_declaration(AST treeNode, ArrayList<String> localcode){
        String codeLabel = treeNode.childAt(0).currentToken.getType();
        //System.err.println(codeLabel);
        String codeType = getCodeType(treeNode.childAt(0));

        String codeSnip = getValue(treeNode.childAt(1));
        //System.out.println(codeLabel +  "  " + codeType + "  " + codeSnip);

        Symbol symbol = new Symbol(codeLabel, codeType, getEnumType(treeNode.childAt(0)));
        labelmap.put(codeLabel, new Symbol(codeSnip, codeType));   // store as a label
        symbolTable.declareSymbol(symbol);
        if(treeNode.childAt(1).children.size() > 1){
            store_assignment(treeNode,localcode);
            //System.err.println(treeNode.childAt(1).children.size());
        }

    }



    /*
       Put the variable declaration into the map
   */
    private void store_assignment(AST treeNode, ArrayList<String> localcode){
        if (treeNode.children.size() == 0){
            assignment_leaf(treeNode, localcode);      // at leaf node
            return;
        }

        String type = getCodeType(treeNode.childAt(0));     // float or int?

        if(!treeNode.currentToken.getType().equals("=")){
            store_assignment(treeNode.childAt(0), localcode);    // if not sitting at equals sign, traverse left
        }
        else{
            // sitting at equals sign
            if(treeNode.isMatch(treeNode.childAt(1).currentToken, "basic-type")){  // RHS is number literal?
                Symbol symbol = symbolTable.resolve(treeNode.childAt(0).currentToken.getType());
                // TODO --> find out how to put a number literal on the stack

            }

        }

        store_assignment(treeNode.childAt(1), localcode);        // traverse right

        if (treeNode.currentToken.getType().equals("=")){
            load_equals_sign(treeNode, localcode);      // now at top of tree
        }
        else if (type.equals("int")){    // integer arithmetic
            integer_load(treeNode, localcode);
        }
        else{
            float_load(treeNode, localcode);  // float arithmetic
        }
    }


    private void assignment_leaf(AST treeNode, ArrayList<String> localcode){
        if (is_identifier(treeNode))
        {
            String name = treeNode.currentToken.getType();
            localcode.add("load_label " + name);
            String gentype = labelmap.get(name).getGenType();
            localcode.add("load_mem_" + gentype);
        }
        else if (is_number(treeNode)){
            System.err.println(treeNode.currentToken);

            String codeLabel = generateLiteralLabel();
            Symbol symbol = new Symbol(getValue(treeNode),getCodeType(treeNode));
            labelmap.put(codeLabel,symbol);
            localcode.add("load_label " + codeLabel);
            if (getEnumType(treeNode) == Type.FLOAT64){
                localcode.add("load_mem_float");
            }
            else{
                localcode.add("load_mem_int");
            }
        }

    }

    private String generateLiteralLabel() {
        return "LL" + num++;
    }

    private void load_equals_sign(AST treeNode, ArrayList<String> localcode){
        if (treeNode.childAt(0).TYPE == Type.INT32){
            integer_load(treeNode, localcode);
        } else{
            float_load(treeNode, localcode);
        }
    }

    private void integer_load(AST treeNode, ArrayList<String> localcode){
        String var = treeNode.childAt(0).currentToken.getType();
        String op = treeNode.currentToken.getType();
        if (op.equals("=")){
            localcode.add("load_label " + var);
            localcode.add("store_mem_int");
            if (verbose){ printInt(var, localcode); }
        }
        else{
            if(treeNode.childAt(1).TYPE == Type.FLOAT64){
                localcode.add("to_int");
            }
            localcode.add(operations.get(op));
        }
    }

    private void float_load(AST treeNode, ArrayList<String> localcode){
        String var = treeNode.childAt(0).currentToken.getType();
        String op = treeNode.currentToken.getType();
        if (op.equals("=")){
            localcode.add("load_label " + var);
            localcode.add("store_mem_float");{
                printFloat(var, localcode);
            }
        } else
        {
            if (treeNode.childAt(1).TYPE == Type.INT32){
                localcode.add("to_float");
            }
            localcode.add(operations.get(op) + "_f");
        }
    }


    private void printInt(String var, ArrayList<String> localcode){
        //localcode.add("");
        localcode.add("load_label " + var);
        localcode.add("load_mem_int");
        localcode.add("print_int");
        localcode.add("load_label newline");
        localcode.add("load_mem_int");
        localcode.add("print_byte");
        //localcode.add("");
    }
    private void printFloat(String var, ArrayList<String> localcode){
        //localcode.add("");
        localcode.add("load_label " + var);
        localcode.add("load_mem_float");
        localcode.add("print_float");
        localcode.add("load_label newline");
        localcode.add("load_mem_int");
        localcode.add("print_byte");
        //localcode.add("");
    }


    /*
        Iterate through Map that stores all the labels and write to file
     */
    private void addLabels(ArrayList<String> labels)
    {
        labels.add("");
        labelmap.put("newline", new Symbol("int_literal 10", "int"));

        Iterator it = labelmap.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry) it.next();
            Object key = pair.getKey();  // get the key
            labels.add(key + ":");
            Symbol temp = labelmap.get(key);
            labels.add("    " + temp.getName());
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
