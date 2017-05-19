package com.SemanticAnalyzer;

import com.AST.AST;
import com.LexicalAnalysis.Identifier;
import com.LexicalAnalysis.Keyword;
import com.LexicalAnalysis.Token;

import java.util.*;

/**
 * Created by shayanraouf on 5/18/2017.
 */
public class SymbolTable {
    public static void main(String args[]){
       Map<Integer, Symbol> table = new HashMap<>();
       Symbol s1 = new Symbol(new Identifier("foo",0,0));
       Symbol s2 = new Symbol(new Identifier("foo",0,0));
       table.put(s1.hashCode(),s1);
       table.put(s2.hashCode(),s2);

        for (Map.Entry<Integer, Symbol> entry : table.entrySet())
        {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
    }
        private Stack<HashSet<Symbol>> stack = new Stack<>();
        private Map<Integer, Symbol> table = new HashMap<>();
        public SymbolTable(){

        }

        public Symbol define(Token t){
            return new Symbol(t);
        }

        public void insert(Symbol symbol){
            table.put(symbol.hashCode(),symbol);
        }

        public Token lookup(Symbol value){
            return null;
        }


}
