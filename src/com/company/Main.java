package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        double a = 3e15;
        new Main().run(args[0]);

    }
    public static void run(String in){

        Lexer lexer = new Lexer(FileToString(in));
        Iterator<Token> it = lexer.iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }
    }

    public static String FileToString(String input){
        Scanner scanner = null;
        StringBuilder sb = null;
        try{
            scanner = new Scanner(new File(input));
            sb = new StringBuilder();


            while(scanner.hasNextLine()){
                sb.append(scanner.nextLine());
                sb.append("\n");
            }

        }
        catch (Exception e){
            System.err.println("Error " + e);
            System.exit(-1);
        }
        finally {
            scanner.close();
            return sb.toString();
        }
    }
}
