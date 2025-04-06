package com.github.marcelektro.langdetect;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class WikipediaScraper {

    public static void main(String[] args) throws Exception{

        var in = new HashMap<String, String>(){{
            put("en", "Knowledge");
            put("pl", "Wiedza");
            put("es", "Conocimiento");
            put("fr", "Connaissance");
        }};

        int numChars = 2_000;

        for (var entry : in.entrySet()) {
            String lang = entry.getKey();
            String topic = entry.getValue();

            String result = scrapeWikipedia(lang, topic, numChars);
            System.out.println("Result for " + lang + ": " + result);
        }
    }


    public static String scrapeWikipedia(String lang, String topic, int numChars) throws Exception {

        final var jsonResponse = retrieveWikiAsString(lang, topic);

        final var text = jsonResponse.substring(jsonResponse.indexOf("\"extract\":\"") + 11)
                .replace("\\n", "\n")
                .replace("\\\"", "\"");

        // get the first numChars, but counting only by a-z chars
        final var result = new StringBuilder();

        int charCount = 0;
        for (char c : text.toCharArray()) {

            if (Character.isLetter(c)) {
                charCount++;

                if (charCount > numChars)
                    break;
            }

            result.append(c);
        }

        // should not happen
        if (result.isEmpty()) {
            throw new RuntimeException("No content found for the given topic.");
        }

        return result.toString().trim();
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
