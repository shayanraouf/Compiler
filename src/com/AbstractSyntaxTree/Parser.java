package com.AbstractSyntaxTree;

import com.LexicalAnalysis.*;

import java.util.*;

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

        while(tokenizer.hasNext()){
            readToken();
            Line get = getNextLine();
            lines.add(get);
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
            case "print": return getNextPrintStatement();
            case "return": return getNextReturnStatement();
            default:
                break;
        }
        return null;
    }

    private ReturnStatement getNextReturnStatement() {
        return new ReturnStatement(getNextExpression());
    }

    private PrintStatement getNextPrintStatement() {
        PrintStatement printStatement = new PrintStatement("--print statement test--");
        return printStatement;
    }



    private ForStatement getNextForStatement() {
        readToken(); // read open paren for begin of for loop '('
        Expression expr1 = getNextExpression();
        Expression expr2 = getNextExpression();
        Expression expr3 = getNextExpression();
        BlockStatement statement = getNextBlockStatement();
        //readToken(); // read close paren
        return new ForStatement(expr1,expr2,expr3,statement);
    }

    private boolean match(Token t){
        return token.equals(t);
    }

    private Expression getNextExpression() {

        readToken(); // read first term
        Expression root = expr();
        return null;
    }

    private Expression expr(){

        while(tokenizer.hasNext() && !(token instanceof SemiColon)){

            readToken();
        }
        return null;
    }



    private Expression factor(){
        return null;
    }


//    def factor(tokens):
//            if match(tokens,'('):
//    result = expr(tokens)
//        if not match(tokens, ')'):
//    raise ParseError('expecting ")"')
//        return result
//    return literal(tokens)
//
//    def term(tokens):
//    result = factor(tokens)
//    while len(tokens) > 0:
//            if  match(tokens, '*'):
//    result *= factor(tokens)
//    elif match(tokens, '/'):
//    result /= factor(tokens)
//        else:
//                return result
//    return result


//    def expr(tokens):
//    result = term(tokens)
//    while len(tokens) > 0:
//            if match(tokens, '+'):
//    result += term(tokens)
//    elif match(tokens, '-'):
//    result -= term(tokens)
//        else:
//                return result
//    return result
    private BlockStatement getNextBlockStatement(){
        readToken(); // read open curly brace
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

//    private Expression getNextExpression(){
//        readToken(); // actual thing
//        //int i = 0;
//        Expression expr = expression();
//        //readToken(); // semicolon
////        while(tokenizer.hasNext() && token instanceof Comma) {
////            readToken();
////            expr.addExpression(expression());
////            readToken();
////        }
//        //if(tokenizer.hasNext()) readToken();
//        return expr;
//    }



    private Expression parsedExpression(List<Token> expressionList) {
//        for(Token t: expressionList){
//            System.out.print(t + "--");
//        }
        System.out.println();
        List<Token> left = new ArrayList<>();
        List<Token> right = new ArrayList<>();

        int i = 0;
        while(i < expressionList.size() && !expressionList.get(i).getType().equals("=")){
            left.add(expressionList.get(i));
            i++;
        }

        i++;
        while(i < expressionList.size()){
            right.add(expressionList.get(i));
            i++;
        }


//
//        for(Token t: left){
//            System.out.print(t + " ");
//        }
//
//        System.out.println();
//
//        for(Token t: right){
//            System.out.print(t + " ");
//        }

        return null;
    }

}
