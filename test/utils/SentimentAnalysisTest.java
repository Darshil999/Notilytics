package utils;

import models.Article;
import utils.SentimentAnalysis;
import utils.SentimentTuple;

import org.junit.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static junit.framework.TestCase.*;

public class SentimentAnalysisTest {

    // lexicon behavior -- tested
    // AnalyzeSentiment() -- tested
    // extractWords() -- tested
    // numberOfNegAndPosWords() -- tested

    /**
     * Testing passing articles with positive sentiment.
     */
    @Test
    public void testPositiveSentiment() {
        // Create articles with words you know are positive in your lexicon
        Article article1 = createArticle("Wonderful and spectacular things happening");
        Article article2 = createArticle("Fantastic and incredible results");

        List<Article> articles = Arrays.asList(article1, article2);
        String result = SentimentAnalysis.analyzeSentiment(articles);

        assertEquals(":-)", result);
    }

    /**
     * Testing passing articles with negative sentiment.
     */
    @Test
    public void testNegativeSentiment() {
        Article article1 = createArticle("Awful terrible situation");
        Article article2 = createArticle("Disastrous and catastrophic outcome");

        List<Article> articles = Arrays.asList(article1, article2);
        String result = SentimentAnalysis.analyzeSentiment(articles);

        assertEquals(":-(", result);
    }

    /**
     * Testing passing articles with neutral sentiment.
     */
    @Test
    public void testNeutralSentiment() {
        Article article1 = createArticle("Mixed results, common occurrences.");

        List<Article> articles = Arrays.asList(article1);
        String result = SentimentAnalysis.analyzeSentiment(articles);

        assertEquals(":-|", result);
    }

    /**
     * Testing passing empty article list.
     */
    @Test
    public void testEmptyArticleList() {
        List<Article> articles = List.of();
        String result = SentimentAnalysis.analyzeSentiment(articles);
        assertEquals(":-|", result);
    }

    /**
     * Testing passing article with null description.
     */
    @Test
    public void testArticlesWithNullFields() {
        Article article = createArticle(null);
        List<Article> articles = List.of(article);
        String result = SentimentAnalysis.analyzeSentiment(articles);
        assertEquals(":-|", result);
    }

    /**
     * Testing articles with no sentiment words (neutral words only).
     */
    @Test
    public void testNoSentimentWords() {
        Article article = createArticle("the and of to in");
        List<Article> articles = List.of(article);
        String result = SentimentAnalysis.analyzeSentiment(articles);
        assertEquals(":-|", result);
    }

    /**
     * Testing case insensitivity, uppercase words should be processed the same.
     */
    @Test
    public void testCaseInsensitivity() {
        Article article1 = createArticle("WONDERFUL EXCELLENT GREAT");
        Article article2 = createArticle("wonderful excellent great");

        List<Article> articles1 = List.of(article1);
        List<Article> articles2 = List.of(article2);

        assertEquals(SentimentAnalysis.analyzeSentiment(articles1),
                SentimentAnalysis.analyzeSentiment(articles2));
    }

    /**
     * Testing word extraction with punctuation.
     */
    @Test
    public void testPunctuationHandling() {
        Article article = createArticle("Great! Wonderful, excellent.");
        List<Article> articles = List.of(article);
        String result = SentimentAnalysis.analyzeSentiment(articles);
        assertEquals(":-)", result);
    }

    /**
     * Testing extractWords method directly.
     */
    @Test
    public void testExtractWords() {
        List<String> words = SentimentAnalysis.extractWords("Hello, world! This is a test.");
        assertTrue(words.contains("hello"));
        assertTrue(words.contains("world"));
        assertTrue(words.contains("test"));
        assertEquals(6, words.size());
    }

    /**
     * Testing extractWords with null input.
     */
    @Test
    public void testExtractWordsNull() {
        List<String> words = SentimentAnalysis.extractWords(null);
        assertNotNull(words);
        assertTrue(words.isEmpty());
    }

    /**
     * Testing extractWords with empty string.
     */
    @Test
    public void testExtractWordsEmpty() {
        List<String> words = SentimentAnalysis.extractWords("");
        assertNotNull(words);
        assertTrue(words.isEmpty());
    }

    /**
     * Testing extractWords with apostrophes (contractions).
     */
    @Test
    public void testExtractWordsWithApostrophes() {
        List<String> words = SentimentAnalysis.extractWords("It's don't can't");
        assertTrue(words.contains("it's"));
        assertTrue(words.contains("don't"));
        assertTrue(words.contains("can't"));
    }

    /**
     * Testing boundary condition - exactly 71% positive.
     */
    @Test
    public void testBoundaryPositive() {
        // Create article with 71% positive words
        // 71 positive words and 29 negative words = 71%
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < 71; i++) {
            description.append("good ");
        }
        for (int i = 0; i < 29; i++) {
            description.append("bad ");
        }

        Article article = createArticle(description.toString());
        String result = SentimentAnalysis.analyzeSentiment(List.of(article));
        assertEquals(":-)", result);
    }

    /**
     * Testing boundary condition - exactly 71% negative.
     */
    @Test
    public void testBoundaryNegative() {
        // Create article with 71% negative words
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < 71; i++) {
            description.append("bad ");
        }
        for (int i = 0; i < 29; i++) {
            description.append("good ");
        }
        Article article = createArticle(description.toString());
        String result = SentimentAnalysis.analyzeSentiment(List.of(article));
        assertEquals(":-(", result);
    }

    /**
     * Testing exactly 70% positive (should be neutral).
     */
    @Test
    public void testExactly70PercentPositive() {
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < 65; i++) {
            description.append("good ");
        }
        for (int i = 0; i < 35; i++) {
            description.append("bad ");
        }

        Article article = createArticle(description.toString());
        String result = SentimentAnalysis.analyzeSentiment(List.of(article));
        assertEquals(":-|", result);
    }

    /**
     * Testing multiple articles with mixed sentiments.
     */
    @Test
    public void testMultipleArticlesMixed() {
        Article positive = createArticle("wonderful excellent fantastic amazing");
        Article negative = createArticle("terrible awful horrible");
        Article neutral = createArticle("good bad");

        List<Article> articles = Arrays.asList(positive, negative, neutral);
        String result = SentimentAnalysis.analyzeSentiment(articles);
        assertNotNull(result);
        assertTrue(result.equals(":-)") || result.equals(":-(") || result.equals(":-|"));
    }

    /**
     * Testing numberOfNegAndPosWords method directly.
     */
    @Test
    public void testNumberOfNegAndPosWords() {
        Article article = createArticle("good bad excellent terrible");
        SentimentTuple result = SentimentAnalysis.numberOfNegAndPosWords(article);

        assertEquals(2, result.getPositives());
        assertEquals(2, result.getNegatives());
    }



    /**
     * Testing that numbers and special characters are ignored.
     */
    @Test
    public void testNumbersAndSpecialChars() {
        Article article = createArticle("wonderful 123 @@@ excellent $$$ amazing");
        List<Article> articles = List.of(article);

        String result = SentimentAnalysis.analyzeSentiment(articles);
        assertEquals(":-)", result);
    }

    /**
     * Testing article with empty description but valid title/content.
     */
    @Test
    public void testEmptyDescription() {
        Article article = createArticle("");
        String result = SentimentAnalysis.analyzeSentiment(List.of(article));
        assertEquals(":-|", result);
    }

    /**
     * Testing very long article.
     */
    @Test
    public void testVeryLongArticle() {
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            description.append("wonderful ");
        }
        Article article = createArticle(description.toString());
        String result = SentimentAnalysis.analyzeSentiment(List.of(article));
        assertEquals(":-)", result);
    }


    /**
     * Helper method for creating test articles.
     * @param description
     * @return dummy article with given description
     */
    private Article createArticle(String description) {
        return new Article(null,null, null, null, null, description, null, null);
    }


}
