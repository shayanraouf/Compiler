/*
  Lexical Analyzer
  Authors: Shayan Raouf & Josh Trygg
  CSS 448 - Compilers - Bernstein
  Lexer.java
 */
package com.LexicalAnalysis;

public class LeftBracket extends Token{

    public LeftBracket(int r, int c){
        super("[",r,c);
    }
}