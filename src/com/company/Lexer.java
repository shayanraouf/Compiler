package com.company;

import java.util.*;

/**
 * Created by shayanraouf on 3/28/2017.
 */
class Lexer implements Iterable<Token>{

    private String input;
    private List<Token> tokens;
    private Set<String> reservedKeyWords;
    private Map<Integer,String> symbolTable;
    char[] ASCII = {'(', ')','[',']'};


    public Lexer(String input){
        this.input = input;
        tokenize();
    }


    @Override
    public Iterator<Token> iterator(){
        Iterator<Token> it = new Iterator<Token>(){
            private int elementIndex = 0;

            @Override
            public boolean hasNext() {
                return elementIndex < tokens.size();
            }

            @Override
            public Token next() {
                return tokens.get(elementIndex++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }



    private void tokenize(){
        initializeMap();
        StringBuilder sb = new StringBuilder();
        int row = 1;
        int col = 1;
        for(int i = 1; i < input.length(); i++){ // for loop
            col++;
            char current = input.charAt(i);
            char lookAHead = input.charAt(i);
            
            if(isLetter(current)){
                // TODO: 3/30/2017  
                continue;
            }
            
            if(isDigit(current)){
                // TODO: 3/30/2017  
                continue;
            }
            
            
            switch (current){

                case '(':
                    tokens.add(new Operator("(",row,col));
                    break;
                case ')':
                    tokens.add(new Operator(")",row, col));
                    break;
                case ' ':
                    continue;

                case '\n':
                    col = 1;
                    row++;
                    continue;
                case '[':
                case ']':
                case '{':
                case '}':
                case ',':
                case ';':

                case '+':
                case '-':
                case '*':
                case '/':
                case '~':
                case '=':
                case '>':
                    // TODO: 3/30/2017
                    // case for >=

                case '<':
                    // TODO: 3/30/2017
                    // case for <=

                case '!':
                    // TODO: 3/30/2017
                    // case for !=
                case '&':
                    // TODO: 3/30/2017
                case '|':
                    // TODO: 3/30/2017

                 // TODO: 3/30/2017
                 // case letter

                // TODO: 3/30/2017
                // case letter


                 default:
                     break;
                     // TODO: 3/30/2017
                     // handle error

            }

        }


    }

    private void clearStringBuilder(StringBuilder sb){
        sb.setLength(0);
    }

    private boolean isLetter(char c){
        return false;
    }

    private boolean isDigit(char c){
        return false;
    }




    public void initializeMap(){
        reservedKeyWords = new HashSet<>();
        symbolTable = new HashMap<>();
        tokens = new ArrayList<>();

        //Adding all the operators
        reservedKeyWords.add("!");
        reservedKeyWords.add("!=");
        reservedKeyWords.add("+");
        reservedKeyWords.add("-");


        reservedKeyWords.add("*");
        reservedKeyWords.add("/");

        reservedKeyWords.add("&");
        reservedKeyWords.add("|");
        reservedKeyWords.add("~");

        reservedKeyWords.add(">");
        reservedKeyWords.add("<");
        reservedKeyWords.add(">=");
        reservedKeyWords.add("<=");

        reservedKeyWords.add("for");
        reservedKeyWords.add("this");
        reservedKeyWords.add("if");
        reservedKeyWords.add("else");
        reservedKeyWords.add("null");

    }

}




class Keyword extends Token{

    public Keyword(String s, int r, int c){
        super(s,r,c);
    }

}

class Operator extends Token{

    public Operator(String s, int r, int c){
        super(s,r,c);

    }

    @Override
    public String toString(){
        return super.toString() + " " + TokenName.names.get(super.text);
    }

    
}

class Identifier extends Token{
    public Identifier(String s,int r, int c){
        super(s,r,c);
    }


    public int hash(){
        return Math.abs(super.text.hashCode());
    }

}

class Comma extends Token{
    
    public Comma(String s, int r, int c){
        super(s,r,c);
    }
    
}



class SemiColon extends Token{

    public SemiColon(String s, int r, int c){
        super(s,r,c);
    }
}


class LeftParanthesis extends Token{
    public LeftParanthesis(String s, int r, int c){
        super(s,r,c);
    }

}

class RightParanthesis extends Token{

    public RightParanthesis(String s, int r, int c) {
        super(s,r,c);
    }
}

class LeftBracket extends Token{

    public LeftBracket(String s, int r, int c){
        super(s,r,c);
    }
}

class RightBracket extends Token{
    
    public RightBracket(String s, int r, int c){
        super(s,r,c);
    }
}


class LeftBrace extends Token{

    public LeftBrace(String s, int r, int c){
        super(s,r,c);
    }
}

class RightBrace extends Token{
    
    public RightBrace(String s, int r, int c){
        super(s,r,c);
    }
}

