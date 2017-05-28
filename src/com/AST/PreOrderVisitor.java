package com.AST;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by shayanraouf on 5/24/2017.
 */
public class PreOrderVisitor{
    Queue<AST> stack = new LinkedList<>();
    public PreOrderVisitor(AST root) {
        init(root);
    }
    public void init(AST root) {
        for (AST tree : root.children) {
            init2(tree);
        }

    }
    private void init2(AST treeNode){
        if(treeNode != null){
            stack.add(treeNode);
            for(AST child: treeNode.children){
                init2(child);
            }
        }
    }
    public void display(){
        while(!stack.isEmpty()){
            System.out.println(stack.poll());
        }
    }



}
