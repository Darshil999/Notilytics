package models;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for SearchQuery model, focusing on readability average calculation.
 * Tests the Java 8 Streams API usage for calculating average readability scores
 * as required by Individual Task (e).
 *
 * @author Wei Huang
 */
public class SearchQueryTest {

    /**
     * Test calculateAverageFleschKincaidGrade with multiple articles having valid scores.
     * @author Wei Huang
     */
    @Test
    public void testCalculateAverageFleschKincaidGrade_MultipleArticles() {
        List<Article> articles = Arrays.asList(
                new Article("Source1", "id1", "https://source1.com", "Author1", 
                           "Title1", "Description1", "https://article1.com", 
                           "2025-11-07T10:00:00Z", 10.0, 70.0),
                new Article("Source2", "id2", "https://source2.com", "Author2", 
                           "Title2", "Description2", "https://article2.com", 
                           "2025-11-07T10:00:00Z", 12.0, 65.0),
                new Article("Source3", "id3", "https://source3.com", "Author3", 
                           "Title3", "Description3", "https://article3.com", 
                           "2025-11-07T10:00:00Z", 14.0, 60.0)
        );

        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", articles, 3, ":-|");
        
        assertEquals(12.0, query.getAverageFleschKincaidGrade(), 0.001);
    }

    /**
     * Test calculateAverageFleschReadingEase with multiple articles having valid scores.
     * @author Wei Huang
     */
    @Test
    public void testCalculateAverageFleschReadingEase_MultipleArticles() {
        List<Article> articles = Arrays.asList(
                new Article("Source1", "id1", "https://source1.com", "Author1", 
                           "Title1", "Description1", "https://article1.com", 
                           "2025-11-07T10:00:00Z", 10.0, 70.0),
                new Article("Source2", "id2", "https://source2.com", "Author2", 
                           "Title2", "Description2", "https://article2.com", 
                           "2025-11-07T10:00:00Z", 12.0, 65.0),
                new Article("Source3", "id3", "https://source3.com", "Author3", 
                           "Title3", "Description3", "https://article3.com", 
                           "2025-11-07T10:00:00Z", 14.0, 60.0)
        );

        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", articles, 3, ":-|");
        
        assertEquals(65.0, query.getAverageFleschReadingEase(), 0.001);
    }

    /**
     * Test with empty article list.
     * @author Wei Huang
     */
    @Test
    public void testCalculateAverage_EmptyList() {
        List<Article> articles = new ArrayList<>();
        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", articles, 0, ":-|");
        
        assertEquals(0.0, query.getAverageFleschKincaidGrade(), 0.001);
        assertEquals(0.0, query.getAverageFleschReadingEase(), 0.001);
    }

    /**
     * Test with null article list.
     * @author Wei Huang
     */
    @Test
    public void testCalculateAverage_NullList() {
        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", null, 0, ":-|");
        
        assertEquals(0.0, query.getAverageFleschKincaidGrade(), 0.001);
        assertEquals(0.0, query.getAverageFleschReadingEase(), 0.001);
    }

    /**
     * Test with articles having zero readability scores (should be filtered out).
     * Tests the filter() method in the Streams API pipeline.
     * @author Wei Huang
     */
    @Test
    public void testCalculateAverage_ArticlesWithZeroScores() {
        List<Article> articles = Arrays.asList(
                new Article("Source1", "id1", "https://source1.com", "Author1", 
                           "Title1", "Description1", "https://article1.com", 
                           "2025-11-07T10:00:00Z", 0.0, 0.0),
                new Article("Source2", "id2", "https://source2.com", "Author2", 
                           "Title2", "Description2", "https://article2.com", 
                           "2025-11-07T10:00:00Z", 0.0, 0.0)
        );

        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", articles, 2, ":-|");
        
        assertEquals(0.0, query.getAverageFleschKincaidGrade(), 0.001);
        assertEquals(0.0, query.getAverageFleschReadingEase(), 0.001);
    }

    /**
     * Test with mix of valid and zero scores.
     * Tests that filter() correctly excludes articles without scores.
     * @author Wei Huang
     */
    @Test
    public void testCalculateAverage_MixedScores() {
        List<Article> articles = Arrays.asList(
                new Article("Source1", "id1", "https://source1.com", "Author1", 
                           "Title1", "Description1", "https://article1.com", 
                           "2025-11-07T10:00:00Z", 10.0, 80.0),
                new Article("Source2", "id2", "https://source2.com", "Author2", 
                           "Title2", "Description2", "https://article2.com", 
                           "2025-11-07T10:00:00Z", 0.0, 0.0),
                new Article("Source3", "id3", "https://source3.com", "Author3", 
                           "Title3", "Description3", "https://article3.com", 
                           "2025-11-07T10:00:00Z", 20.0, 60.0)
        );

        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", articles, 3, ":-|");
        
        assertEquals(15.0, query.getAverageFleschKincaidGrade(), 0.001);
        assertEquals(70.0, query.getAverageFleschReadingEase(), 0.001);
    }

    /**
     * Test with single article.
     * @author Wei Huang
     */
    @Test
    public void testCalculateAverage_SingleArticle() {
        List<Article> articles = Arrays.asList(
                new Article("Source1", "id1", "https://source1.com", "Author1", 
                           "Title1", "Description1", "https://article1.com", 
                           "2025-11-07T10:00:00Z", 13.5, 62.8)
        );

        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", articles, 1, ":-|");
        
        assertEquals(13.5, query.getAverageFleschKincaidGrade(), 0.001);
        assertEquals(62.8, query.getAverageFleschReadingEase(), 0.001);
    }

    /**
     * Test with negative readability scores (valid case for very simple text).
     * @author Wei Huang
     */
    @Test
    public void testCalculateAverage_NegativeScores() {
        List<Article> articles = Arrays.asList(
                new Article("Source1", "id1", "https://source1.com", "Author1", 
                           "Title1", "Description1", "https://article1.com", 
                           "2025-11-07T10:00:00Z", -2.0, 95.0),
                new Article("Source2", "id2", "https://source2.com", "Author2", 
                           "Title2", "Description2", "https://article2.com", 
                           "2025-11-07T10:00:00Z", -4.0, 98.0)
        );

        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", articles, 2, ":-|");
        
        assertEquals(-3.0, query.getAverageFleschKincaidGrade(), 0.001);
        assertEquals(96.5, query.getAverageFleschReadingEase(), 0.001);
    }

    /**
     * Test with large number of articles (simulating 50 articles from assignment).
     * @author Wei Huang
     */
    @Test
    public void testCalculateAverage_LargeNumberOfArticles() {
        List<Article> articles = new ArrayList<>();
        
        for (int i = 1; i <= 50; i++) {
            articles.add(new Article(
                "Source" + i, "id" + i, "https://source" + i + ".com",
                "Author" + i, "Title" + i, "Description" + i,
                "https://article" + i + ".com", "2025-11-07T10:00:00Z",
                (double) i, 100.0 - i
            ));
        }

        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", articles, 50, ":-|");
        
        assertEquals(25.5, query.getAverageFleschKincaidGrade(), 0.001);
        assertEquals(74.5, query.getAverageFleschReadingEase(), 0.001);
    }

    /**
     * Test getAverageFleschKincaidGradeFormatted().
     * @author Wei Huang
     */
    @Test
    public void testGetAverageFleschKincaidGradeFormatted() {
        List<Article> articles = Arrays.asList(
                new Article("Source1", "id1", "https://source1.com", "Author1", 
                           "Title1", "Description1", "https://article1.com", 
                           "2025-11-07T10:00:00Z", 12.3456, 70.0)
        );

        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", articles, 1, ":-|");
        
        assertEquals("12.35", query.getAverageFleschKincaidGradeFormatted());
    }

    /**
     * Test getAverageFleschReadingEaseFormatted().
     * @author Wei Huang
     */
    @Test
    public void testGetAverageFleschReadingEaseFormatted() {
        List<Article> articles = Arrays.asList(
                new Article("Source1", "id1", "https://source1.com", "Author1", 
                           "Title1", "Description1", "https://article1.com", 
                           "2025-11-07T10:00:00Z", 12.0, 67.8901)
        );

        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", articles, 1, ":-|");
        
        assertEquals("67.89", query.getAverageFleschReadingEaseFormatted());
    }

    /**
     * Test formatted output with zero values.
     * @author Wei Huang
     */
    @Test
    public void testGetFormattedScores_Zero() {
        List<Article> articles = new ArrayList<>();
        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", articles, 0, ":-|");
        
        assertEquals("0.00", query.getAverageFleschKincaidGradeFormatted());
        assertEquals("0.00", query.getAverageFleschReadingEaseFormatted());
    }

    /**
     * Test formatted output with negative values.
     * @author Wei Huang
     */
    @Test
    public void testGetFormattedScores_Negative() {
        List<Article> articles = Arrays.asList(
                new Article("Source1", "id1", "https://source1.com", "Author1", 
                           "Title1", "Description1", "https://article1.com", 
                           "2025-11-07T10:00:00Z", -3.456, 95.0)
        );

        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", articles, 1, ":-|");
        
        assertEquals("-3.46", query.getAverageFleschKincaidGradeFormatted());
    }

    /**
     * Test basic getter methods.
     * @author Wei Huang
     */
    @Test
    public void testGetters() {
        List<Article> articles = Arrays.asList(
                new Article("Source1", "id1", "https://source1.com", "Author1", 
                           "Title1", "Description1", "https://article1.com", 
                           "2025-11-07T10:00:00Z", 10.0, 70.0)
        );

        SearchQuery query = new SearchQuery("ok", "climate change", "relevancy", articles, 100, ":-|");
        
        assertEquals("climate change", query.getSearchTerm());
        assertEquals("relevancy", query.getSortBy());
        assertEquals(1, query.getArticles().size());
        assertEquals(100, query.getTotalResults());
    }

    /**
     * Test with articles having only non-zero grade (ease is zero).
     * @author Wei Huang
     */
    @Test
    public void testCalculateAverage_OnlyGradeNonZero() {
        List<Article> articles = Arrays.asList(
                new Article("Source1", "id1", "https://source1.com", "Author1", 
                           "Title1", "Description1", "https://article1.com", 
                           "2025-11-07T10:00:00Z", 15.0, 0.0)
        );

        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", articles, 1, ":-|");
        
        assertEquals(15.0, query.getAverageFleschKincaidGrade(), 0.001);
        assertEquals(0.0, query.getAverageFleschReadingEase(), 0.001);
    }

    /**
     * Test with articles having only non-zero ease (grade is zero).
     * @author Wei Huang
     */
    @Test
    public void testCalculateAverage_OnlyEaseNonZero() {
        List<Article> articles = Arrays.asList(
                new Article("Source1", "id1", "https://source1.com", "Author1", 
                           "Title1", "Description1", "https://article1.com", 
                           "2025-11-07T10:00:00Z", 0.0, 75.0)
        );

        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", articles, 1, ":-|");
        
        assertEquals(0.0, query.getAverageFleschKincaidGrade(), 0.001);
        assertEquals(75.0, query.getAverageFleschReadingEase(), 0.001);
    }

    /**
     * Test precision with decimal values.
     * @author Wei Huang
     */
    @Test
    public void testCalculateAverage_DecimalPrecision() {
        List<Article> articles = Arrays.asList(
                new Article("Source1", "id1", "https://source1.com", "Author1", 
                           "Title1", "Description1", "https://article1.com", 
                           "2025-11-07T10:00:00Z", 12.333, 65.666),
                new Article("Source2", "id2", "https://source2.com", "Author2", 
                           "Title2", "Description2", "https://article2.com", 
                           "2025-11-07T10:00:00Z", 13.667, 66.334)
        );

        SearchQuery query = new SearchQuery("ok", "test", "publishedAt", articles, 2, ":-|");
        
        assertEquals(13.0, query.getAverageFleschKincaidGrade(), 0.001);
        assertEquals(66.0, query.getAverageFleschReadingEase(), 0.001);
    }
}