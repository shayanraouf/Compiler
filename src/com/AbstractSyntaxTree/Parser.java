package com.AbstractSyntaxTree;

import com.LexicalAnalysis.*;
import com.LexicalAnalysis.Number;
import com.sun.xml.internal.bind.v2.TODO;

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
        this.token = null;
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
            //if(token == null) continue;
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
            case "function": return getNextFunction();
            case "for": return getNextForStatement();
            case "if": return getNextIfStatement();
            case "while": return getNextWhileStatement();
            default:
                break;
        }
        return null;
    }



    // TODO
    private ForStatement getNextForStatement() {
        readToken(); // read open paren
        Expression expr1 = getNextExpression();
        Expression expr2 = getNextExpression();
        Expression expr3 = getNextExpression();
        BlockStatement statement = getNextBlockStatement();

        return new ForStatement(expr1,expr2,expr3,statement);
    }

    private BlockStatement getNextBlockStatement(){
        readToken(); // read open paren
        Statement statement = getNextStatement();
        BlockStatement blockStatement = new BlockStatement(statement);
        readToken();
        while(tokenizer.hasNext()) {
            if(token instanceof RightBrace) break;
            blockStatement.addStatement(getNextStatement());
            readToken();
        }
        if(tokenizer.hasNext()) readToken();
        return blockStatement;
    }

    private IfStatement getNextIfStatement() {
        Expression expression = getNextExpression();
        BlockStatement blockStatement = getNextBlockStatement();
        BlockStatement elseBlockStatement = null;
        String type = token.getType();
        if(type.equals("else")){
            elseBlockStatement = getNextBlockStatement();
        }
        return new IfStatement(expression,blockStatement,elseBlockStatement);
    }

    private Statement getNextWhileStatement() {
        return null;
    }

    private Function getNextFunction(){
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
    }



    private Argument getNextArgument() {
        return null;
    }

    private Expression getNextExpression(){
        readToken();
        Expression expr = expression();
        readToken();
        while(tokenizer.hasNext() && token instanceof Comma) {
            expr.addExpression(expression());
            readToken();
        }
        if(tokenizer.hasNext()) readToken();
        return expr;
    }

    private Expression expression() {
        Expression expression = null;
        if(token instanceof Number){ // number ::= character-literal | integer-literal | float-literal

            if(token instanceof Int32){ // integer-literal

            }
            else if(token instanceof Float64){ //float-literal

            }
            // character-literal ??

        }
        else if(token instanceof Identifier){ // function-call | variable

        }
        return null;
    }

}
