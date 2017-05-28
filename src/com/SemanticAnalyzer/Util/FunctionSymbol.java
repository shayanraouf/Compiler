package com.SemanticAnalyzer.Util;

import com.LexicalAnalysis.Token;
import com.LexicalAnalysis.Type;

/**
 * Created by shayanraouf on 5/26/2017.
 */
public class FunctionSymbol extends Symbol {
    public FunctionSymbol(Token token) {
        super(token);
    }

    public FunctionSymbol(Token token, Type t) {
        super(token,t);
    }

}
