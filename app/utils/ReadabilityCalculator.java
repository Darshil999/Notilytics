package utils;

import java.util.regex.Pattern;

/**
 * ReadabilityCalculator
 *
 * Utility class for calculating text readability metrics using
 * Flesch-Kincaid formulas. Analyzes text complexity based on
 * word count, sentence count, and syllable count.
 *
 * Part of Individual Task (e) - Description Readability
 * for the NotiLytics project.
 *
 */
public class ReadabilityCalculator {

    // Regex patterns for text analysis
    private static final Pattern SENTENCE_PATTERN = Pattern.compile("[.!?]+");
    private static final Pattern WORD_PATTERN = Pattern.compile("\\b\\w+\\b");
    private static final Pattern VOWEL_PATTERN = Pattern.compile("[aeiouyAEIOUY]+");

    /**
     * Calculates the Flesch-Kincaid Grade Level of given text.
     *
     * Formula: 0.39 × (words/sentences) + 11.8 × (syllables/words) - 15.59
     *
     * Higher grade level = more complex text (requires higher education level)
     * Example: Grade 8 = readable by 8th grader
     *
     * @param text The text to analyze
     * @return Flesch-Kincaid Grade Level score, or 0.0 if text is empty
     */
    public static double calculateFleschKincaidGrade(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0.0;
        }

        int wordCount = countWords(text);
        int sentenceCount = countSentences(text);
        int syllableCount = countSyllables(text);

        if (wordCount == 0 || sentenceCount == 0) {
            return 0.0;
        }

        double wordsPerSentence = (double) wordCount / sentenceCount;
        double syllablesPerWord = (double) syllableCount / wordCount;

        return 0.39 * wordsPerSentence + 11.8 * syllablesPerWord - 15.59;
    }

    /**
     * Calculates the Flesch Reading Ease Score of given text.
     *
     * Formula: 206.835 - 1.015 × (words/sentences) - 84.6 × (syllables/words)
     *
     * Score interpretation:
     * 90-100: Very Easy (5th grade)
     * 80-89:  Easy (6th grade)
     * 70-79:  Fairly Easy (7th grade)
     * 60-69:  Standard (8th-9th grade)
     * 50-59:  Fairly Difficult (10th-12th grade)
     * 30-49:  Difficult (College)
     * 0-29:   Very Confusing (College graduate)
     *
     * @param text The text to analyze
     * @return Flesch Reading Ease Score, or 0.0 if text is empty
     */
    public static double calculateFleschReadingEase(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0.0;
        }

        int wordCount = countWords(text);
        int sentenceCount = countSentences(text);
        int syllableCount = countSyllables(text);

        if (wordCount == 0 || sentenceCount == 0) {
            return 0.0;
        }

        double wordsPerSentence = (double) wordCount / sentenceCount;
        double syllablesPerWord = (double) syllableCount / wordCount;

        return 206.835 - 1.015 * wordsPerSentence - 84.6 * syllablesPerWord;
    }

    /**
     * Counts the number of sentences in the text.
     * Sentences are delimited by periods, exclamation marks, or question marks.
     *
     * Note: Assumes text is already validated as non-null and non-empty by caller.
     *
     * @param text The text to analyze (must be non-null and non-empty)
     * @return Number of sentences
     */
    private static int countSentences(String text) {
        String[] sentences = SENTENCE_PATTERN.split(text.trim());

        // Filter out empty strings
        int count = 0;
        for (String sentence : sentences) {
            if (!sentence.trim().isEmpty()) {
                count++;
            }
        }

        // If no sentence delimiters found, treat as one sentence
        return count == 0 ? 1 : count;
    }

    /**
     * Counts the number of words in the text.
     * Words are sequences of alphanumeric characters.
     *
     * Note: Assumes text is already validated as non-null and non-empty by caller.
     *
     * @param text The text to analyze (must be non-null and non-empty)
     * @return Number of words
     */
    private static int countWords(String text) {
        java.util.regex.Matcher matcher = WORD_PATTERN.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Counts the approximate number of syllables in the text.
     *
     * Algorithm:
     * 1. Count vowel groups (consecutive vowels = 1 syllable)
     * 2. Subtract silent 'e' at end of words
     * 3. Ensure each word has at least 1 syllable
     *
     * Note: This is an approximation. Accurate syllable counting
     * requires a dictionary, but this algorithm is ~85-90% accurate.
     * Assumes text is already validated as non-null and non-empty by caller.
     *
     * @param text The text to analyze (must be non-null and non-empty)
     * @return Approximate number of syllables
     */
    private static int countSyllables(String text) {
        int totalSyllables = 0;
        String[] words = text.toLowerCase().split("\\s+");

        for (String word : words) {
            // Remove non-alphabetic characters
            word = word.replaceAll("[^a-z]", "");

            if (word.isEmpty()) {
                continue;
            }

            int syllables = 0;

            // Count vowel groups
            java.util.regex.Matcher matcher = VOWEL_PATTERN.matcher(word);
            while (matcher.find()) {
                syllables++;
            }

            // Adjust for silent 'e' at the end
            if (word.endsWith("e") && syllables > 1) {
                syllables--;
            }

            // Every word has at least one syllable
            if (syllables == 0) {
                syllables = 1;
            }

            totalSyllables += syllables;
        }

        return totalSyllables;
    }

    /**
     * Formats a readability score to 2 decimal places.
     *
     * @param score The score to format
     * @return Formatted score string
     */
    public static String formatScore(double score) {
        return String.format("%.2f", score);
    }
}
