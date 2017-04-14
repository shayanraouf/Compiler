package com.AbstractSyntaxTree;

import com.LexicalAnalysis.Lexer;
import com.LexicalAnalysis.Token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by shayanraouf on 4/13/2017.
 */
public class Parser {
    private Iterator<Token> tokenizer;
    private Token token;

    public Parser(Lexer lexer){
        this.tokenizer = lexer.iterator();
        this.token = tokenizer.next();
    }

    private void readToken(){
        token = tokenizer.next();
    }

    public AST parse(){
        List<Line> lines = new ArrayList<>();
        while(tokenizer.hasNext()){
            Line get = getNextLine();
            lines.add(get);
        }
        return new AST(lines);
    }


    private Line getNextLine(){
        int row = token.getRow();
        Statement statement = getNextStatement();

        return new Line(row,statement);
    }

    private Statement getNextStatement(){

        String keyword = token.getType();
        switch(keyword){
            case "function": return new Function(); // obviously this will be more in depth

            case "":

                // etc.
        }

        return null;
    }


    private Expression getNextExpression(){
        return null;
    }

}
