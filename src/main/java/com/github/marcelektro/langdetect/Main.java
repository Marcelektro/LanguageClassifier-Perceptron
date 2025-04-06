package com.github.marcelektro.langdetect;

import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        var classifier = new LangClassifier();

        classifier.loadScrapedData(new File("./scraped/"));



        var s = new Scanner(System.in);
        while (!Thread.currentThread().isInterrupted()) {
            var input = s.nextLine();

            if (input.equals("exit")) {
                break;
            }

            var result = classifier.classify(input);
            System.out.println("Detected language: " + result);


        }


        /*var testStr = "Welcome to another video on my channel!";
        var result = classifier.classify(testStr);

        System.out.println("Detected language: " + result);*/

    }

}