package models;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

/**
 * Article model
 * Enhanced to include source URL for hyperlinks and EDT time conversion
 *
 */
public class Article {
    private String sourceName;
    private String sourceId;
    private String sourceUrl;
    private String author;
    private String title;
    private String description;
    private String url;
    private String publishedAt;

    private double fleschKincaidGrade;    // Complexity score (grade level required to understand)
    private double fleschReadingEase;     // Ease of reading score (0-100, higher = easier)

    /**
     * Constructor with all fields including source URL
     *
     * @param sourceName Name of the news source
     * @param sourceId ID of the news source
     * @param sourceUrl URL of the news source website
     * @param author Article author
     * @param title Article title
     * @param description Article description
     * @param url URL to the article
     * @param publishedAt Publication date in ISO format
     */
    public Article(String sourceName, String sourceId, String sourceUrl, String author,
                   String title, String description, String url, String publishedAt) {
        this.sourceName = sourceName;
        this.sourceId = sourceId;
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.description = description;
        this.url = url;
        this.author = author;
        this.publishedAt = publishedAt;
        this.fleschKincaidGrade = 0.0;  // Default to 0, will be calculated later
        this.fleschReadingEase = 0.0;   // Default to 0, will be calculated later
    }

    /**
     * Full constructor with all fields including readability metrics.
     *
     * @param sourceName Name of the news source
     * @param sourceId ID of the news source
     * @param sourceUrl URL of the news source website
     * @param author Article author
     * @param title Article title
     * @param description Article description
     * @param url URL to the article
     * @param publishedAt Publication date in ISO format
     * @param fleschKincaidGrade Readability grade level score
     * @param fleschReadingEase Readability ease score
     *
     */
    public Article(String sourceName, String sourceId, String sourceUrl, String author,
                   String title, String description, String url, String publishedAt,
                   double fleschKincaidGrade, double fleschReadingEase) {
        this.sourceName = sourceName;
        this.sourceId = sourceId;
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.description = description;
        this.url = url;
        this.author = author;
        this.publishedAt = publishedAt;
        this.fleschKincaidGrade = fleschKincaidGrade;
        this.fleschReadingEase = fleschReadingEase;
    }

    /**
     * Legacy constructor for backward compatibility
     * Constructs sourceUrl from sourceName if not provided
     *
     */
    public Article(String sourceName, String author, String title, String description, String url, String publishedAt) {
        this.sourceName = sourceName;
        this.sourceId = null;
        this.sourceUrl = generateSourceUrl(sourceName);
        this.title = title;
        this.description = description;
        this.url = url;
        this.author = author;
        this.publishedAt = publishedAt;
        this.fleschKincaidGrade = 0.0;
        this.fleschReadingEase = 0.0;
    }

    /**
     * Generates a source URL based on source name
     * This is a fallback method when API doesn't provide the URL
     *
     * @param sourceName Name of the source
     * @return Generated URL or "#" if cannot generate
     */
    private String generateSourceUrl(String sourceName) {
        if (sourceName == null || sourceName.equals("No source")) {
            return "#";
        }
        String cleanName = sourceName.toLowerCase()
                .replace(" ", "")
                .replace("(", "")
                .replace(")", "");
        return "https://" + cleanName + ".com";
    }

    public String getSourceName() {
        return sourceName;
    }

    /**
     * Gets the source ID
     * @return source ID
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * Gets the source URL for hyperlinking
     * @return source website URL
     */
    public String getSourceUrl() {
        return sourceUrl != null ? sourceUrl : "#";
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Gets the original published date (ISO format from API)
     * @return Published date in ISO format
     */
    public String getPublishedAt() {
        return publishedAt;
    }

    /**
     * Converts the published date to EDT (Eastern Daylight Time) format
     *
     * As per project requirements: "the published date (converted to EDT time)"
     *
     * Takes the ISO 8601 timestamp from the API (e.g., "2025-11-02T22:36:59Z")
     * and converts it to EDT timezone with a readable format.
     *
     * @return Formatted date in EDT timezone, or original string if conversion fails
     */
    public String getPublishedAtEDT() {
        try {
            // Parse ISO 8601 timestamp (e.g., "2025-11-02T22:36:59Z")
            ZonedDateTime utcTime = ZonedDateTime.parse(publishedAt);

            // Convert to EDT timezone (America/New_York)
            ZonedDateTime edtTime = utcTime.withZoneSameInstant(ZoneId.of("America/New_York"));

            // Format as readable string: "2025-11-02 17:36:59 EDT"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
            return edtTime.format(formatter);

        } catch (Exception e) {
            // If parsing fails, return original string
            return publishedAt;
        }
    }

    /**
     * Gets the Flesch-Kincaid Grade Level score for the article description.
     *
     * Interpretation:
     * - Score represents the U.S. grade level needed to understand the text
     * - Example: 8.0 = readable by 8th grader (13-14 years old)
     * - Higher score = more complex text
     *
     * Formula: 0.39 × (words/sentences) + 11.8 × (syllables/words) - 15.59
     *
     * @return Grade level score (typically 0-18+)
     */
    public double getFleschKincaidGrade() {
        return fleschKincaidGrade;
    }

    /**
     * Gets the formatted Flesch-Kincaid Grade Level as a string (2 decimal places).
     *
     * @return Formatted grade level (e.g., "12.34")
     */
    public String getFleschKincaidGradeFormatted() {
        return String.format("%.2f", fleschKincaidGrade);
    }

    /**
     * Gets the Flesch Reading Ease Score for the article description.
     *
     * Interpretation (higher = easier):
     * - 90-100: Very Easy (5th grade level)
     * - 80-89:  Easy (6th grade)
     * - 70-79:  Fairly Easy (7th grade)
     * - 60-69:  Standard (8th-9th grade)
     * - 50-59:  Fairly Difficult (10th-12th grade)
     * - 30-49:  Difficult (College level)
     * - 0-29:   Very Confusing (College graduate level)
     *
     * Formula: 206.835 - 1.015 × (words/sentences) - 84.6 × (syllables/words)
     *
     * @return Reading ease score (typically 0-100)
     */
    public double getFleschReadingEase() {
        return fleschReadingEase;
    }

    /**
     * Gets the formatted Flesch Reading Ease Score as a string (2 decimal places).
     *
     * @return Formatted reading ease score (e.g., "65.78")
     */
    public String getFleschReadingEaseFormatted() {
        return String.format("%.2f", fleschReadingEase);
    }

    /**
     * Gets a human-readable interpretation of the Reading Ease Score.
     *
     * @return Difficulty level description (e.g., "Standard", "Difficult")
     */
    public String getReadabilityLevel() {
        if (fleschReadingEase >= 90) return "Very Easy";
        if (fleschReadingEase >= 80) return "Easy";
        if (fleschReadingEase >= 70) return "Fairly Easy";
        if (fleschReadingEase >= 60) return "Standard";
        if (fleschReadingEase >= 50) return "Fairly Difficult";
        if (fleschReadingEase >= 30) return "Difficult";
        return "Very Confusing";
    }

    /**
     * Sets the Flesch-Kincaid Grade Level score.
     * Used when calculating readability after article creation.
     *
     * @param fleschKincaidGrade Grade level score
     */
    public void setFleschKincaidGrade(double fleschKincaidGrade) {
        this.fleschKincaidGrade = fleschKincaidGrade;
    }

    /**
     * Sets the Flesch Reading Ease Score.
     * Used when calculating readability after article creation.
     *
     * @param fleschReadingEase Reading ease score
     */
    public void setFleschReadingEase(double fleschReadingEase) {
        this.fleschReadingEase = fleschReadingEase;
    }

    /**
     * Checks if the article has valid readability scores.
     *
     * @return true if both scores are non-zero
     */
    public boolean hasReadabilityScores() {
        return fleschKincaidGrade != 0.0 || fleschReadingEase != 0.0;
    }

}
