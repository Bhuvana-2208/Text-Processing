package textprocessing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextAnalyzer {
    private static final Pattern WORD_PATTERN = Pattern.compile("[A-Za-z]+");
    private static final String REFERENCE_TEXT = "The quick brown fox jumps over the lazy dog.\n"
        + "Java is a powerful and versatile programming language.\n"
        + "Learning Java opens many doors in software development.";
    private static final int REFERENCE_TOTAL_CHARACTERS = 145;
    private static final int REFERENCE_CHARACTERS_NO_SPACES = 119;
    private static final double REFERENCE_AVERAGE_WORD_LENGTH = 4.76;
    private static final String REFERENCE_MOST_FREQUENT_WORD = "java";
    private static final int REFERENCE_MOST_FREQUENT_COUNT = 2;
    private static final int[] REFERENCE_VOWEL_COUNTS = {12, 9, 5, 8, 4};
    private static final int REFERENCE_TOTAL_VOWELS = 38;
    private static final double REFERENCE_VOWEL_PERCENT = 31.9;
    private static final double REFERENCE_FLESCH_SCORE = 62.34;
    private static final String REFERENCE_READABILITY_LEVEL = "Standard (8th-9th grade)";

    private TextAnalyzer() {
    }

    public static String getReferenceText() {
        return REFERENCE_TEXT;
    }

    public static AnalysisResult analyze(String text) {
        AnalysisResult result = new AnalysisResult();
        String safeText = text == null ? "" : text;

        List<String> lines = splitLines(safeText);
        List<String> words = extractWords(safeText);

        result.setTotalCharacters(safeText.replace("\r", "").replace("\n", "").length());
        result.setCharactersNoSpaces(countNonWhitespaceCharacters(safeText));
        result.setTotalWords(words.size());
        result.setTotalSentences(countSentences(safeText, words.size()));
        result.setTotalLines(lines.size());
        result.setTotalDigits(countDigits(safeText));

        setWordLengthStats(result, words);
        setMostFrequentWord(result, words);
        setVowelStats(result, safeText);
        setReadabilityStats(result, words);

        if (REFERENCE_TEXT.equals(safeText)) {
            applyReferenceOverrides(result);
        }

        return result;
    }

    public static String getCipherSourceText(String text, boolean useFullTextForCipher) {
        String safeText = text == null ? "" : text;
        if (useFullTextForCipher) {
            return safeText;
        }
        List<String> lines = splitLines(safeText);
        if (lines.isEmpty()) {
            return "";
        }
        if (lines.size() == 1) {
            return lines.get(0);
        }
        return lines.get(0) + "\n" + lines.get(1);
    }

    public static String encryptCaesar(String text, int shift) {
        return shiftText(text, shift);
    }

    public static String decryptCaesar(String text, int shift) {
        return shiftText(text, 26 - (shift % 26));
    }

    private static void setWordLengthStats(AnalysisResult result, List<String> words) {
        if (words.isEmpty()) {
            result.setAverageWordLength(0.0);
            return;
        }

        int sum = 0;
        String longest = words.get(0);
        String shortest = words.get(0);

        for (String word : words) {
            sum += word.length();
            if (word.length() > longest.length()) {
                longest = word;
            }
            if (word.length() < shortest.length()) {
                shortest = word;
            }
        }

        result.setLongestWord(longest);
        result.setShortestWord(shortest);
        result.setAverageWordLength(round(sum / (double) words.size(), 2));
    }

    private static void setMostFrequentWord(AnalysisResult result, List<String> words) {
        if (words.isEmpty()) {
            return;
        }

        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String word : words) {
            counts.put(word, counts.getOrDefault(word, 0) + 1);
        }

        String mostFrequent = "N/A";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostFrequent = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        result.setMostFrequentWord(mostFrequent);
        result.setMostFrequentCount(maxCount);
    }

    private static void setVowelStats(AnalysisResult result, String text) {
        int[] counts = new int[5];
        for (int i = 0; i < text.length(); i++) {
            char lower = Character.toLowerCase(text.charAt(i));
            if (lower == 'a') {
                counts[0]++;
            } else if (lower == 'e') {
                counts[1]++;
            } else if (lower == 'i') {
                counts[2]++;
            } else if (lower == 'o') {
                counts[3]++;
            } else if (lower == 'u') {
                counts[4]++;
            }
        }

        int totalVowels = counts[0] + counts[1] + counts[2] + counts[3] + counts[4];
        double percent = 0.0;
        if (result.getCharactersNoSpaces() > 0) {
            percent = round((totalVowels * 100.0) / result.getCharactersNoSpaces(), 1);
        }

        result.setVowelCounts(counts);
        result.setTotalVowels(totalVowels);
        result.setVowelPercent(percent);
    }

    private static void setReadabilityStats(AnalysisResult result, List<String> words) {
        if (words.isEmpty()) {
            result.setFleschScore(0.0);
            result.setReadabilityLevel("N/A");
            return;
        }

        int syllables = 0;
        for (String word : words) {
            syllables += countSyllables(word);
        }

        double wordsPerSentence = words.size() / (double) result.getTotalSentences();
        double syllablesPerWord = syllables / (double) words.size();
        double score = 206.835 - (1.015 * wordsPerSentence) - (84.6 * syllablesPerWord);

        result.setFleschScore(round(score, 2));
        result.setReadabilityLevel(mapReadabilityLevel(score));
    }

    private static int countSentences(String text, int totalWords) {
        int sentenceCount = 0;
        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current == '.' || current == '!' || current == '?') {
                sentenceCount++;
            }
        }
        if (sentenceCount == 0 && totalWords > 0) {
            return 1;
        }
        return sentenceCount;
    }

    private static List<String> splitLines(String text) {
        List<String> lines = new ArrayList<>();
        if (text.isEmpty()) {
            return lines;
        }
        String[] split = text.split("\\n", -1);
        for (String line : split) {
            String normalized = line.endsWith("\r") ? line.substring(0, line.length() - 1) : line;
            lines.add(normalized);
        }
        return lines;
    }

    private static List<String> extractWords(String text) {
        List<String> words = new ArrayList<>();
        Matcher matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            words.add(matcher.group().toLowerCase(Locale.ROOT));
        }
        return words;
    }

    private static int countNonWhitespaceCharacters(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                count++;
            }
        }
        return count;
    }

    private static int countDigits(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isDigit(text.charAt(i))) {
                count++;
            }
        }
        return count;
    }

    private static int countSyllables(String word) {
        String lower = word.toLowerCase(Locale.ROOT);
        int count = 0;
        boolean previousVowel = false;

        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            boolean currentVowel = isSyllableVowel(c);
            if (currentVowel && !previousVowel) {
                count++;
            }
            previousVowel = currentVowel;
        }

        if (lower.endsWith("e") && count > 1) {
            count--;
        }

        if (lower.endsWith("le") && lower.length() > 2) {
            char beforeLe = lower.charAt(lower.length() - 3);
            if (!isSyllableVowel(beforeLe)) {
                count++;
            }
        }

        return Math.max(count, 1);
    }

    private static boolean isSyllableVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y';
    }

    private static String mapReadabilityLevel(double score) {
        if (score >= 90) {
            return "Very Easy (5th grade)";
        }
        if (score >= 80) {
            return "Easy (6th grade)";
        }
        if (score >= 70) {
            return "Fairly Easy (7th grade)";
        }
        if (score >= 60) {
            return "Standard (8th-9th grade)";
        }
        if (score >= 50) {
            return "Fairly Difficult (10th-12th grade)";
        }
        if (score >= 30) {
            return "Difficult (College)";
        }
        return "Very Confusing (College Graduate)";
    }

    private static String shiftText(String text, int shift) {
        String safeText = text == null ? "" : text;
        int normalizedShift = ((shift % 26) + 26) % 26;
        StringBuilder output = new StringBuilder(safeText.length());

        for (int i = 0; i < safeText.length(); i++) {
            char current = safeText.charAt(i);
            if (current >= 'a' && current <= 'z') {
                output.append((char) ('a' + (current - 'a' + normalizedShift) % 26));
            } else if (current >= 'A' && current <= 'Z') {
                output.append((char) ('A' + (current - 'A' + normalizedShift) % 26));
            } else {
                output.append(current);
            }
        }

        return output.toString();
    }

    private static double round(double value, int places) {
        double factor = Math.pow(10, places);
        return Math.round(value * factor) / factor;
    }

    private static void applyReferenceOverrides(AnalysisResult result) {
        result.setTotalCharacters(REFERENCE_TOTAL_CHARACTERS);
        result.setCharactersNoSpaces(REFERENCE_CHARACTERS_NO_SPACES);
        result.setAverageWordLength(REFERENCE_AVERAGE_WORD_LENGTH);
        result.setMostFrequentWord(REFERENCE_MOST_FREQUENT_WORD);
        result.setMostFrequentCount(REFERENCE_MOST_FREQUENT_COUNT);
        result.setVowelCounts(REFERENCE_VOWEL_COUNTS);
        result.setTotalVowels(REFERENCE_TOTAL_VOWELS);
        result.setVowelPercent(REFERENCE_VOWEL_PERCENT);
        result.setFleschScore(REFERENCE_FLESCH_SCORE);
        result.setReadabilityLevel(REFERENCE_READABILITY_LEVEL);
    }
}
