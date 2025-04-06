package com.github.marcelektro.langdetect;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LangClassifierEvaluator {

    private final Map<String, List<String>> langTrainData; // lang code -> list of texts from the files

    public LangClassifierEvaluator() {
        this.langTrainData = new HashMap<>();
    }

    public void loadScrapedData(File scrapedDataDir) {

        if (!scrapedDataDir.exists() || !scrapedDataDir.isDirectory()) {
            throw new IllegalArgumentException("scrapedDataDir must exist and be a directory");
        }

        final var languages = LangClassifier.getLanguageDirectories(scrapedDataDir);
        LangClassifier.loadDataToMemory(this.langTrainData, languages, 50_000);

        System.out.println("[Init] Fetched test data for " + this.langTrainData.size() + " languages.");

    }


    /**
     * Test how well the classifier works with the test data.
     * Print the percentage of correctly classified texts for each language.
     */
    public void evaluate(LangClassifier classifier) {

        System.out.println("[Eval] Evaluating classifier...");

        for (var entry : this.langTrainData.entrySet()) {

            var lang = entry.getKey();

            int correct = 0;
            int total = 0;

            for (String text : entry.getValue()) {
                var result = classifier.classify(text);
                if (result.equals(lang)) {
                    correct++;
                }
                total++;
            }

            System.out.println("[Eval] " + lang + ": " + correct + "/" + total + " (" + (correct * 100.0 / total) + "%)");

        }

        System.out.println("[Eval] Finished evaluating classifier.");



    }



}
