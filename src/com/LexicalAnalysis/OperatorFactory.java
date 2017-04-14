package com.LexicalAnalysis;

/**
 * Created by shayanraouf on 4/14/2017.
 */
public class OperatorFactory{
    public static Operator createOp(char ch, int r, int c){
        Operator operator = null;

        switch (ch){
            case '+':
                operator = new Addition(r,c);
                break;
            case '-':
                operator = new Subtraction(r,c);
                break;
            case '*':
                operator = new Multiplication(r,c);
                break;
            case '/':
                operator = new Division(r,c);
                break;
            case '~':
                operator = new BitwiseNot(r,c);
                break;
            case '^':
                operator = new BitwiseXOR(r,c);
                break;
            default:
                throw new IllegalArgumentException("Not an Operator");
        }
        return operator;
    }
}