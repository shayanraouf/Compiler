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
import com.CodeGeneration.Generate;
import java.util.Iterator;

public class Main {
    public static void main(String[] args){
        new Main().run("tmp2.txt");
    }

    public static void run(String in){
        Lexer lexer = new Lexer(in);
//        Iterator<Token> it = lexer.iterator();
//        while(it.hasNext()){
//            Token t = it.next();
//            System.out.println(t + " " + System.identityHashCode(t));
//        }

        AST ast = new AST(lexer);
        ast.parse();

        BuildSymbolTable symbolTable = new BuildSymbolTable(ast);
//        symbolTable.decorateFirstPass();
//        symbolTable.decorateFirstPass();
        symbolTable.buildTable();
        //ast.display();

        Generate generator = new Generate(ast);
        generator.GenCode();
    }
}