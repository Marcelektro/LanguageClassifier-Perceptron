package com.github.marcelektro.langdetect;

import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        var classifier = new LangClassifier();

        classifier.loadScrapedData(new File("./scraped/"));


        var classifierEvaluator = new LangClassifierEvaluator();
        classifierEvaluator.loadScrapedData(new File("./scraped_testing/"));

        classifierEvaluator.evaluate(classifier);


        var s = new Scanner(System.in);
        while (!Thread.currentThread().isInterrupted()) {

            System.out.println("Enter text to classify, or file (file://<path>) (or 'exit' to quit):");

            var input = s.nextLine();

            if (input.equals("exit")) {
                System.out.println("Exiting...");
                break;
            }

            if (input.startsWith("file://")) {
                var filePath = input.substring(7);
                var file = new File(filePath);
                if (!file.exists()) {
                    System.out.println("File does not exist! Are you sure you provided the correct path?");
                    continue;
                }
                System.out.println("Classifying file: " + file.getAbsolutePath());
                try {
                    var fileContents = LangClassifier.readStringFromFile(file, Integer.MAX_VALUE);

                    System.out.println("File contents length: " + fileContents.length());

                    var result = classifier.classify(fileContents);
                    System.out.println("Detected language: " + result);

                } catch (Exception e) {
                    System.out.println("Error reading file: " + e.getMessage());
                }

                continue;
            }

            var result = classifier.classify(input);
            System.out.println("Detected language: " + result);

        }

    }

}