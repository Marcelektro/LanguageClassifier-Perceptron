package com.github.marcelektro.langdetect;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

public class LangClassifier {

    private final Map<String, Perceptron> langPerceptron; // lang code -> perceptron
    private final Map<String, List<String>> langTrainData; // lang code -> list of texts from the files


    public LangClassifier() {
        this.langPerceptron = new HashMap<>();
        this.langTrainData = new HashMap<>();
    }


    public void loadScrapedData(File scrapedDataDir) {

        if (!scrapedDataDir.exists() || !scrapedDataDir.isDirectory()) {
            throw new IllegalArgumentException("scrapedDataDir must exist and be a directory");
        }

        final var languages = getLanguageDirectories(scrapedDataDir);
        this.loadDataToMemory(languages);

        for (String lang : this.langTrainData.keySet()) {
            System.out.println("[Init] Initializing " + lang);

            final var perceptron = new Perceptron(26, 0.2, 0.2);
            this.langPerceptron.put(lang, perceptron);
        }

        performLearn(400);

        System.out.println("[Init] Finished learning! Awaiting classification...");

    }


    private void performLearn(int epochs) {
        for (int i = 0; i < epochs; i++) {
            this.performLearn();
        }
    }


    private void performLearn() {
        for (var entry : this.langTrainData.entrySet()) {

            var lang = entry.getKey();
            var perceptron = this.langPerceptron.get(lang);

            Collections.shuffle(entry.getValue());

            learnFor(entry.getValue(), 1, perceptron);

            // for all other languages, learn with 0
            for (var otherLanguage : this.langTrainData.entrySet()) {

                if (otherLanguage.getKey().equals(lang))
                    continue;

                Collections.shuffle(entry.getValue());

                learnFor(otherLanguage.getValue(), 0, perceptron);
            }

        }
    }



    private void learnFor(List<String> dataList, int answer, Perceptron perceptron) {
        for (String data : dataList) {
            learnFromFile(data, answer, perceptron);
        }
    }

    private void learnFromFile(String data, int answer, Perceptron perceptron) {
        final var freq = DataUtil.getCharFrequency(data);

        perceptron.learn(freq, answer);
    }


    private String readStringFromFile(File file, int charsLimit) {
        try {

            final var str = Files.readString(file.toPath());

            final var result = new StringBuilder();
            int charCount = 0;
            for (char c : str.toCharArray()) {
                if (Character.isLetter(c)) {
                    charCount++;

                    if (charCount > charsLimit)
                        break;

                    result.append(Character.toLowerCase(c));
                }
            }

            return result.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error reading file " + file.getName(), e);
        }
    }



    private void loadDataToMemory(File[] languages) {

        for (File languageDir : languages) {
            final var lang = languageDir.getName();

            final var files = languageDir.listFiles(file -> file.getName().endsWith(".txt"));

            if (files == null || files.length == 0) {
                System.out.println("Skipping " + lang + " as it has no text files");
                continue;
            }

            var data = new ArrayList<String>(files.length);

            for (File file : files) {
                try {
                    final var str = readStringFromFile(file, 50_000);
                    data.add(str);
                } catch (Exception e) {
                    throw new RuntimeException("Error reading file " + file.getName(), e);
                }
            }

            this.langTrainData.put(lang, data);
        }

    }


    private File[] getLanguageDirectories(File scrapedDataDir) {
        final var languages = scrapedDataDir.listFiles(file -> {
            if (!file.isDirectory()) {
                System.out.println("Skipping " + file.getName() + " as it is not a directory");
                return false;
            }

            final var lang = file.getName();
            if (lang.length() == 2) {
                return true;
            }
            System.out.println("Skipping " + lang + " as it is not a valid language code");
            return false;
        });

        if (languages == null || languages.length < 2) {
            throw new IllegalArgumentException("scrapedDataDir must contain at least two language directories");
        }
        return languages;
    }





    public String classify(String text) {
        final var freq = DataUtil.getCharFrequency(text);
        String bestLang = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (Map.Entry<String, Perceptron> entry : this.langPerceptron.entrySet()) {
            final var lang = entry.getKey();
            final var perceptron = entry.getValue();
            final var score = perceptron.compute(freq);

            System.out.println("Score for " + lang + ": " + score);

            if (score > bestScore) {
                bestScore = score;
                bestLang = lang;
            }
        }

        return bestLang;
    }


}
