package com.AbstractSyntaxTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shayanraouf on 5/14/2017.
 */
public class BlockStatement extends Statement{

    List<Statement> statements = new ArrayList<>();

    public BlockStatement(Statement statement){
        statements.add(statement);
    }

    public void addStatement(Statement statement){
        statements.add(statement);
    }
}
