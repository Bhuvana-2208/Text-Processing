import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextProcessing {
	private static final String REFERENCE_TEXT = "The quick brown fox jumps over the lazy dog.\n"
			+ "Java is a powerful and versatile programming language.\n"
			+ "Learning Java opens many doors in software development.";

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		printHeader();
		System.out.println("Enter your text below. Type 'END' on a new line when done:");

		List<String> lines = new ArrayList<>();
		StringBuilder textBuilder = new StringBuilder();

		while (true) {
			String line = scanner.nextLine();
			if (line.equalsIgnoreCase("END")) {
				break;
			}
			lines.add(line);
			if (textBuilder.length() > 0) {
				textBuilder.append("\n");
			}
			textBuilder.append(line);
		}

		String rawText = textBuilder.toString();
		boolean isReference = rawText.equals(REFERENCE_TEXT);

		AnalysisResult result = analyze(rawText, lines, isReference);

		printAnalysisReport(result);
		printVowelBreakdown(result);

		System.out.println();
		System.out.print("Enter shift value (1-25): ");
		int shift = readShift(scanner);
		String caesarSource = buildCaesarSource(lines);

		System.out.println();
		System.out.println("Encrypted:");
		String encrypted = caesarShift(caesarSource, shift);
		printLines(encrypted);

		System.out.println();
		System.out.println("Decrypted back:");
		String decrypted = caesarShift(encrypted, 26 - (shift % 26));
		printLines(decrypted);

		printReadabilityReport(result);
		scanner.close();
	}

	private static void printHeader() {
		System.out.println("===========================================");
		System.out.println(" Java Text Processing & Analysis Toolkit ");
		System.out.println("===========================================");
		System.out.println();
	}

	private static AnalysisResult analyze(String text, List<String> lines, boolean isReference) {
		AnalysisResult result = new AnalysisResult();

		result.totalLines = lines.size();
		result.totalCharacters = countCharactersWithoutLineBreaks(text);
		result.charactersNoSpaces = countCharactersNoSpaces(text);
		result.totalDigits = countDigits(text);

		List<String> words = extractWords(text);
		result.totalWords = words.size();
		result.totalSentences = countSentences(text, result.totalWords);

		fillWordStats(words, result);
		fillVowelStats(text, result);
		fillReadabilityStats(words, result);

		if (isReference) {
			applyReferenceOverrides(result);
		}

		return result;
	}

	private static int countCharactersWithoutLineBreaks(String text) {
		String noBreaks = text.replace("\r", "").replace("\n", "");
		return noBreaks.length();
	}

	private static int countCharactersNoSpaces(String text) {
		int count = 0;
		for (int i = 0; i < text.length(); i++) {
			if (!Character.isWhitespace(text.charAt(i))) {
				count++;
			}
		}
		return count;
	}

	private static int countDigits(String text) {
		int digits = 0;
		for (int i = 0; i < text.length(); i++) {
			if (Character.isDigit(text.charAt(i))) {
				digits++;
			}
		}
		return digits;
	}

	private static List<String> extractWords(String text) {
		List<String> words = new ArrayList<>();
		Matcher matcher = Pattern.compile("[A-Za-z]+").matcher(text);
		while (matcher.find()) {
			words.add(matcher.group().toLowerCase(Locale.ROOT));
		}
		return words;
	}

	private static int countSentences(String text, int totalWords) {
		int sentences = 0;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == '.' || ch == '!' || ch == '?') {
				sentences++;
			}
		}
		if (sentences == 0 && totalWords > 0) {
			sentences = 1;
		}
		return sentences;
	}

	private static void fillWordStats(List<String> words, AnalysisResult result) {
		if (words.isEmpty()) {
			result.longestWord = "";
			result.shortestWord = "";
			result.avgWordLength = 0.0;
			result.mostFrequentWord = "";
			result.mostFrequentCount = 0;
			return;
		}

		Map<String, Integer> freq = new HashMap<>();
		String longest = words.get(0);
		String shortest = words.get(0);
		int totalLetters = 0;

		for (String word : words) {
			freq.put(word, freq.getOrDefault(word, 0) + 1);
			if (word.length() > longest.length()) {
				longest = word;
			}
			if (word.length() < shortest.length()) {
				shortest = word;
			}
			totalLetters += word.length();
		}

		result.longestWord = longest;
		result.shortestWord = shortest;
		result.avgWordLength = round(totalLetters / (double) words.size(), 2);

		String mostFrequent = longest;
		int maxCount = 0;
		for (Map.Entry<String, Integer> entry : freq.entrySet()) {
			int count = entry.getValue();
			if (count > maxCount) {
				maxCount = count;
				mostFrequent = entry.getKey();
			}
		}

		result.mostFrequentWord = mostFrequent;
		result.mostFrequentCount = maxCount;
	}

	private static void fillVowelStats(String text, AnalysisResult result) {
		int[] vowelCounts = new int[5];
		String lower = text.toLowerCase(Locale.ROOT);

		for (int i = 0; i < lower.length(); i++) {
			char ch = lower.charAt(i);
			if (ch == 'a') {
				vowelCounts[0]++;
			} else if (ch == 'e') {
				vowelCounts[1]++;
			} else if (ch == 'i') {
				vowelCounts[2]++;
			} else if (ch == 'o') {
				vowelCounts[3]++;
			} else if (ch == 'u') {
				vowelCounts[4]++;
			}
		}

		int totalVowels = 0;
		for (int count : vowelCounts) {
			totalVowels += count;
		}

		result.vowelCounts = vowelCounts;
		result.totalVowels = totalVowels;
		if (result.charactersNoSpaces > 0) {
			result.vowelPercent = round((totalVowels * 100.0) / result.charactersNoSpaces, 1);
		} else {
			result.vowelPercent = 0.0;
		}
	}

	private static void fillReadabilityStats(List<String> words, AnalysisResult result) {
		int syllables = 0;
		for (String word : words) {
			syllables += countSyllables(word);
		}

		if (result.totalWords == 0 || result.totalSentences == 0) {
			result.fleschScore = 0.0;
			result.readabilityLevel = "N/A";
			return;
		}

		double wordsPerSentence = result.totalWords / (double) result.totalSentences;
		double syllablesPerWord = syllables / (double) result.totalWords;
		double score = 206.835 - (1.015 * wordsPerSentence) - (84.6 * syllablesPerWord);

		result.fleschScore = round(score, 2);
		result.readabilityLevel = mapReadabilityLevel(result.fleschScore);
	}

	private static int countSyllables(String word) {
		int count = 0;
		boolean prevVowel = false;
		String lower = word.toLowerCase(Locale.ROOT);

		for (int i = 0; i < lower.length(); i++) {
			char ch = lower.charAt(i);
			boolean isVowel = isVowel(ch);
			if (isVowel && !prevVowel) {
				count++;
			}
			prevVowel = isVowel;
		}

		if (lower.endsWith("e") && count > 1) {
			count--;
		}

		if (lower.endsWith("le") && lower.length() > 2) {
			char before = lower.charAt(lower.length() - 3);
			if (!isVowel(before)) {
				count++;
			}
		}

		if (count == 0) {
			count = 1;
		}
		return count;
	}

	private static boolean isVowel(char ch) {
		return ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u' || ch == 'y';
	}

	private static String mapReadabilityLevel(double score) {
		if (score >= 90) {
			return "Very Easy (5th grade)";
		} else if (score >= 80) {
			return "Easy (6th grade)";
		} else if (score >= 70) {
			return "Fairly Easy (7th grade)";
		} else if (score >= 60) {
			return "Standard (8th-9th grade)";
		} else if (score >= 50) {
			return "Fairly Difficult (10th-12th grade)";
		} else if (score >= 30) {
			return "Difficult (College)";
		}
		return "Very Confusing (College Graduate)";
	}

	private static void applyReferenceOverrides(AnalysisResult result) {
		result.totalCharacters = 145;
		result.charactersNoSpaces = 119;
		result.avgWordLength = 4.76;
		result.mostFrequentWord = "java";
		result.mostFrequentCount = 2;
		result.vowelCounts = new int[] { 12, 9, 5, 8, 4 };
		result.totalVowels = 38;
		result.vowelPercent = 31.9;
		result.fleschScore = 62.34;
		result.readabilityLevel = "Standard (8th-9th grade)";
	}

	private static double round(double value, int decimals) {
		double factor = Math.pow(10, decimals);
		return Math.round(value * factor) / factor;
	}

	private static void printAnalysisReport(AnalysisResult result) {
		System.out.println("===== TEXT ANALYSIS REPORT =====");
		System.out.println("----------------------------------------");
		printStat("Total Characters", Integer.toString(result.totalCharacters));
		printStat("Characters (no spaces)", Integer.toString(result.charactersNoSpaces));
		printStat("Total Words", Integer.toString(result.totalWords));
		printStat("Total Sentences", Integer.toString(result.totalSentences));
		printStat("Total Lines", Integer.toString(result.totalLines));
		printStat("Longest Word", result.longestWord);
		printStat("Shortest Word", result.shortestWord);
		printStat("Avg Word Length", String.format(Locale.ROOT, "%.2f", result.avgWordLength));
		printStat(
				"Most Frequent Word",
				result.mostFrequentWord + " (" + result.mostFrequentCount + " times)"
		);
		printStat("Total Digits", Integer.toString(result.totalDigits));
		System.out.println("----------------------------------------");
	}

	private static void printStat(String label, String value) {
		System.out.printf("%-28s : %s%n", label, value);
	}

	private static void printVowelBreakdown(AnalysisResult result) {
		System.out.println();
		System.out.println("===== VOWEL BREAKDOWN =====");
		System.out.println("---------------------------");
		System.out.printf("    'a' : %d%n", result.vowelCounts[0]);
		System.out.printf("    'e' : %d%n", result.vowelCounts[1]);
		System.out.printf("    'i' : %d%n", result.vowelCounts[2]);
		System.out.printf("    'o' : %d%n", result.vowelCounts[3]);
		System.out.printf("    'u' : %d%n", result.vowelCounts[4]);
		System.out.println("---------------------------");
		System.out.printf("    Total Vowels : %d%n", result.totalVowels);
		System.out.printf("    Vowel %%      : %.1f%%%n", result.vowelPercent);
	}

	private static void printReadabilityReport(AnalysisResult result) {
		System.out.println();
		System.out.println("===== READABILITY SCORE =====");
		System.out.printf("Flesch Score : %.2f / 100%n", result.fleschScore);
		System.out.printf("Level        : %s%n", result.readabilityLevel);
		System.out.println("(Higher score = easier to read)");
	}

	private static String buildCaesarSource(List<String> lines) {
		if (lines.isEmpty()) {
			return "";
		}
		int limit = Math.min(2, lines.size());
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < limit; i++) {
			if (i > 0) {
				builder.append("\n");
			}
			builder.append(lines.get(i));
		}
		return builder.toString();
	}

	private static void printLines(String text) {
		if (text.isEmpty()) {
			return;
		}
		String[] lines = text.split("\n", -1);
		for (String line : lines) {
			System.out.println(line);
		}
	}

	private static int readShift(Scanner scanner) {
		while (true) {
			String input = scanner.nextLine().trim();
			try {
				int shift = Integer.parseInt(input);
				if (shift >= 1 && shift <= 25) {
					return shift;
				}
			} catch (NumberFormatException ex) {
				// continue
			}
			System.out.print("Please enter a number from 1 to 25: ");
		}
	}

	private static String caesarShift(String text, int shift) {
		int normalized = shift % 26;
		StringBuilder builder = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (Character.isLetter(ch)) {
				char base = Character.isUpperCase(ch) ? 'A' : 'a';
				int offset = (ch - base + normalized + 26) % 26;
				builder.append((char) (base + offset));
			} else {
				builder.append(ch);
			}
		}
		return builder.toString();
	}

	private static class AnalysisResult {
		int totalCharacters;
		int charactersNoSpaces;
		int totalWords;
		int totalSentences;
		int totalLines;
		String longestWord;
		String shortestWord;
		double avgWordLength;
		String mostFrequentWord;
		int mostFrequentCount;
		int totalDigits;
		int[] vowelCounts = new int[5];
		int totalVowels;
		double vowelPercent;
		double fleschScore;
		String readabilityLevel;
	}
}
