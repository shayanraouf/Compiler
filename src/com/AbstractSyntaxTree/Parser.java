package com.AbstractSyntaxTree;

import com.LexicalAnalysis.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by shayanraouf on 4/13/2017.
 */
public class Parser {
    private Iterator<Token> tokenizer;
    private Token token;
    Lexer lexer;
    public Parser(Lexer lexer){
        this.tokenizer = lexer.iterator();
        this.token = null;
        this.lexer = lexer;
    }

    private void readToken(){
        token = tokenizer.next();
    }

    public AST parse(){
        List<Line> lines = new ArrayList<>();

//        Iterator<Token> it = lexer.iterator();
//        Token token;
//        while(it.hasNext()){
//            token = it.next();
//            if(token == null) continue;
//
//            // if(token instanceof Statement){}
//            System.out.println(token);
//        }

        while(tokenizer.hasNext()){
            readToken();
            if(token == null) continue;
            //System.out.println(token);

             Line get = getNextLine();
             lines.add(get);
//            if(get != null){
//                lines.add(get);
//            }

        }
        return new AST(lines);
    }


    private Line getNextLine(){
        if(token == null) return null;
        int row = token.getRow();
        Statement statement = getNextStatement();

        return new Line(row,statement);
    }

    private Statement getNextStatement(){
        String keyword = token.getType();

        switch(keyword){
            case "function":
                readToken(); // read identifier
                String returnType = "void";
                String identifier = token.getType();
                Argument arguments = null;
                readToken(); // open paren

                readToken(); // close paren or parameter
                if(!(token instanceof RightParanthesis)){
                    arguments = getNextArgument();
                }


                readToken(); // open curly or return type
                if(!(token instanceof LeftBrace)){
                    returnType = token.getType();
                    readToken();
                }
                readToken(); // get the next token for the next recursive call

                Statement functionStatement = null;

                if(!(token instanceof RightBrace)){
                    functionStatement = getNextStatement();
                    readToken(); // read right brace
                }

                return new Function(identifier,arguments,functionStatement,returnType);

            default:
                break;
        }
        return null;
    }

    private Argument getNextArgument() {
        return null;
    }

    private Expression getNextExpression(){
        return null;
    }

}
