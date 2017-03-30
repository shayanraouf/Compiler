package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){

        new Main().run(args[0]);
    }

    public static void run(String input){
        try{
            Scanner scanner = new Scanner(new File(input));
            while(scanner.hasNext()){
                System.out.println(scanner.next());
            }
            scanner.close();
        }
        catch (FileNotFoundException e){
            System.err.println("File Not Found " + e);
            System.exit(-1);
        }
    }
}
