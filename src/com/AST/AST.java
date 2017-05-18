package com.AST;

/**
 * Created by shayanraouf on 5/14/2017.
 */


import com.LexicalAnalysis.*;

import java.util.*;
public class AST {
    Iterator<Token> iterator;
    Token currentToken;
    Token nextToken;
    List<AST> children = new ArrayList<>();
    Stack<Character> stack = new Stack<>();
    public AST(Lexer lexer){
        iterator = lexer.iterator();
        currentToken = null;
        nextToken = iterator.next();
    }
    public AST()              { ; }
    public AST(Token t)       { currentToken = t; }

    public AST(int tokenType) { this.currentToken = new Token(tokenType); }

    public void parse(){
        while(hasNext()){
            readToken();
            AST ast = getNextTree();
            if(ast != null){
                children.add(ast);
            }
        }
    }

    private void display(AST treeNode, int level, int level2){

        if(treeNode == null) return;
        print(level);
        for(AST element: treeNode.children){
            if(element != null){
                System.out.println(element.currentToken);
                print(level);
            }
            display(element, level + 1, level2 + 1);
            display(element, level - 1, level2 + 1);


        }
    }

    public void print(int n){
        for(int i = 0; i < n; i++){
            System.out.print("+---");
        }
    }

    public void display() {
//        System.out.println(children.get(0).token);
//        System.out.println(children.get(0).children.get(0).token);
//
//
//
//        System.out.println(children.get(1).token);
//        System.out.println(children.get(1).children.get(0).token);
         System.out.println("Size of Tree " + children.size());
//        System.out.println(children.get(0).token);
        //System.out.println(children.get(1).token);
        for (AST tree : children) {
            //System.out.println(tree);
            display2(tree, 1);
        }

    }
        private void display2(AST treeNode, int level){

            if(treeNode != null){
                print(level);
                System.out.println(treeNode.currentToken);

                for(AST child: treeNode.children){
                    display2(child, level + 1);
                }

            }

        }



    private AST getNextTree() {
        if(currentToken == null) return null;
        if(isMatch(currentToken,"id")){
            return generateExpression();
        }

        if(isMatch(currentToken, "}")){
            if(!stack.isEmpty()){ // is it empty?
                if(stack.peek() == '{'){
                    stack.pop();
                    //if(stack.isEmpty())
                }
            }
            //readToken();
        }
        if(currentToken == null) return null;
        String keyword = currentToken.getType();

        switch(keyword){
            case "while": return getNextWhileStatement();
            case "print": return getNextPrintStatement();
            case "return": return getNextReturnStatement();
            case "function": return getNextFunction();
            case "for": return getNextForStatement();
            case "if": return getNextIfStatement();
            default:
                break;
        }
        return null;
    }
    private AST getNextFunction() {
        AST function = new AST(currentToken);
        if(!isMatch(nextToken,"id")) System.err.println("Expected identifier, Found: " + nextToken);

        readToken();
        function.addChild(new Node(currentToken)); // save function identifier

        if(!isMatch(nextToken,"("))System.err.println("Expected '(' , Found: " + nextToken);

        readToken(); // get next

        if(!isMatch(nextToken,")"))System.err.println("Expected ')' , Found: " + nextToken);
        // TODO: read in params

        readToken(); // get next
        if(!isMatch(nextToken,"{"))System.err.println("Expected '{' , Found: " + nextToken);

        readToken(); // get next
        readToken(); // get next
        stack.push('{');
        function.addChild(generateBlockStatement());
        return function;
    }

    private ExprNode generateBlockStatement(){
        ExprNode blockStatement = new Node(new Token("BlockStatement"));


//        if(isMatch(nextToken,"}")){ // is close?
//
//            if(!stack.isEmpty()){ // is it empty?
//                stack.pop();
//                if(stack.isEmpty()) return blockStatement;
//            }
//        }
//        AST nextTree = getNextTree();
//        if(nextTree == null) return blockStatement;
//        blockStatement.addChild(nextTree);

        while(hasNext() && nextToken != null){
            if(isMatch(currentToken, "}") || isMatch(nextToken,"}")){ // is close?
                if(!stack.isEmpty()){ // is it empty?
                    if(stack.peek() == '{'){
                        stack.pop();
                        if(stack.isEmpty()) break;
                    }
                }
            }

            AST nextTree = getNextTree();
            if(nextTree == null) break;
            readToken();
            blockStatement.addChild(nextTree);
        }
        return blockStatement;
    }

    private AST getNextWhileStatement(){
        AST whileStatement = new AST(currentToken);

        readToken(); // read open '('

        readToken(); // must equal expression
        ExprNode expression = generateExpression();
        whileStatement.addChild(expression);
        readToken(); // read '{'
        stack.push('{');
        whileStatement.addChild(generateBlockStatement());
        return whileStatement;
    }

    private AST getNextForStatement() {
        AST forStatement = new AST(currentToken); // parent token -> for
        readToken(); // open paren '('

        readToken();
        ExprNode expr1 = isMatch(currentToken,";") ? null: generateExpression();
        forStatement.addChild(expr1);
        readToken();
        ExprNode expr2 = isMatch(currentToken,";") ? null: generateExpression();
        forStatement.addChild(expr2);

        readToken();
        ExprNode expr3 = isMatch(currentToken,")") ? null: generateExpression();

        forStatement.addChild(expr3);

        readToken(); // open curly

        //readToken(); // read first token for block statement

        stack.push('{');
        forStatement.addChild(generateBlockStatement());
        System.out.println(currentToken);
        return forStatement;
    }



//foo(4)
    private ExprNode generateExpression(){

        ExprNode root;
        if(isMatch(currentToken,"id")){
            ExprNode idNode = new Node(currentToken);

            if(isMatch(nextToken,"=")){
                Token assignment = nextToken;
                readToken();
                readToken();
                ExprNode right = generateExpression();
                root = new AddNode(idNode,assignment,right);
                return root;
            }
            else if(isMatch(currentToken,"op")){
                Token operator = currentToken;
                readToken();

                root = new AddNode(idNode,operator,generateExpression());
                return root;

            }
            else if(isMatch(currentToken,"(")){
                readToken();
                ExprNode param = generateExpression();
                if(param != null){
                    idNode.addChild(param);
                }
                readToken();
                return idNode;
            }
        }
        else if(isMatch(currentToken,"int32")){
            ExprNode num = new IntNode(currentToken);
            if(isMatch(nextToken,"op")){
                Token operator = currentToken;
                readToken();
                ExprNode nextExpre = generateExpression();
                return new AddNode(num, operator,nextExpre);
            }
            else{
                return num;
            }
        }
        return null;
    }

    private boolean isMatch(Token token, String s){
        switch (s){
            case "}": return token instanceof RightBrace;
            case "{": return token instanceof LeftBrace;
            case "(": return token instanceof LeftParanthesis;
            case ")": return token instanceof RightParanthesis;
            case ";": return token instanceof SemiColon;
            case "var": return token instanceof Keyword;
            case "id": return token instanceof Identifier;
            case "=": return token.getType().equals("=");
            case "int32": return token instanceof Int32;
            case "op": return token instanceof Operator;
        }
        return false;
    }

    private AST getNextReturnStatement(){
        return null;
    }






    private AST getNextPrintStatement(){
        return null;
    }

    private AST getNextIfStatement() {
        return null;
    }






    public int getNodeType()  { return currentToken.type; }

    private void readToken(){
        currentToken = nextToken;
        nextToken = iterator.next();
    }

    private boolean hasNext(){
        return iterator.hasNext();
    }

    public void addChild(AST t) {
        if ( children==null ) children = new ArrayList<AST>();
        children.add(t);
    }
    public boolean isNil()    { return currentToken==null; }


    public String toString() { return currentToken.toString(); }


    public String toStringTree() {
        if ( children==null || children.size()==0 ) return this.toString();
        StringBuilder buf = new StringBuilder();
        if ( !isNil() ) {
            buf.append("(");
            buf.append(this.toString());
            buf.append(' ');
        }
        for (int i = 0; children!=null && i < children.size(); i++) {
            AST t = (AST)children.get(i);
            if ( i>0 ) buf.append(' ');
            buf.append(t.toStringTree());
        }
        if ( !isNil() ) buf.append(")");
        return buf.toString();
    }
}
