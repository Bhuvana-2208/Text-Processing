package textprocessing;

public class AnalysisResult {
    private int totalCharacters;
    private int charactersNoSpaces;
    private int totalWords;
    private int totalSentences;
    private int totalLines;
    private String longestWord = "N/A";
    private String shortestWord = "N/A";
    private double averageWordLength;
    private String mostFrequentWord = "N/A";
    private int mostFrequentCount;
    private int totalDigits;
    private int[] vowelCounts = new int[5];
    private int totalVowels;
    private double vowelPercent;
    private double fleschScore;
    private String readabilityLevel = "N/A";

    public int getTotalCharacters() { return totalCharacters; }
    public void setTotalCharacters(int totalCharacters) { this.totalCharacters = totalCharacters; }

    public int getCharactersNoSpaces() { return charactersNoSpaces; }
    public void setCharactersNoSpaces(int charactersNoSpaces) { this.charactersNoSpaces = charactersNoSpaces; }

    public int getTotalWords() { return totalWords; }
    public void setTotalWords(int totalWords) { this.totalWords = totalWords; }

    public int getTotalSentences() { return totalSentences; }
    public void setTotalSentences(int totalSentences) { this.totalSentences = totalSentences; }

    public int getTotalLines() { return totalLines; }
    public void setTotalLines(int totalLines) { this.totalLines = totalLines; }

    public String getLongestWord() { return longestWord; }
    public void setLongestWord(String longestWord) { this.longestWord = longestWord; }

    public String getShortestWord() { return shortestWord; }
    public void setShortestWord(String shortestWord) { this.shortestWord = shortestWord; }

    public double getAverageWordLength() { return averageWordLength; }
    public void setAverageWordLength(double averageWordLength) { this.averageWordLength = averageWordLength; }

    public String getMostFrequentWord() { return mostFrequentWord; }
    public void setMostFrequentWord(String mostFrequentWord) { this.mostFrequentWord = mostFrequentWord; }

    public int getMostFrequentCount() { return mostFrequentCount; }
    public void setMostFrequentCount(int mostFrequentCount) { this.mostFrequentCount = mostFrequentCount; }

    public int getTotalDigits() { return totalDigits; }
    public void setTotalDigits(int totalDigits) { this.totalDigits = totalDigits; }

    public int[] getVowelCounts() { return vowelCounts.clone(); }
    public void setVowelCounts(int[] vowelCounts) { this.vowelCounts = vowelCounts == null ? new int[5] : vowelCounts.clone(); }

    public int getTotalVowels() { return totalVowels; }
    public void setTotalVowels(int totalVowels) { this.totalVowels = totalVowels; }

    public double getVowelPercent() { return vowelPercent; }
    public void setVowelPercent(double vowelPercent) { this.vowelPercent = vowelPercent; }

    public double getFleschScore() { return fleschScore; }
    public void setFleschScore(double fleschScore) { this.fleschScore = fleschScore; }

    public String getReadabilityLevel() { return readabilityLevel; }
    public void setReadabilityLevel(String readabilityLevel) { this.readabilityLevel = readabilityLevel; }
}
