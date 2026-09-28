package models;

import java.util.List;

/**
 * SearchQuery - Represents a single search query with its results
 *
 * This class encapsulates all information about one search operation:
 * - The search term used
 * - The sorting method applied
 * - The list of articles returned (limited to 10)
 * - The total number of results available
 *
 * Used to maintain search history where each search is displayed separately.
 *
 * @author Wei Huang
 */
public class SearchQuery {
    private String status;
    private String searchTerm;
    private String sortBy;
    private List<Article> articles;
    private int totalResults;
    private final String sentiment;

    // Readability metrics - Added by Wei Huang for Individual Part (e)
    private double averageFleschKincaidGrade;
    private double averageFleschReadingEase;

    /**
     * Constructor for SearchQuery
     *
     * @param searchTerm The search term/keywords used
     * @param sortBy The sorting method (publishedAt, relevancy, popularity)
     * @param articles List of articles (limited to 10)
     * @param totalResults Total number of results available for this search
     * @author Wei Huang
     */
    public SearchQuery(String status, String searchTerm, String sortBy, List<Article> articles, int totalResults, String sentiment) {
        this.status = status;
        this.searchTerm = searchTerm;
        this.sortBy = sortBy;
        this.articles = articles;
        this.totalResults = totalResults;
        this.sentiment = sentiment;

        // Calculate average readability scores using Java 8 Streams API - Added by Wei Huang
        this.averageFleschKincaidGrade = calculateAverageFleschKincaidGrade();
        this.averageFleschReadingEase = calculateAverageFleschReadingEase();
    }

    /**
     * Constructor for SearchQuery when there is an error, or no search yet.
     * @param status
     * @author Finn Kleckner
     */
    public SearchQuery(String status) {
        this.status = status;
        this.searchTerm = "";
        this.sortBy = "";
        this.articles = List.of();
        this.totalResults = 0;
        this.sentiment = "";

        // Calculate average readability scores using Java 8 Streams API - Added by Wei Huang
        this.averageFleschKincaidGrade = 0.0;
        this.averageFleschReadingEase = 0.0;
    }
    public String getStatus() {return status;}
    public String getSearchTerm() {
        return searchTerm;
    }

    public String getSortBy() {
        return sortBy;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public String getSentiment() {return sentiment;}

    /**
     * Gets the average Flesch-Kincaid Grade Level across all articles.
     *
     * @return Average grade level score
     * @author Wei Huang
     */
    public double getAverageFleschKincaidGrade() {
        return averageFleschKincaidGrade;
    }

    /**
     * Gets the formatted average Flesch-Kincaid Grade Level (2 decimal places).
     *
     * @return Formatted average grade level
     * @author Wei Huang
     */
    public String getAverageFleschKincaidGradeFormatted() {
        return String.format("%.2f", averageFleschKincaidGrade);
    }

    /**
     * Gets the average Flesch Reading Ease Score across all articles.
     *
     * @return Average reading ease score
     * @author Wei Huang
     */
    public double getAverageFleschReadingEase() {
        return averageFleschReadingEase;
    }

    /**
     * Gets the formatted average Flesch Reading Ease Score (2 decimal places).
     *
     * @return Formatted average reading ease score
     * @author Wei Huang
     */
    public String getAverageFleschReadingEaseFormatted() {
        return String.format("%.2f", averageFleschReadingEase);
    }

    /**
     * Calculates the average Flesch-Kincaid Grade Level using Java 8 Streams API.
     *
     * This method demonstrates the use of Streams API as required by the assignment.
     * It filters articles with valid scores, maps to their grade values, and calculates average.
     *
     * @return Average grade level, or 0.0 if no valid scores
     * @author Wei Huang
     */
    private double calculateAverageFleschKincaidGrade() {
        if (articles == null || articles.isEmpty()) {
            return 0.0;
        }

        // Use Java 8 Streams API to calculate average - Required by assignment
        return articles.stream()
                .filter(article -> article.hasReadabilityScores())  // Only articles with scores
                .mapToDouble(Article::getFleschKincaidGrade)        // Extract grade scores
                .average()                                           // Calculate average
                .orElse(0.0);                                       // Default to 0.0 if empty
    }

    /**
     * Calculates the average Flesch Reading Ease Score using Java 8 Streams API.
     *
     * This method demonstrates the use of Streams API as required by the assignment.
     * It filters articles with valid scores, maps to their ease values, and calculates average.
     *
     * @return Average reading ease score, or 0.0 if no valid scores
     * @author Wei Huang
     */
    private double calculateAverageFleschReadingEase() {
        if (articles == null || articles.isEmpty()) {
            return 0.0;
        }

        // Use Java 8 Streams API to calculate average - Required by assignment
        return articles.stream()
                .filter(article -> article.hasReadabilityScores())  // Only articles with scores
                .mapToDouble(Article::getFleschReadingEase)         // Extract ease scores
                .average()                                           // Calculate average
                .orElse(0.0);                                       // Default to 0.0 if empty
    }
}