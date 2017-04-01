package com.company;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shayanraouf on 3/30/2017.
 */
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
        put("~","NOTSURE");
        put("=","ASSIGNMENT_OPERATOR");
        put(">","GREATER_THAN_OPERATOR");
        put(">=","GREATER_THAN_EQUALTO_OPERATOR");
        put("<","LESS_THAN_OPERATOR");
        put("<=","LESS_THAN_EQUALTO_OPERATOR");
        put("&","BITWISE_AND");
        put("&&","LOGICAL_AND");
        put("|","BITWISE_OR");
        put("||","LOGICAL_OR");
        put("^","BITWISE_XOR");

        // TODO: 3/30/2017 the rest


    }};
}
