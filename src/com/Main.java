/*
  Lexical Analyzer
  Authors: Shayan Raouf & Josh Trygg
  CSS 448 - Compilers - Bernstein
  Lexer.java
 */

package com;
import com.AST.AST;
import com.LexicalAnalysis.Lexer;
import com.LexicalAnalysis.Token;
import com.SemanticAnalyzer.BuildSymbolTable;

import java.util.Iterator;


public class Main {
    public static void main(String[] args){
        new Main().run2("tmp2.txt");
    }

    public static void run2(String in){
        Lexer lexer = new Lexer(in);
        AST ast = new AST(lexer);
        ast.parse();
        BuildSymbolTable symbolTable = new BuildSymbolTable(ast);
        symbolTable.firstPass();
        symbolTable.firstRun();
        //ast.display();

    }

    public static void run(String in){

        Lexer lexer = new Lexer(in);
        Iterator<Token> it = lexer.iterator();
        Token token;
        while(it.hasNext()){
            token = it.next();
            System.out.println(token);
        }
    }
}
