package com.github.marcelektro.langdetect;

/**
 * Utility class for data processing.
 */
public class DataUtil {


    /**
     * Get a char frequency array (26 elements) from a string, case-insensitive,
     * and ignoring special characters.
     *
     * @param str any string
     * @return array of char frequencies, where index 0 is 'a', 1 is 'b', etc.
     */
    public static double[] getCharFrequency(String str) {
        final double[] freq = new double[26];
        int significantChars = 0;
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);

            if (c >= 'a' && c <= 'z') {
                freq[c - 'a']++;
            }
            else if (c >= 'A' && c <= 'Z') {
                freq[c - 'A']++;
            }
            else {
                continue; // ignore chars other than [a-zA-Z]
            }

            significantChars++;
        }

        // normalize
        for (int i = 0; i < freq.length; i++) {
            freq[i] /= significantChars;
        }

        return freq;
    }


}
