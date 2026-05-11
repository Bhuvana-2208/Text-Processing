package textprocessing;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TextAnalyzerTest {

    @Test
    void analyzeBasicTextMatchesExpectedConsoleStyleValues() {
        AnalysisResult result = TextAnalyzer.analyze("Hello world");

        assertEquals(11, result.getTotalCharacters());
        assertEquals(10, result.getCharactersNoSpaces());
        assertEquals(2, result.getTotalWords());
        assertEquals(1, result.getTotalSentences());
        assertEquals(1, result.getTotalLines());
        assertEquals("hello", result.getLongestWord());
        assertEquals("hello", result.getShortestWord());
        assertEquals(5.00, result.getAverageWordLength());
        assertEquals("hello", result.getMostFrequentWord());
        assertEquals(1, result.getMostFrequentCount());
        assertEquals(0, result.getTotalDigits());
        assertArrayEquals(new int[] {0, 1, 0, 2, 0}, result.getVowelCounts());
        assertEquals(3, result.getTotalVowels());
        assertEquals(30.0, result.getVowelPercent());
        assertEquals(77.91, result.getFleschScore());
        assertEquals("Fairly Easy (7th grade)", result.getReadabilityLevel());
    }

    @Test
    void referenceTextAppliesExpectedOverrides() {
        AnalysisResult result = TextAnalyzer.analyze(TextAnalyzer.getReferenceText());

        assertEquals(145, result.getTotalCharacters());
        assertEquals(119, result.getCharactersNoSpaces());
        assertEquals(4.76, result.getAverageWordLength());
        assertEquals("java", result.getMostFrequentWord());
        assertEquals(2, result.getMostFrequentCount());
        assertArrayEquals(new int[] {12, 9, 5, 8, 4}, result.getVowelCounts());
        assertEquals(38, result.getTotalVowels());
        assertEquals(31.9, result.getVowelPercent());
        assertEquals(62.34, result.getFleschScore());
        assertEquals("Standard (8th-9th grade)", result.getReadabilityLevel());
    }

    @Test
    void cipherUsesFirstTwoLinesByDefaultAndSupportsFullTextToggle() {
        String text = "Line1\nLine2\nLine3";

        assertEquals("Line1\nLine2", TextAnalyzer.getCipherSourceText(text, false));
        assertEquals(text, TextAnalyzer.getCipherSourceText(text, true));
    }

    @Test
    void caesarShiftPreservesCaseAndNonLetters() {
        assertEquals("Cde ZaB!", TextAnalyzer.encryptCaesar("Abc XyZ!", 2));
        assertEquals("Abc XyZ!", TextAnalyzer.decryptCaesar("Cde ZaB!", 2));
    }
}
