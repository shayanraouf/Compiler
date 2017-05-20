package com.AST;

/**
 * Created by shayanraouf on 5/14/2017.
 */


import com.LexicalAnalysis.*;
import com.LexicalAnalysis.Byte;
import com.LexicalAnalysis.Number;

import java.util.*;
public class AST {

    private Iterator<Token> iterator;
    private Token currentToken;
    private Token nextToken;
    protected List<AST> children = new ArrayList<>();
    private Stack<Character> stack = new Stack<>();
    private HashMap<String, Integer> precedenceMap = new HashMap();

    /**
     * Constructor that accepts the Lexical Analyser
     * @param lexer
     */
    public AST(Lexer lexer){
        iterator = lexer.iterator();
        currentToken = null;
        nextToken = iterator.next();
        this.init_precedence_map();
    }
    public AST(Token t)       { currentToken = t; }

    public AST(int tokenType) { this.currentToken = new Token(tokenType); }

    /**
     *  Initialize Precedence Mapping
     *      (unary and binary operators)
     */
    public void init_precedence_map(){
        precedenceMap.put("||", 0);
        precedenceMap.put("&&", 1);
        precedenceMap.put("=", 2);
        precedenceMap.put("+", 3);
        precedenceMap.put("-", 4);
        precedenceMap.put("~", 5);
        precedenceMap.put("*", 6);
        precedenceMap.put("/", 7);
        precedenceMap.put("^", 8);
    }

    /**
     * program ::= statement*
     */
    public void parse(){
        while(hasNext()){
            readToken();
            AST ast = statement();
            if(ast != null){
                children.add(ast);
            }
        }
    }

    /**
     * statement ::= declaration-statement | block-statement |
     * for-statement | while-statement | if-statement | print-statement |
     * return-statement | exit-statement | expression-statement
     * @return
     */
    private AST statement() {
        if(currentToken == null) return null;

        if(isMatch(currentToken,"id")) return expression();

        if(isMatch(currentToken, "}")){             // checks if a close param and updates stack
            if(!stack.isEmpty()){
                if(stack.peek() == '{') stack.pop();
            }
        }

        if(currentToken == null) return null;

//        if(hasNext() && isMatch(currentToken, "{")){
//            stack.push('{');
//            return statement();        // block-statement |
//        }

        String keyword = currentToken.getType();
        switch(keyword){
            case "static": return variable_declaration();                // variable-declaration |
            case "const": return variable_declaration();                  // variable-declaration |
            case "var": return variable_declaration();
            case "for": return for_statement();                          // for-statement |
            case "while": return while_statement();                      // while-statement |
            case "if": return if_statement();                            // if-statement |
            case "print": return print_statement();                      // print-statement |
            case "return": return return_statement();                    // return-statement |
            case "exit": return exit_statement();                        // exit-statement |
            case "function": return function_declaration();              // function-declaration |
            case "type": return type_declaration();                      // type-declaration |
                         // variable-declaration |
            default:
                break;
        }
        return null;
    }

    /**
     * parameter ::=
     * refopt constopt identifier non-array-type-descriptor dimension-wildcardsopt
     * refopt constopt identifier = expression
     * @return AST
     */
    private AST parameter(){
        ExprNode param = new Node(new Token("parameter"));
        if(isMatch(currentToken,"ref")){
            param.addChild(new Node(currentToken));
            readToken();
        }

        if(isMatch(currentToken,"const")){
            param.addChild(new Node(currentToken));
            readToken();
        }

        param.addChild(new Node(currentToken)); // add the identifier
        readToken();

        param.addChild(non_array_type_descriptor());
        return param;
    }

    /**
     * parameters ::= ( parameters , )* parameter
     * @return AST
     */
    private AST parameters(){
        ExprNode parameters = new Node(new Token("parameter(s)"));

        while(hasNext()){                       // calls parameter() for every comma
            parameters.addChild(parameter());
            if(isMatch(nextToken,",")){
                readToken();
                readToken();
            }
            if(isMatch(currentToken,")") || isMatch(nextToken,")")) break;
        }
        return parameters;
    }

    /**
     * non-array-type-descriptor ::= record-descriptor | identifier | basic-type
     * @return AST
     */
    private AST non_array_type_descriptor(){
        ExprNode nonArrayTypeDescriptor = null;
        System.out.println(currentToken.getType());
        if(isMatch(currentToken,"record")){
            nonArrayTypeDescriptor = new Node(currentToken);
            nonArrayTypeDescriptor.addChild(record_descriptor());
        }
        else if(isMatch(currentToken,"keyword")){
            nonArrayTypeDescriptor = new IntNode(currentToken);
        }
        else if(isMatch(currentToken,"id")){
            nonArrayTypeDescriptor = new Node(currentToken);
        }
        return nonArrayTypeDescriptor;
    }

    /**
     * dimension ::= [ expressions ]
     * @return
     */
    private AST dimension(){
        AST dimension = new AST(new Token("dimension"));
        dimension.addChild(expressions());
        readToken();
        return dimension;
    }

    /**
     * expressions ::= ( expression , )* expression
     * @return
     */
    private AST expressions(){
        AST expressions = new AST(new Token("expression(s)"));
        while(hasNext() && !isMatch(currentToken,"]") && !isMatch(currentToken,";")){
            readToken();
            expressions.addChild(expression());
        }
        return expressions;
    }

    /**
     * function-declaration ::=
     * function identifier ( parametersopt ) type-descriptoropt block-statement
     * @return
     */
    private AST function_declaration() {
        AST function = new AST(currentToken);

        readToken();
        function.addChild(new Node(currentToken)); // save function identifier

        readToken(); // get next
        if(!isMatch(nextToken,")")){ // we have parameter(s)
            readToken();
            function.addChild(parameters());
        }

        readToken(); // get next
        readToken(); // get next

        readToken(); // get next
        stack.push('{');
        function.addChild(block_statement());
        return function;
    }

    /**
     * block-statement ::= { statement* }
     * @return
     */
    private ExprNode block_statement(){
        ExprNode blockStatement = new Node(new Token("block-statement"));

        while(hasNext() && nextToken != null){
            if(isMatch(currentToken, "}") || isMatch(nextToken,"}")){ // is close?
                if(!stack.isEmpty()){ // is it empty?
                    if(stack.peek() == '{'){
                        stack.pop();
                        if(stack.isEmpty()) break;
                    }
                }
            }

            AST nextTree = statement();
            if(nextTree == null) break;
            readToken();
            blockStatement.addChild(nextTree);
        }
        return blockStatement;
    }

    /**
     * while-statement ::= while ( expression ) block-statement
     * @return
     */
    private AST while_statement(){
        AST whileStatement = new AST(currentToken);

        readToken(); // read open '('
        readToken(); // must equal expression

        whileStatement.addChild(expression());
        readToken();// curr = , next =
        readToken(); // curr =  , next = statement
        stack.push('{');
        whileStatement.addChild(block_statement());
        return whileStatement;
    }

    /**
     * for-statement ::= for ( expression ; expression ; expression ) block-statement
     * @return
     */
    private AST for_statement() {
        AST forStatement = new AST(currentToken); // parent token -> for
        if(!isMatch(nextToken,"("))System.err.println("Expected '(' , Found: " + nextToken);
        ExprNode expr1 = null;
        ExprNode expr2 = null;
        ExprNode expr3 = null;

        readToken(); // curr  = '(' , next = 'expr' or ";"
        readToken();

        if(isMatch(currentToken,"id")){
            expr1 = expression();
        }

        //readToken(); // curr = 'expr' or ";"
        readToken();

        if(isMatch(currentToken,"id")){
            expr2 = expression();
        }

        //readToken(); // curr = ; , next = 'expr' or ";"
        readToken(); // curr = 'expr' or ";"

        if(isMatch(currentToken,"id")){
            expr3 = expression();
        }

        forStatement.addChild(expr1);
        forStatement.addChild(expr2);
        forStatement.addChild(expr3);

        stack.push('{');
        readToken(); // curr = rehash
        readToken(); // curr = rehash
        forStatement.addChild(block_statement());
        return forStatement;
    }

    /**
     * expression ::=
     variable = expression    // included
     ! expression
     ~ expression
     - expression

     expression + expression  // included
     expression - expression  // included
     expression * expression  // included
     expression / expression  // included
     expression | expression
     expression & expression
     expression ^ expression

     expression << expression
     expression >> expression
     expression == expression
     expression != expression
     expression < expression
     expression <= expression
     expression > expression
     expression >= expression
     expression && expression
     expression || expression
     type-cast
     function-call
     variable
     number
     string
     ( expression )
     * @return
     */
    private ExprNode expression(){
        ExprNode expr;   // tree
        expr = equals();
        return expr;
    }
    private ExprNode equals(){
        ExprNode expr;
        expr = E();
        while (isMatch(currentToken, "=")|| isMatch(currentToken, "!=") || isMatch(currentToken, "==")
                || isMatch(currentToken, "<")|| isMatch(currentToken, ">")/*      || isMatch(currentToken, "|")|| isMatch(currentToken, "^") || isMatch(currentToken, "==")
                || isMatch(currentToken, "!=")|| isMatch(currentToken, ">") || isMatch(currentToken, ">=")
                || isMatch(currentToken, "<=")|| isMatch(currentToken, "<") || isMatch(currentToken, "<<")
                || isMatch(currentToken, "<<")
          */
                ){
            Token op = currentToken;
            readToken();
            ExprNode expr1 = E();
            expr = new AddNode(expr, op, expr1);
        }
        return expr;
    }
    private ExprNode E(){
        ExprNode expr;
        expr = T();
        while (isMatch(currentToken, "+")|| isMatch(currentToken, "-")){
            Token op = currentToken;
            readToken();
            ExprNode expr1 = T();
            expr = new AddNode(expr, op, expr1);
        }
        return expr;
    }

    private ExprNode T(){
        ExprNode expr;
        expr = F();
        while (isMatch(currentToken, "*") || isMatch(currentToken, "/")){
            Token op = currentToken;
            readToken();
            ExprNode expr1 = F();
            expr = new AddNode(expr, op, expr1);
        }
        return expr;
    }

    private ExprNode F(){
        ExprNode expr;
        expr = P();
        if (isMatch(currentToken, "^")){
            Token op = currentToken;
            readToken();
            ExprNode expr1 = F();
            return new AddNode(expr, op, expr1);
        }
        else
            return expr;
    }

    private ExprNode P(){
        ExprNode expr;
        if(isMatch(currentToken, "id") || isMatch(currentToken, "basic-type")){   // are we at a terminal?
            Token v = currentToken;
            readToken();
            expr = new Node(v);
            return expr;
        }
        else if (isMatch(currentToken, "(")){   // open paren
            readToken();
            expr = E();
            expect(")");
            return expr;
        }
        else if(isUnary(currentToken)){     // unary operator
            readToken();
            expr = F();
            return null;
        }
        else {
            System.out.println("ERROR!!!!!");
            return null;
        }
    }

    private void expect(String tok){
        if (currentToken.getType() == tok)
            readToken();
        else if(tok == null)
            System.out.println("NO MORE TOKENS");
        else
            System.out.println("ERROR!");
    }


    private boolean isBinary(){
            return true;
    }
    private boolean isUnary(Token tok){
        return false;
    }
    private int precedence(){
        return precedenceMap.get(currentToken.getType());
    }
/*
    --------------- End Expression section -----------------------------------------------------
 */



    /**
     * return-statement ::= return expressionopt ;
     * @return
     */
    private AST return_statement(){
        AST return_statement = new AST(new Token("return-statement"));
        if(isMatch(nextToken,";")) return return_statement;
        readToken();
        return_statement.addChild(expression());
        return return_statement;
    }


    /**
     * print-statement ::= print expression ;
     * @return
     */
    private AST print_statement(){
        AST print_statement = new AST(new Token("print-statement"));
        readToken();
        print_statement.addChild(expression());
        return print_statement;
    }

    /**
     * if-statement ::= if ( expression ) block-statement ( else block-statement )opt
     * @return
     */
    private AST if_statement() {
        AST if_statement = new AST(currentToken);

        readToken(); // read open '('
        readToken(); // must equal expression

        if_statement.addChild(expression());
        readToken();// curr = close_paren, next = open_brace
        readToken(); // curr = open_brace , next = identifier
        stack.push('{');
        if_statement.addChild(block_statement());
        if(isMatch(nextToken,"else")){
            readToken();
            if_statement.addChild(else_statement());
        }
        return if_statement;

    }

    /**
     * ( else block-statement )opt
     * @return
     */
    private AST else_statement() {
        AST else_statement = new AST(new Token("else-statement"));
        readToken();
        readToken();
        stack.push('{');
        else_statement.addChild(block_statement());
        return else_statement;
    }

    /**
     * dimension-wildcards ::= [ ( dimension-wildcards , ) * ]
     * @return
     */
    private AST dimension_wildcards() {
        return null;
    }

    /**
     * basic-type ::= byte | int32 | float64
     * @return
     */
    private AST basic_type(){
        return null;
    }

    /**
     * variable-declaration ::=
     *  staticopt constopt var identifier = expression ;
     *  staticopt constopt var identifier type-descriptor ;
     * @return
     */
    private AST variable_declaration(){
        AST variable_declaration = new AST(new Token("variable-declaration"));
        if(isMatch(currentToken,"static")){
            variable_declaration.addChild(new Node(currentToken));
            readToken();
        }

        if(isMatch(currentToken,"const")){
            variable_declaration.addChild(new Node(currentToken));
            readToken();
        }

        if(!isMatch(currentToken,"var")) error_message("var", currentToken);
        readToken();
        if(!isMatch(currentToken,"id")) error_message("identifier", currentToken);
        variable_declaration.addChild(new Node(currentToken));

        if(isMatch(nextToken,"=")){                      // case: = expression ;
            variable_declaration.addChild(expression());
        }
        else {                                              // case: type-descriptor ;
            readToken();
            variable_declaration.addChild(type_descriptor());
        }

        return variable_declaration;
    }

    /**
     * field-declarations ::= (field-declaration , )* field-declaration
     * @return
     */
    private AST field_declarations(){
        ExprNode field_declarations = new Node(new Token("field-declarations"));

        while(hasNext()){                       // calls parameter() for every comma
            field_declarations.addChild(field_declaration());
            if(isMatch(currentToken,",")){
                readToken();
                readToken();
            }
            if (currentToken.getType().equals("end")) return field_declarations;
            else if(isMatch(currentToken,")") || isMatch(nextToken,")")) break;
        }
        return field_declarations;
    }


    /**
     * field-declaration ::= identifier type-descriptor
     * @return
     */
    private AST field_declaration(){
        AST field_declaration = new AST(new Token("field-declaration"));
        field_declaration.addChild(new Node(currentToken)); // add identifier

        //if(!isMatch(currentToken,"id")) error_message("identifier", currentToken);
        readToken();
        field_declaration.addChild(type_descriptor());
        return field_declaration;
    }

    /**
     * record-descriptor ::= record field-declarations end
     * @return
     */
    private AST record_descriptor(){
        if(!isMatch(currentToken,"record"))error_message("record", currentToken);
        AST record_descriptor = new AST(new Token("record-descriptor"));
        readToken();
        record_descriptor.addChild(field_declarations());
        //if(!isMatch(currentToken,"end"))error_message("end", currentToken);
        record_descriptor.addChild(new Node(currentToken));
        return record_descriptor;
    }


    /**
     * type-declaration ::= type identifier type-descriptor ;
     * @return
     */
    private AST type_declaration(){
        AST type_declaration = new AST(new Token("type-declaration"));
        readToken();
        if(!isMatch(currentToken,"id")) error_message("identifier", currentToken);
        type_declaration.addChild(new Node(currentToken)); // add identifier
        readToken();
        type_declaration.addChild(type_descriptor());
        readToken();
        return type_declaration;
    }

    /**
     * type-descriptor ::= non-array-type-descriptor dimensionopt
     * @return
     */
    private AST type_descriptor(){
        AST type_descriptor = new AST(new Token("type-descriptor"));
        type_descriptor.addChild(non_array_type_descriptor());
        if(isMatch(nextToken,"[")){
            readToken();
            type_descriptor.addChild(dimension());
        }
        else readToken();

        return type_descriptor;
    }

    /**
     * exit-statement ::= exit expressionopt ;
     * @return
     */
    private AST exit_statement(){
        AST exit_statement = new Node(currentToken);
        if(isMatch(nextToken,";")) return exit_statement;
        readToken();
        exit_statement.addChild(expression());
        return exit_statement;
    }

    /**
     * expression-statement ::= expression ;
     * @return
     */
    private AST expression_statement(){
        ExprNode expression = expression();
        if(!isMatch(nextToken,";")) error_message(";",nextToken);
        return expression;
    }


    /**
     * type-cast ::= basic-type ( expression )
     * @return
     */
    private AST type_cast(){
        AST type_cast = new AST(new Token("type-cast"));
        type_cast.addChild(basic_type());
        readToken();
        readToken();
        type_cast.addChild(expression());
        readToken();
        readToken();
        return type_cast;
    }

    /**
     * function-call ::= identifier ( expressionsopt )
     * @return
     */
    private AST function_call(){
        AST function_call = new AST(new Token("function-call"));
        function_call.addChild(new Node(currentToken)); // identifier
        readToken();
        if(!isMatch(nextToken, ")")){
            function_call.addChild(expressions());
        }
        readToken();

        return function_call;
    }

    /**
     * variable ::= identifier subscriptopt ( . variable )*
     * @return
     */
    private AST variable(){
        AST variable = new AST(new Token("variable"));
        variable.addChild(new Node(currentToken));
        if(isMatch(nextToken, "[")){
            readToken();
            variable.addChild(subscript());
        }

        while(hasNext() && !isMatch(currentToken, ")") && isMatch(nextToken,")")){
            variable.addChild(variable());
        }
        return variable;
    }

    /**
     * subscript ::=  [ expressions ]
     * @return
     */
    private AST subscript(){
        AST subscript = new AST(new Token("subscript"));
        readToken(); // curr = expression, next = ]
        subscript.addChild(expression());
        readToken();
        return subscript;

    }

    /**
     *  number ::= character-literal | integer-literal | float-literal
     * @return
     */
    private AST number(){
        return null;
    }


    private void error_message(String expected, Token found){
        System.err.println("Expected: " + expected + " Found" + found);
    }

    /**
     * Private function that returns if it's a match given a token and string
     * @param token
     * @param s
     * @return
     */
    private boolean isMatch(Token token, String s){
        if(token == null) return false;
        switch (s){
            case ",": return token instanceof Comma;
            case "[": return token instanceof LeftBracket;
            case "]": return token instanceof RightBracket;
            case "}": return token instanceof RightBrace;
            case "{": return token instanceof LeftBrace;
            case "(": return token instanceof LeftParanthesis;
            case ")": return token instanceof RightParanthesis;
            case ";": return token instanceof SemiColon;
            case "id": return token instanceof Identifier;
            case "+": return token instanceof Addition;
            case "-": return token instanceof Subtraction;
            case "*": return token instanceof Multiplication;
            case "/": return token instanceof Division;
            case "<": return token.getType().equals("<");
            case ">": return token.getType().equals(">");
            case "=": return token.getType().equals("=");
            case "!=": return token.getType().equals("!=");
            case "==": return token.getType().equals("==");
            case "ref": return token.getType().equals("ref");
            case "const": return token.getType().equals("const");
            case "static": return token.getType().equals("static");
            case "var": return token.getType().equals("var");
            case "basic-type": return token instanceof Number;
            case "record": return token.getType().equals("record");
            case "int32": return token instanceof Int32;
            case "float64": return token instanceof Float64;
            case "byte": return token instanceof Byte;
            case "op": return token instanceof Operator;
            case "keyword": return token instanceof Keyword;
            case "if": return token.getType().equals("if");
            case "else": return token.getType().equals("else");
        }
        return false;
    }
/*
    private boolean isDeclaration(String s) {
        if (token == null) return false;
        switch (s) {
            case "int32": return true;
            case "byte": return true;
            case "const": return true;
            case "float64": return true;
            case "static" : return true;
                return token instanceof LeftBracket;
            case "]":
                return token instanceof RightBracket;
        }
    }*/

    public void print(int n){
        for(int i = 0; i < n; i++){
            System.out.print("+---");
        }
    }

    public void display() {
        System.out.println("Size of Tree " + children.size());

        for (AST tree : children) {
            display(tree, 1);
        }

    }
    private void display(AST treeNode, int level){

        if(treeNode != null){
            print(level);
            System.out.println(treeNode.currentToken);

            for(AST child: treeNode.children){
                display(child, level + 1);
            }

        }

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
