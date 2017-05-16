package com.AbstractSyntaxTree;

/**
 * Created by shayanraouf on 5/14/2017.
 */
public class PrintStatement extends Statement {
    String stringToPrint;

    @Override
    public String toString() {
        return "PrintStatement{" +
                "stringToPrint='" + stringToPrint + '\'' +
                '}';
    }

    public PrintStatement(String stringToPrint) {
        this.stringToPrint = stringToPrint;
    }
}
