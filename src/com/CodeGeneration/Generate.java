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
    int num2 = 0;
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
            else if(is_function_call(child)){
                function_call(child,localcode);

            }
            else if(is_print_statement(child)){
                System.err.print("if(is_print_statement(child))");
                print_statement(child,localcode);

            }


        }
    }

    private boolean is_print_statement(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"print-statement");
    }

    private void function_call(AST treeNode,ArrayList<String> localcode){

        String function_id = treeNode.childAt(0).currentToken.getType();
        localcode.add("load_label " + function_id);
        localcode.add("call");
    }



    /*
        Put the function declaration into the map
    */
    private void function_scope(AST treeNode, ArrayList<String> localcode){

        String function_id = treeNode.childAt(0).currentToken.getType();

        symbolTable.declareSymbol(function_id, Type.FUNCTION);
        symbolTable.push();
        ArrayList<String> currscope = new ArrayList<>();
        currscope.add(function_id + ":");

        for(AST child: treeNode.children){
            if(child != null && child.children.size() > 0){

                if(is_parameters(child)){
                    handle_params(child, currscope);
                }

                if(is_block_statement(child)){
                    GenCode(child, currscope);
                }


            }
        }

        currscope.add("return");  // returning from function
        functioncode.add("");

        // add the lines gathered from the latest scope to the master function call scope
        for(String line : currscope){
            functioncode.add(line);
        }
        symbolTable.pop();
        System.out.println("Pop Corn");
    }


    private void handle_params(AST treeNode, ArrayList<String> localcode){
        //System.err.println("--------------------" + treeNode.children.size());

        for(AST child: treeNode.children){
            //System.err.println(child.currentToken + " handle_params");
            handle_parameter(child, localcode);
        }
    }

    private void handle_parameter(AST treeNode, ArrayList<String> localcode){
        for(AST child: treeNode.children){

            if(AST.isMatch(child.currentToken,"const")){

            }

            if(AST.isMatch(child.currentToken,"ref")){

            }


            if(is_identifier(child)){
                store_declaration(child,localcode);
            }


            System.err.println(child.currentToken + " handle_param_singular");
            //handle_parameter(child.childAt(0), localcode);
        }
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

        String unique_codeLabel = "";
        //labelmap.containsKey(codeLabel

        if(labelmap.containsKey(codeLabel)){
            //System.err.println("ppppppppppppppppppppppppppppppppppppppppp");
            unique_codeLabel = gen_unique(codeLabel);
            symbol.alias = unique_codeLabel;
            symbolTable.declareSymbol(symbol);
            labelmap.put(unique_codeLabel, new Symbol(codeSnip, codeType));   // store as a label
        }
        else
        {
            labelmap.put(codeLabel, new Symbol(codeSnip, codeType));   // store as a label
            symbolTable.declareSymbol(symbol);
        }
        System.err.println("+++++++++++++++++++++ " + symbol.alias);

        if(treeNode.childAt(1).children.size() > 1){
            store_assignment(treeNode,localcode);
            //System.err.println(treeNode.childAt(1).children.size());
        }

    }

    private String gen_unique(String codeLabel){
        return codeLabel + num2++;
    }



    /*
       Put the variable declaration into the map
   */
    private void store_assignment(AST treeNode, ArrayList<String> localcode){
        /*if (treeNode.children.size() == 0){
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
        }*/
        store_assignment2(treeNode.childAt(1),localcode);
        load_equals_sign(treeNode, localcode);

    }

    private AST store_assignment2(AST treeNode, ArrayList<String> localcode){
        if (treeNode.children.size() == 0){
            assignment_leaf(treeNode, localcode);      // at leaf node
            return treeNode;
        }
        AST left = store_assignment2(treeNode.childAt(0),localcode);
        if(left.TYPE == Type.INT32 && treeNode.TYPE == Type.FLOAT64){
            localcode.add("to_float");
        }

        AST right = store_assignment2(treeNode.childAt(1),localcode);
        if(right.TYPE == Type.INT32 && treeNode.TYPE == Type.FLOAT64){
            localcode.add("to_float");
        }
        assignment_leaf(treeNode,localcode);
        return treeNode;
    }


    private void assignment_leaf(AST treeNode, ArrayList<String> localcode){
        if (is_identifier(treeNode))
        {
            String name = treeNode.currentToken.getType();
            Symbol symbol = symbolTable.resolve(name);
            localcode.add("load_label " + symbol.alias);
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
        else{ // operator
            String op = treeNode.currentToken.getType();
            if(treeNode.TYPE == Type.FLOAT64){
                localcode.add(operations.get(op) + "_f");
            }
            else{
                localcode.add(operations.get(op));
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
            //if (verbose){ printInt(var, localcode); }
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
            }
        } else
        {
            if (treeNode.childAt(1).TYPE == Type.INT32){
                localcode.add("to_float");
            }
            localcode.add(operations.get(op) + "_f");
        }
    }

    private void print_statement(AST treeNode, ArrayList<String> localcode){
        //System.err.println(treeNode.childAt(0).TYPE);
        String id = treeNode.childAt(0).currentToken.getType();
        //System.err.println("--------------------- " + id);
        Symbol symbol = symbolTable.resolve(id);
        //System.err.println("--------------------- " + symbol.alias);
        if(treeNode.childAt(0).TYPE == Type.FLOAT64){
            printFloat(symbol.alias,localcode);
        }
        else{
            printInt(symbol.alias,localcode);
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
    private boolean is_block_statement(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"block-statement");
    }
    private boolean is_parameters(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"parameter(s)");
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
    private boolean is_function_call(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"function-call");
    }
    private boolean is_number(AST treeNode){
        return AST.isMatch(treeNode.currentToken,"basic-type");
    }
}
