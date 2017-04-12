/*
  Lexical Analyzer
  Authors: Shayan Raouf & Josh Trygg
  CSS 448 - Compilers - Bernstein
  Lexer.java
 */

import java.util.HashMap;
import java.util.Map;

public class TokenName {

    public static Map<String,String> names = new HashMap<String,String>()
    {{
        put("(","OPEN_PAREN");
        put(")","CLOSE_PAREN");
        put("[","OPEN_BRACKET");
        put("]","CLOSE_BRACKET");
        put("{","OPEN_BRACE");
        put("}","CLOSE_BRACE");
        put(",","COMMA");
        put(";","SEMICOLON");
        put("+","PLUS");
        put("-","MINUS");
        put("*","MULTIPLICATION");
        put("/","DIVISON");
        put("=","ASSIGNMENT_OPERATOR");
        put("==", "EQUALTO");
        put("!=","NOT_EQUALTO_ASSIGNMENT_OPERATOR");
        put(">","GREATER_THAN_OPERATOR");
        put(">>","IN_STREAM");
        put("<<","OUT_STREAM");
        put(">=","GREATER_THAN_EQUALTO_OPERATOR");
        put("<","LESS_THAN_OPERATOR");
        put("<=","LESS_THAN_EQUALTO_OPERATOR");
        put("!", "NOT_EQUALTO");
        put("~","BITWISE_NOT");
        put("&","BITWISE_AND");
        put("&&","LOGICAL_AND");
        put("|","BITWISE_OR");
        put("||","LOGICAL_OR");
        put("^","BITWISE_XOR");
    }};
}
