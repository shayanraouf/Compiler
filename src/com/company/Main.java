package com.company;

import java.io.*;
import java.util.Iterator;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
//        String s = "sadfjhsafjksafksd asd f asd3";
//        CharSequence charSequence = new StringBuilder(s);



        double a4sf3 = 3e15;
        new Main().run("tmp.txt");

    }
    public static void run(String in){

        Lexer lexer = new Lexer(in);
        Iterator<Token> it = lexer.iterator();
        Token token;
        while(it.hasNext()){
            token = it.next();
            if(token == null) continue;
            System.out.println(token);
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
