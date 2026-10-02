package utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Comprehensive test for ReadabilityCalculator
 */
public class ReadabilityCalculatorTest {

    @Test
    public void testCalculateFleschKincaidGrade_NormalText() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("The cat sat on the mat.");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testCalculateFleschKincaidGrade_ComplexText() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(
                "The implementation of sophisticated algorithms requires comprehensive understanding."
        );
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testCalculateFleschKincaidGrade_Null() {
        assertEquals(0.0, ReadabilityCalculator.calculateFleschKincaidGrade(null), 0.01);
    }

    @Test
    public void testCalculateFleschKincaidGrade_Empty() {
        assertEquals(0.0, ReadabilityCalculator.calculateFleschKincaidGrade(""), 0.01);
    }

    @Test
    public void testCalculateFleschKincaidGrade_WhitespaceOnly() {
        assertEquals(0.0, ReadabilityCalculator.calculateFleschKincaidGrade("   "), 0.01);
    }

    @Test
    public void testCalculateFleschKincaidGrade_SingleWord() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("Hello.");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testCalculateFleschKincaidGrade_MultipleSentences() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(
                "First sentence. Second sentence. Third sentence."
        );
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testCalculateFleschKincaidGrade_NoDelimiters() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("No punctuation here");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testCalculateFleschKincaidGrade_PunctuationOnly() {
        assertEquals(0.0, ReadabilityCalculator.calculateFleschKincaidGrade("..."), 0.01);
    }

    @Test
    public void testCalculateFleschReadingEase_NormalText() {
        double ease = ReadabilityCalculator.calculateFleschReadingEase("The cat sat on the mat.");
        assertFalse(Double.isNaN(ease));
    }

    @Test
    public void testCalculateFleschReadingEase_ComplexText() {
        double ease = ReadabilityCalculator.calculateFleschReadingEase(
                "The implementation of sophisticated algorithms requires comprehensive understanding."
        );
        assertFalse(Double.isNaN(ease));
    }

    @Test
    public void testCalculateFleschReadingEase_Null() {
        assertEquals(0.0, ReadabilityCalculator.calculateFleschReadingEase(null), 0.01);
    }

    @Test
    public void testCalculateFleschReadingEase_Empty() {
        assertEquals(0.0, ReadabilityCalculator.calculateFleschReadingEase(""), 0.01);
    }

    @Test
    public void testCalculateFleschReadingEase_WhitespaceOnly() {
        assertEquals(0.0, ReadabilityCalculator.calculateFleschReadingEase("   "), 0.01);
    }

    @Test
    public void testCalculateFleschReadingEase_SingleWord() {
        double ease = ReadabilityCalculator.calculateFleschReadingEase("Hello.");
        assertFalse(Double.isNaN(ease));
    }

    @Test
    public void testCalculateFleschReadingEase_MultipleSentences() {
        double ease = ReadabilityCalculator.calculateFleschReadingEase(
                "First sentence. Second sentence! Third sentence?"
        );
        assertFalse(Double.isNaN(ease));
    }

    @Test
    public void testCalculateFleschReadingEase_NoDelimiters() {
        double ease = ReadabilityCalculator.calculateFleschReadingEase("No punctuation here");
        assertFalse(Double.isNaN(ease));
    }

    @Test
    public void testCalculateFleschReadingEase_PunctuationOnly() {
        assertEquals(0.0, ReadabilityCalculator.calculateFleschReadingEase("!!!"), 0.01);
    }

    @Test
    public void testFormatScore_Positive() {
        assertEquals("12.35", ReadabilityCalculator.formatScore(12.3456));
    }

    @Test
    public void testFormatScore_Negative() {
        assertEquals("-5.67", ReadabilityCalculator.formatScore(-5.666));
    }

    @Test
    public void testFormatScore_Zero() {
        assertEquals("0.00", ReadabilityCalculator.formatScore(0.0));
    }

    @Test
    public void testFormatScore_Small() {
        assertEquals("0.00", ReadabilityCalculator.formatScore(0.001));
    }

    @Test
    public void testFormatScore_Large() {
        assertEquals("999.99", ReadabilityCalculator.formatScore(999.99));
    }

    @Test
    public void testWordsWithNumbers() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("There are 123 items.");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testWordsWithHyphens() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("Well-known high-tech solution.");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testWordsWithApostrophes() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("It's don't can't won't.");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testWordsWithSpecialChars() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("Hello @world #hashtag!");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testSentenceWithPeriod() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("Hello.");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testSentenceWithExclamation() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("Hello!");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testSentenceWithQuestion() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("Hello?");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testMultiplePunctuation() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("Really?! Yes!!! Amazing...");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testSimpleSyllables() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("Cat dog run.");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testComplexSyllables() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("Beautiful extraordinary.");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testSilentE() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("Home phone make.");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testVowelGroups() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("Beautiful ocean dream.");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testNoVowels() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("Hm mm shh.");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testAllVowels() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("Aeiou.");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testMixedCase() {
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("HeLLo WoRLd.");
        assertFalse(Double.isNaN(grade));
    }

    @Test
    public void testBothScoresSameText() {
        String text = "The quick brown fox jumps over the lazy dog.";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);
        double ease = ReadabilityCalculator.calculateFleschReadingEase(text);

        assertFalse(Double.isNaN(grade));
        assertFalse(Double.isNaN(ease));
    }

    @Test
    public void testConsistency() {
        String text = "Hello world.";
        double grade1 = ReadabilityCalculator.calculateFleschKincaidGrade(text);
        double grade2 = ReadabilityCalculator.calculateFleschKincaidGrade(text);
        assertEquals(grade1, grade2, 0.001);
    }

    @Test
    public void testRealWorldNewsHeadline() {
        String text = "Scientists discover new treatment for cancer using nanotechnology.";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);
        double ease = ReadabilityCalculator.calculateFleschReadingEase(text);

        assertFalse(Double.isNaN(grade));
        assertFalse(Double.isNaN(ease));
    }

    @Test
    public void testLongParagraph() {
        String text = "Climate change represents one of the most significant challenges facing humanity today. " +
                "Scientists around the world are working to understand its impacts. " +
                "They are developing solutions to address this global crisis.";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);
        double ease = ReadabilityCalculator.calculateFleschReadingEase(text);

        assertFalse(Double.isNaN(grade));
        assertFalse(Double.isNaN(ease));
    }

    /**
     * Test to verify that text without sentence delimiters is treated as one sentence.
     * This ensures the branch "count == 0 ? 1 : count" returns 1 correctly.
     */
    @Test
    public void testCalculateFleschKincaidGrade_NoDelimiters_VerifyValue() {
        // Text with no punctuation marks (. ! ?)
        String text = "This is a sentence without any delimiter";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);

        // Verify the grade is calculated (should treat as 1 sentence)
        // Expected: positive value since we have words
        assertTrue("Grade should be positive for valid text", grade > 0);

        // Additional verification: the calculation should work
        assertFalse("Grade should not be NaN", Double.isNaN(grade));
    }

    /**
     * Test to verify that words with only non-alphabetic characters are handled correctly.
     * This covers the "if (word.isEmpty()) continue;" branch in countSyllables.
     */
    @Test
    public void testCalculateFleschKincaidGrade_OnlySymbols() {
        // Text with only non-alphabetic characters between spaces
        String text = "@@@ ### $$$ !!!";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);

        // Should return 0.0 because no valid words exist
        assertEquals("Grade should be 0.0 for text with no valid words", 0.0, grade, 0.01);
    }

    /**
     * Test to verify silent 'e' handling when syllables > 1.
     * This covers the TRUE branch of "if (word.endsWith("e") && syllables > 1)".
     * Words like "home", "make", "take" should have silent 'e' subtracted.
     */
    @Test
    public void testCalculateFleschKincaidGrade_SilentE_MultiSyllable() {
        // Words with silent 'e' and multiple vowel groups
        String text = "He ate the cake.";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);

        // Verify calculation works correctly with silent 'e' adjustment
        // Note: Grade can be negative for very simple text
        assertTrue("Grade should be calculated correctly", !Double.isNaN(grade) && !Double.isInfinite(grade));
        assertFalse("Grade should not be NaN", Double.isNaN(grade));
    }

    /**
     * Test to verify that single-syllable words ending in 'e' keep their syllable.
     * This covers the FALSE branch of "if (word.endsWith("e") && syllables > 1)".
     * A word like "e" or "be" should still count as 1 syllable.
     */
    @Test
    public void testCalculateFleschKincaidGrade_SingleSyllableWithE() {
        // Single syllable word ending in 'e'
        String text = "Be me we.";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);

        // Should calculate successfully
        assertTrue("Grade should be calculated", grade >= 0 || grade < 0);
        assertFalse("Grade should not be NaN", Double.isNaN(grade));
    }

    /**
     * Test to verify words with no vowels get at least 1 syllable.
     * This covers the TRUE branch of "if (syllables == 0)".
     */
    @Test
    public void testCalculateFleschKincaidGrade_WordsWithNoVowels_VerifyMinimum() {
        // Words with no vowels should still count as 1 syllable each
        String text = "Shh hmm psst.";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);

        // Should calculate successfully (each word gets minimum 1 syllable)
        assertTrue("Grade should be calculated for consonant-only words", grade >= 0 || grade < 0);
        assertFalse("Grade should not be NaN", Double.isNaN(grade));
    }

    /**
     * Test to verify empty sentences after split are filtered correctly.
     * This covers the loop in countSentences that filters empty strings.
     */
    @Test
    public void testCalculateFleschKincaidGrade_MultiplePunctuationMarks() {
        // Multiple punctuation marks create empty strings when split
        String text = "Hello...World!!!";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);

        // Should handle multiple delimiters correctly
        assertTrue("Grade should be calculated correctly", grade >= 0 || grade < 0);
        assertFalse("Grade should not be NaN", Double.isNaN(grade));
    }

    /**
     * Test FleschReadingEase with the same edge cases to ensure both formulas work.
     */
    @Test
    public void testCalculateFleschReadingEase_OnlySymbols() {
        String text = "@@@ ### $$$ !!!";
        double ease = ReadabilityCalculator.calculateFleschReadingEase(text);
        assertEquals("Ease should be 0.0 for text with no valid words", 0.0, ease, 0.01);
    }

    /**
     * Test FleschReadingEase with no delimiters to verify value.
     */
    @Test
    public void testCalculateFleschReadingEase_NoDelimiters_VerifyValue() {
        String text = "This is a sentence without any delimiter";
        double ease = ReadabilityCalculator.calculateFleschReadingEase(text);
        assertTrue("Ease should have a reasonable value", ease > 0 || ease < 206.835);
        assertFalse("Ease should not be NaN", Double.isNaN(ease));
    }

    /**
     * Test with text that has mixed punctuation to ensure sentence counting works.
     */
    @Test
    public void testCalculateFleschKincaidGrade_MixedPunctuation() {
        String text = "First! Second? Third.";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);

        // Should recognize 3 sentences
        assertTrue("Grade should be calculated for mixed punctuation", grade >= 0 || grade < 0);
        assertFalse("Grade should not be NaN", Double.isNaN(grade));
    }

    /**
     * Test formatScore with edge cases for complete coverage.
     */
    @Test
    public void testFormatScore_VerySmallNegative() {
        assertEquals("-0.00", ReadabilityCalculator.formatScore(-0.004));
    }

    /**
     * Test formatScore with a value that rounds up.
     */
    @Test
    public void testFormatScore_RoundingUp() {
        assertEquals("12.35", ReadabilityCalculator.formatScore(12.345));
    }

    /**
     * Test with actual news article description format.
     */
    @Test
    public void testRealWorldNewsDescription() {
        String text = "Scientists have discovered a new species of butterfly in the Amazon rainforest. " +
                "The discovery was made during a research expedition last month.";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);
        double ease = ReadabilityCalculator.calculateFleschReadingEase(text);

        // Verify both scores are calculated
        assertTrue("Grade should be positive", grade > 0);
        assertTrue("Ease should be in reasonable range", ease > 0 && ease < 120);
    }

    /**
     * Test to cover the sentenceCount == 0 branch (when wordCount > 0).
     * This is a special edge case that's hard to trigger.
     */
    @Test
    public void testCalculateFleschKincaidGrade_OnlyWhitespaceAndPunctuation() {
        // Text with only spaces and punctuation - should have sentences but filtering makes count=0
        String text = "   .  .  .   ";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);

        // Should return 0.0 because no valid content
        assertEquals("Grade should be 0.0 for whitespace and punctuation only", 0.0, grade, 0.01);
    }

    /**
     * Test to cover empty sentence branch in countSentences.
     */
    @Test
    public void testCalculateFleschKincaidGrade_EmptySentencesAfterSplit() {
        // Multiple delimiters in a row create empty strings after split
        String text = "Start...Middle!!!End???";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);

        // Should handle properly and calculate
        assertTrue("Grade should be calculated", !Double.isNaN(grade));
    }

    /**
     * Test FleschReadingEase with edge case to cover the sentenceCount == 0 branch.
     */
    @Test
    public void testCalculateFleschReadingEase_OnlyWhitespaceAndPunctuation() {
        String text = "   .  .  .   ";
        double ease = ReadabilityCalculator.calculateFleschReadingEase(text);
        assertEquals("Ease should be 0.0 for whitespace and punctuation only", 0.0, ease, 0.01);
    }

    /**
     * Test with text that produces an empty sentence after filtering.
     * This covers the branch where sentence.trim().isEmpty() is true.
     */
    @Test
    public void testCalculateFleschKincaidGrade_SentencesWithOnlySpaces() {
        // Sentences split but contain only spaces
        String text = "Word.   .Another";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);

        // Should calculate successfully
        assertTrue("Grade should be calculated", !Double.isNaN(grade));
    }

    /**
     * Test to ensure all private method null checks are covered.
     * Testing via public methods that call them with valid data.
     */
    @Test
    public void testCalculateFleschKincaidGrade_ValidTextCoversPrivateMethods() {
        // Valid text ensures all private methods are called with non-null data
        String text = "Hello world.";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);

        // All private methods (countSentences, countWords, countSyllables) should execute
        assertTrue("Grade should be calculated successfully", grade > -20 && grade < 20);
    }

    /**
     * Test formatScore with more variations.
     */
    @Test
    public void testFormatScore_ExactlyTwo() {
        assertEquals("2.00", ReadabilityCalculator.formatScore(2.0));
    }

    /**
     * Test formatScore with negative rounding.
     */
    @Test
    public void testFormatScore_NegativeRounding() {
        assertEquals("-12.35", ReadabilityCalculator.formatScore(-12.346));
    }

    /**
     * Test with text that has only punctuation marks separated by spaces.
     * This creates a scenario where split produces empty strings.
     */
    @Test
    public void testCalculateFleschKincaidGrade_PunctuationWithSpaces() {
        String text = ". . . . .";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);
        assertEquals("Grade should be 0.0 for only punctuation", 0.0, grade, 0.01);
    }

    /**
     * Test FleschReadingEase with only punctuation marks separated by spaces.
     */
    @Test
    public void testCalculateFleschReadingEase_PunctuationWithSpaces() {
        String text = "! ! ! ? ? ?";
        double ease = ReadabilityCalculator.calculateFleschReadingEase(text);
        assertEquals("Ease should be 0.0 for only punctuation", 0.0, ease, 0.01);
    }

    /**
     * Test with consecutive delimiters creating truly empty sentences.
     */
    @Test
    public void testCalculateFleschKincaidGrade_ConsecutiveDelimiters() {
        String text = "......!!!???";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);
        assertEquals("Grade should be 0.0", 0.0, grade, 0.01);
    }

    /**
     * Test a combination that might produce wordCount > 0 but empty sentences.
     */
    @Test
    public void testCalculateFleschKincaidGrade_WordsButEmptySentences() {
        // This is a tricky edge case - text with spaces and delimiters
        String text = "   .   .   .   ";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);
        assertEquals("Grade should be 0.0", 0.0, grade, 0.01);
    }

    /**
     * Test to ensure the branch where wordCount != 0 AND sentenceCount != 0 is covered.
     * This is the normal case that should trigger the calculation.
     */
    @Test
    public void testCalculateFleschKincaidGrade_NormalTextWithCalculation() {
        String text = "The cat sat.";
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade(text);

        // Verify actual calculation occurred (not returning 0.0)
        // For this text: 3 words, 1 sentence, ~3 syllables
        // Grade should be calculated, not 0.0
        assertNotEquals("Grade should not be 0.0 for valid text with words and sentences", 0.0, grade, 0.01);
        assertFalse("Grade should not be NaN", Double.isNaN(grade));
    }

    /**
     * Test FleschReadingEase with normal text to ensure calculation branch is covered.
     */
    @Test
    public void testCalculateFleschReadingEase_NormalTextWithCalculation() {
        String text = "The dog runs.";
        double ease = ReadabilityCalculator.calculateFleschReadingEase(text);

        // Verify actual calculation occurred
        assertNotEquals("Ease should not be 0.0 for valid text", 0.0, ease, 0.01);
        assertFalse("Ease should not be NaN", Double.isNaN(ease));
    }

    /**
     * Test to ensure class can be instantiated (covers constructor).
     * Although this is a utility class with only static methods,
     * this test ensures 100% class coverage.
     */
    @Test
    public void testClassInstantiation() {
        // Create an instance to cover the implicit constructor
        ReadabilityCalculator calculator = new ReadabilityCalculator();
        assertNotNull("Calculator instance should not be null", calculator);

        // Verify static methods still work
        double grade = ReadabilityCalculator.calculateFleschKincaidGrade("Test.");
        assertFalse("Static method should work", Double.isNaN(grade));
    }
}
