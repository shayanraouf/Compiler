package com;

/**
 * Created by shayanraouf on 4/13/2017.
 */
public final class Line {
    private Statement statement;
    private int number;

    public Line(int number, Statement statement) {
        this.number = number;
        this.statement = statement;
    }

    public int getNumber() {
        return number;
    }

    public Statement getStatement() {
        return statement;
    }



    @Override
    public String toString() {
        return number + " " + statement;
    }



}
