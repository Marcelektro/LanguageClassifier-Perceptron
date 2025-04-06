package com.github.marcelektro.langdetect;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

public class WikipediaScraper {

    public static void main(String[] args) throws Exception{

        final var in = new HashMap<String, List<String>>(){{
            put("en", List.of(
                    "Knowledge",
                    "Human",
                    "Science",
                    "Computer",
                    "Language",
                    "Computer_programming"
            ));
            put("pl", List.of(
                    "Wiedza",
                    "Człowiek_rozumny",
                    "Nauka",
                    "Komputer",
                    "Język_(mowa)",
                    "Programowanie_komputerów"
            ));
            put("es", List.of(
                    "Conocimiento",
                    "Homo_sapiens",
                    "Ciencia",
                    "Computadora",
                    "Lenguaje",
                    "Programación"
            ));
            put("fr", List.of(
                    "Connaissance",
                    "Homo_sapiens",
                    "Science",
                    "Ordinateur",
                    "Langage",
                    "Programmation_informatique"
            ));
        }};

        final var outputDir = new File("./scraped/");

        if (outputDir.mkdirs())
            System.out.println("[WikipediaScraper] Created output dir.");

        for (final var entry : in.entrySet()) {
            final var lang = entry.getKey();

            final var langDir = new File(outputDir, lang);

            if (langDir.mkdirs())
                System.out.println("[WikipediaScraper] Created lang output dir.");

            final var topics = entry.getValue();

            for (final var topic : topics) {
                final var outputFile = new File(langDir, "wiki_" + topic + ".txt");

                scrapeWikipedia(lang, topic, outputFile);
            }
        }
    }


    public static void scrapeWikipedia(String lang, String topic, File outputFile) throws Exception {

        final var jsonResponse = retrieveWikiAsString(lang, topic);

        final var text = jsonResponse.substring(jsonResponse.indexOf("\"extract\":\"") + 11)
                .replace("\\n", "\n")
                .replace("\\\"", "\"");

        Files.writeString(outputFile.toPath(), text);

    }


    private static String retrieveWikiAsString(String lang, String topic) throws Exception {

        final var urlFormat = "https://%s.wikipedia.org/w/api.php?action=query&prop=extracts&explaintext&titles=%s&format=json&utf8=true";

        final var url = String.format(
                urlFormat,
                URLEncoder.encode(lang, StandardCharsets.UTF_8),
                URLEncoder.encode(topic, StandardCharsets.UTF_8)
        );

        System.out.println("[WikipediaScraper] URL: " + url);

        final var con = (HttpsURLConnection) new URI(url).toURL().openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Accept-Charset", "UTF-8");
        con.setRequestProperty("Accept", "application/json; charset=utf-8");

        final var responseCode = con.getResponseCode();

        if (responseCode != 200) {
            System.out.println("Error: " + responseCode);
            throw new RuntimeException("Unsatisfactory response code: " + responseCode);
        }

        final var response = new StringBuilder();

        // not cool to load the whole response into memory, but it's usually small
        try (final var in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        return response.toString();
    }


}
