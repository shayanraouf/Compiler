/*
  Compiler
  Authors: Shayan Raouf & Josh Trygg
  CSS 448 - Compilers - Bernstein
  Main.java
 */

package com;
import com.AST.AST;
import com.LexicalAnalysis.Lexer;
import com.SemanticAnalyzer.BuildSymbolTable;
import com.CodeGeneration.Generate;

public class Main {
    public static void main(String[] args){
        new Main().run("tmp2.txt");
    }

    public static void run(String in){
        Lexer lexer = new Lexer(in);

        AST ast = new AST(lexer);
        ast.parse();

        BuildSymbolTable symbolTable = new BuildSymbolTable(ast);
          symbolTable.buildTable();
          ast.display();

         Generate generator = new Generate(ast);
         generator.GenCode();
    }
}