package models;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for Article model, focusing on readability-related functionality.
 * Tests all readability score methods.
 *
 */
public class ArticleTest {

    /**
     * Test getReadabilityLevel() for "Very Easy" range (90-100).
     */
    @Test
    public void testGetReadabilityLevel_VeryEasy() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 5.0, 95.0);
        
        assertEquals("Very Easy", article.getReadabilityLevel());
    }

    /**
     * Test getReadabilityLevel() for "Easy" range (80-89).
     */
    @Test
    public void testGetReadabilityLevel_Easy() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 6.0, 85.0);
        
        assertEquals("Easy", article.getReadabilityLevel());
    }

    /**
     * Test getReadabilityLevel() for "Fairly Easy" range (70-79).
     */
    @Test
    public void testGetReadabilityLevel_FairlyEasy() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 7.0, 75.0);
        
        assertEquals("Fairly Easy", article.getReadabilityLevel());
    }

    /**
     * Test getReadabilityLevel() for "Standard" range (60-69).
     */
    @Test
    public void testGetReadabilityLevel_Standard() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 9.0, 65.0);
        
        assertEquals("Standard", article.getReadabilityLevel());
    }

    /**
     * Test getReadabilityLevel() for "Fairly Difficult" range (50-59).
     */
    @Test
    public void testGetReadabilityLevel_FairlyDifficult() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 11.0, 55.0);
        
        assertEquals("Fairly Difficult", article.getReadabilityLevel());
    }

    /**
     * Test getReadabilityLevel() for "Difficult" range (30-49).
     */
    @Test
    public void testGetReadabilityLevel_Difficult() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 14.0, 40.0);
        
        assertEquals("Difficult", article.getReadabilityLevel());
    }

    /**
     * Test getReadabilityLevel() for "Very Confusing" range (0-29).
     */
    @Test
    public void testGetReadabilityLevel_VeryConfusing() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 18.0, 20.0);
        
        assertEquals("Very Confusing", article.getReadabilityLevel());
    }

    /**
     * Test getReadabilityLevel() at boundary: exactly 90.
     */
    @Test
    public void testGetReadabilityLevel_Boundary90() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 5.0, 90.0);
        
        assertEquals("Very Easy", article.getReadabilityLevel());
    }

    /**
     * Test getReadabilityLevel() at boundary: exactly 80.
     */
    @Test
    public void testGetReadabilityLevel_Boundary80() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 6.0, 80.0);
        
        assertEquals("Easy", article.getReadabilityLevel());
    }

    /**
     * Test getReadabilityLevel() at boundary: exactly 70.
     */
    @Test
    public void testGetReadabilityLevel_Boundary70() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 7.0, 70.0);
        
        assertEquals("Fairly Easy", article.getReadabilityLevel());
    }

    /**
     * Test getReadabilityLevel() at boundary: exactly 60.
     */
    @Test
    public void testGetReadabilityLevel_Boundary60() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 9.0, 60.0);
        
        assertEquals("Standard", article.getReadabilityLevel());
    }

    /**
     * Test getReadabilityLevel() at boundary: exactly 50.
     */
    @Test
    public void testGetReadabilityLevel_Boundary50() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 11.0, 50.0);
        
        assertEquals("Fairly Difficult", article.getReadabilityLevel());
    }

    /**
     * Test getReadabilityLevel() at boundary: exactly 30.
     */
    @Test
    public void testGetReadabilityLevel_Boundary30() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 14.0, 30.0);
        
        assertEquals("Difficult", article.getReadabilityLevel());
    }

    /**
     * Test getFleschKincaidGrade() getter.
     */
    @Test
    public void testGetFleschKincaidGrade() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 12.5, 65.0);
        
        assertEquals(12.5, article.getFleschKincaidGrade(), 0.001);
    }

    /**
     * Test getFleschReadingEase() getter.
     */
    @Test
    public void testGetFleschReadingEase() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 12.5, 65.0);
        
        assertEquals(65.0, article.getFleschReadingEase(), 0.001);
    }

    /**
     * Test getFleschKincaidGradeFormatted() with positive value.
     */
    @Test
    public void testGetFleschKincaidGradeFormatted_Positive() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 12.3456, 65.0);
        
        assertEquals("12.35", article.getFleschKincaidGradeFormatted());
    }

    /**
     * Test getFleschKincaidGradeFormatted() with negative value.
     */
    @Test
    public void testGetFleschKincaidGradeFormatted_Negative() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", -2.567, 95.0);
        
        assertEquals("-2.57", article.getFleschKincaidGradeFormatted());
    }

    /**
     * Test getFleschKincaidGradeFormatted() with zero.
     */
    @Test
    public void testGetFleschKincaidGradeFormatted_Zero() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 0.0, 0.0);
        
        assertEquals("0.00", article.getFleschKincaidGradeFormatted());
    }

    /**
     * Test getFleschReadingEaseFormatted() with positive value.
     */
    @Test
    public void testGetFleschReadingEaseFormatted_Positive() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 12.0, 67.8901);
        
        assertEquals("67.89", article.getFleschReadingEaseFormatted());
    }

    /**
     * Test getFleschReadingEaseFormatted() with zero.
     */
    @Test
    public void testGetFleschReadingEaseFormatted_Zero() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 0.0, 0.0);
        
        assertEquals("0.00", article.getFleschReadingEaseFormatted());
    }

    /**
     * Test setFleschKincaidGrade() setter.
     */
    @Test
    public void testSetFleschKincaidGrade() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 0.0, 0.0);
        
        article.setFleschKincaidGrade(15.5);
        assertEquals(15.5, article.getFleschKincaidGrade(), 0.001);
    }

    /**
     * Test setFleschReadingEase() setter.
     */
    @Test
    public void testSetFleschReadingEase() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 0.0, 0.0);
        
        article.setFleschReadingEase(72.3);
        assertEquals(72.3, article.getFleschReadingEase(), 0.001);
    }

    /**
     * Test hasReadabilityScores() when both scores are zero.
     */
    @Test
    public void testHasReadabilityScores_BothZero() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 0.0, 0.0);
        
        assertFalse("Should return false when both scores are zero", 
                    article.hasReadabilityScores());
    }

    /**
     * Test hasReadabilityScores() when grade is non-zero.
     */
    @Test
    public void testHasReadabilityScores_GradeNonZero() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 12.5, 0.0);
        
        assertTrue("Should return true when grade is non-zero", 
                   article.hasReadabilityScores());
    }

    /**
     * Test hasReadabilityScores() when ease is non-zero.
     */
    @Test
    public void testHasReadabilityScores_EaseNonZero() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 0.0, 65.5);
        
        assertTrue("Should return true when ease is non-zero", 
                   article.hasReadabilityScores());
    }

    /**
     * Test hasReadabilityScores() when both scores are non-zero.
     */
    @Test
    public void testHasReadabilityScores_BothNonZero() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 12.5, 65.5);
        
        assertTrue("Should return true when both scores are non-zero", 
                   article.hasReadabilityScores());
    }

    /**
     * Test constructor without readability scores (default values).
     */
    @Test
    public void testConstructorWithoutReadabilityScores() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z");
        
        assertEquals(0.0, article.getFleschKincaidGrade(), 0.001);
        assertEquals(0.0, article.getFleschReadingEase(), 0.001);
        assertFalse("Should return false for default scores", 
                    article.hasReadabilityScores());
    }

    /**
     * Test constructor with readability scores.
     */
    @Test
    public void testConstructorWithReadabilityScores() {
        Article article = new Article("Test Source", "test-id", "https://test.com",
                "Author", "Title", "Description", "https://article.com",
                "2025-11-07T10:00:00Z", 10.5, 70.3);
        
        assertEquals(10.5, article.getFleschKincaidGrade(), 0.001);
        assertEquals(70.3, article.getFleschReadingEase(), 0.001);
        assertTrue("Should return true for non-zero scores", 
                   article.hasReadabilityScores());
    }
}
