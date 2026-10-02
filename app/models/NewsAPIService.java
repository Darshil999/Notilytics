package models;

import play.libs.ws.*;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.*;

/**
 * NewsAPIService
 * --------------
 * Service class for interacting with the NewsAPI (newsapi.org).
 * Handles HTTP requests to fetch news articles based on search queries.
 *
 * Key Responsibilities:
 *  • Make asynchronous HTTP requests to NewsAPI using WSClient
 *  • Parse JSON responses and convert to Article objects
 *  • Extract source information and construct source URLs
 *  • Calculate readability scores for article descriptions
 *  • Compute word-level statistics using Java 8 Streams API
 *  • Handle API errors gracefully
 *
 */
@Singleton
public class NewsAPIService {

    private final WSClient ws;
    private final String apiKey = "0eb7128c8d5c41d4a367f4dc22da97bb";
    private final String baseUrl = "https://newsapi.org/v2/everything";
    private final String sourcesUrl = "https://newsapi.org/v2/top-headlines/sources";
    private final String topHeadlinesUrl = "https://newsapi.org/v2/top-headlines?";

    /**
     * Constructor with dependency injection for WSClient
     *
     * @param ws WebService client for making HTTP requests
     */
    @Inject
    public NewsAPIService(WSClient ws) {
        this.ws = ws;
    }

    /**
     * Search for news articles using NewsAPI
     * Makes an asynchronous HTTP GET request and returns a CompletionStage
     *
     * @param query Search keywords
     * @param sortBy Sorting method (publishedAt, relevancy, popularity)
     * @return CompletionStage of QueryResult containing articles
     */
    public CompletionStage<SearchQuery> search(String query, String sortBy) {
        String url = baseUrl + "?q=" + query + "&sortBy=" + sortBy + "&pageSize=10&apiKey=" + apiKey;

        return ws.url(url)
                .get()
                .thenApply(response -> {
                    JsonNode json = response.asJson();
                    String status = json.get("status").asText();

                    if (status.equals("ok")) {
                        int totalResults = json.get("totalResults").asInt();
                        List<Article> articles = new ArrayList<>();

                        JsonNode articlesNode = json.get("articles");
                        if (articlesNode != null && articlesNode.isArray()) {
                            for (JsonNode articleNode : articlesNode) {
                                // Extract source information
                                JsonNode sourceNode = articleNode.get("source");
                                String sourceName = sourceNode.get("name").asText("No source");
                                String sourceId = sourceNode.findPath("id").asText(null);

                                // Get the actual article URL to extract domain
                                String articleUrl = articleNode.get("url").asText("#");
                                
                                // Construct source URL - use article URL's domain if available
                                String sourceUrl = constructSourceUrlFromArticle(sourceId, sourceName, articleUrl);

                                // Extract article information
                                String author = articleNode.get("author").asText("Unknown");
                                String title = articleNode.get("title").asText("No title");
                                String description = articleNode.get("description").asText("No description");
                                String publishedAt = articleNode.get("publishedAt").asText("");

                                double fleschKincaidGrade = utils.ReadabilityCalculator.calculateFleschKincaidGrade(description);
                                double fleschReadingEase = utils.ReadabilityCalculator.calculateFleschReadingEase(description);

                                articles.add(new Article(sourceName, sourceId, sourceUrl, author, title, description, articleUrl, publishedAt, fleschKincaidGrade, fleschReadingEase));
                            }
                        }
                        String sentiment = utils.SentimentAnalysis.analyzeSentiment(articles);
                        return new SearchQuery(status, query, sortBy, articles, totalResults, sentiment);
                    } else {
                        return new SearchQuery("error");
                    }
                });
    }

    /**
     * Compute word-level statistics for a search query.
     * Fetches up to 50 articles and analyzes word frequency in descriptions
     * using Java 8 Streams API.
     *
     * Returns the top 50 most frequent words in article descriptions.
     *
     * @param query Search keywords
     * @return CompletionStage of Map containing word frequencies (word -> count)
     */
    public CompletionStage<Map<String, Long>> getWordStats(String query) {
        String url = baseUrl + "?q=" + query + "&apiKey=" + apiKey +
                "&pageSize=50&sortBy=publishedAt";

        return ws.url(url).get().thenApply(response -> {
            if (response.getStatus() != 200)
            {
                return Collections.emptyMap();
            }

            JsonNode json = response.asJson();
            JsonNode articlesNode = json.findPath("articles");

            // --- Asynchronous Loops
            List<String> allWords = StreamSupport.stream(articlesNode.spliterator(), true) // true = parallel
                    .map(item -> item.findPath("description").asText("").toLowerCase())
                    .filter(desc -> !desc.isBlank())
                    .flatMap(desc ->
                            Arrays.stream(
                                    desc.trim()
                                            .replaceAll("[^a-z0-9\\s]", " ")
                                            .replaceAll("[^a-z0-9\\s]", " ")
                                            .split("\\s+")
                            )
                    )
                    .filter(word -> !word.isBlank())
                    .toList();

            // Count + sort top 50(For readability purpose)
            Map<String, Long> sortedWordCounts = allWords.stream()
                    .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                    .limit(50)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (a, b) -> a,
                            LinkedHashMap::new
                    ));

            return sortedWordCounts;

        }
        );
    }

    /**
     * List news sources with optional filters
     * 
     */
    public CompletionStage<List<Source>> listSources(Optional<String> category, Optional<String> language, Optional<String> country) {
        StringBuilder urlBuilder = new StringBuilder(sourcesUrl + "?apiKey=" + apiKey);
        category.ifPresent(c -> urlBuilder.append("&category=").append(c));
        language.ifPresent(l -> urlBuilder.append("&language=").append(l));
        country.ifPresent(co -> urlBuilder.append("&country=").append(co));

        return ws.url(urlBuilder.toString()).get().thenApply(resp -> {
            if (resp.getStatus() != 200) return Collections.emptyList();
            JsonNode json = resp.asJson();
            JsonNode sourcesNode = json.findPath("sources");
            List<Source> sources = new ArrayList<>();
            for (JsonNode item : sourcesNode) {
                String id = item.findPath("id").asText(null);
                String name = item.findPath("name").asText("No name");
                String description = item.findPath("description").asText("No description");
                String url = item.findPath("url").asText("#");
                String cat = item.findPath("category").asText("general");
                String lang = item.findPath("language").asText("en");
                String coun = item.findPath("country").asText("us");
                sources.add(new Source(id, name, description, url, cat, lang, coun));
            }
            return sources;
        });
    }

    /**
     * Get source by ID
     * 
     */
    public CompletionStage<Optional<Source>> getSourceById(String sourceId) {
        return listSources(Optional.empty(), Optional.empty(), Optional.empty())
                .thenApply(list -> list.stream().filter(s -> Objects.equals(s.getId(), sourceId)).findFirst());
    }

    /**
     * Fetch latest articles for a source id using Top Headlines endpoint
     * 
     */
    public CompletionStage<List<Article>> getLatestArticlesForSource(String sourceId, int pageSize) {
        String url = topHeadlinesUrl + "sources=" + sourceId + "&apiKey=" + apiKey + "&pageSize=" + pageSize;
        return ws.url(url).get().thenApply(resp -> {
            if (resp.getStatus() != 200) return Collections.<Article>emptyList();
            JsonNode json = resp.asJson();
            JsonNode articlesNode = json.findPath("articles");
            List<Article> articles = new ArrayList<>();
            for (JsonNode item : articlesNode) {
                String title = item.findPath("title").asText("No title");
                String description = item.findPath("description").asText("No description");
                String articleUrl = item.findPath("url").asText("#");
                String datePublished = item.findPath("publishedAt").asText("No date");
                String sourceName = item.findPath("source").findPath("name").asText("No source");
                String author = item.findPath("author").asText("No author");
                String sid = item.findPath("source").findPath("id").asText((String) null);
                
                double fleschKincaidGrade = utils.ReadabilityCalculator.calculateFleschKincaidGrade(description);
                double fleschReadingEase = utils.ReadabilityCalculator.calculateFleschReadingEase(description);
                
                // Extract domain from article URL for source URL
                String sourceUrl = extractDomainFromUrl(articleUrl);
                if (sourceUrl == null) {
                    sourceUrl = constructSourceUrl(sid, sourceName);
                }
                
                articles.add(new Article(sourceName, sid, sourceUrl, author, title, description, articleUrl, datePublished, fleschKincaidGrade, fleschReadingEase));
            }
            return articles;
        });
    }

    /**
     * Get articles by source name or ID
     * Searches for articles from a specific source using multiple strategies
     * 
     * @param sourceNameOrId Either the source ID or source name
     * @param pageSize Number of articles to fetch
     * @return CompletionStage of List of Articles
     */
    public CompletionStage<List<Article>> getArticlesBySourceNameOrId(String sourceNameOrId, int pageSize) {
        // First try as sourceId with top-headlines
        CompletionStage<List<Article>> topHeadlinesAttempt = getLatestArticlesForSource(sourceNameOrId, pageSize);
        
        // If that returns empty, search by source name in everything endpoint
        return topHeadlinesAttempt.thenCompose(articles -> {
            if (!articles.isEmpty()) {
                return CompletableFuture.completedFuture(articles);
            }
            
            // Search using source name in query
            String url = baseUrl + "?q=" + sourceNameOrId + "&apiKey=" + apiKey + "&pageSize=" + pageSize + "&sortBy=publishedAt";
            return ws.url(url).get().thenApply(resp -> {
                if (resp.getStatus() != 200) return Collections.<Article>emptyList();
                JsonNode json = resp.asJson();
                JsonNode articlesNode = json.findPath("articles");
                List<Article> resultArticles = new ArrayList<>();
                
                for (JsonNode item : articlesNode) {
                    String title = item.findPath("title").asText("No title");
                    String description = item.findPath("description").asText("No description");
                    String articleUrl = item.findPath("url").asText("#");
                    String datePublished = item.findPath("publishedAt").asText("No date");
                    String sourceName = item.findPath("source").findPath("name").asText("No source");
                    String author = item.findPath("author").asText("No author");
                    String sid = item.findPath("source").findPath("id").asText((String) null);
                    
                    double fleschKincaidGrade = utils.ReadabilityCalculator.calculateFleschKincaidGrade(description);
                    double fleschReadingEase = utils.ReadabilityCalculator.calculateFleschReadingEase(description);
                    
                    String sourceUrl = extractDomainFromUrl(articleUrl);
                    if (sourceUrl == null) {
                        sourceUrl = constructSourceUrl(sid, sourceName);
                    }
                    
                    resultArticles.add(new Article(sourceName, sid, sourceUrl, author, title, description, articleUrl, datePublished, fleschKincaidGrade, fleschReadingEase));
                }
                return resultArticles;
            });
        });
    }

    /**
     * Constructs a source URL from article URL, source ID, or source name
     * Priority: 1. Extract domain from article URL, 2. Use sourceId, 3. Fallback to sourceName
     *
     * @param sourceId The source identifier from NewsAPI
     * @param sourceName The source display name
     * @param articleUrl The article URL to extract domain from
     * @return Constructed source URL
     */
    private String constructSourceUrlFromArticle(String sourceId, String sourceName, String articleUrl) {
        // Try to extract domain from article URL (most reliable)
        if (articleUrl != null && !articleUrl.equals("#")) {
            String domain = extractDomainFromUrl(articleUrl);
            if (domain != null) {
                return domain;
            }
        }
        
        // Fallback to old logic
        return constructSourceUrl(sourceId, sourceName);
    }

    /**
     * Extracts the domain (protocol + host) from a full URL
     * Example: https://www.bbc.com/news/article123 -> https://www.bbc.com
     * 
     * @param url Full article URL
     * @return Domain URL or null if invalid
     */
    private String extractDomainFromUrl(String url) {
        try {
            if (url == null || url.isEmpty() || url.equals("#")) {
                return null;
            }
            
            // Parse URL to extract protocol and host
            java.net.URL parsedUrl = new java.net.URL(url);
            return parsedUrl.getProtocol() + "://" + parsedUrl.getHost();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Constructs a source URL from source ID or source name
     * Fallback method when article URL is not available
     *
     * @param sourceId The source identifier from NewsAPI
     * @param sourceName The source display name
     * @return Constructed source URL or "#" if unavailable
     */
    private String constructSourceUrl(String sourceId, String sourceName) {
        // Prefer sourceId as it's more reliable
        if (sourceId != null && !sourceId.isEmpty()) {
            return "https://" + sourceId + ".com";
        }

        // Fallback to sourceName
        if (sourceName == null || sourceName.equals("No source")) {
            return "#";
        }

        // Clean source name: remove spaces and special characters
        String cleanName = sourceName.toLowerCase()
                .replaceAll("[^a-z0-9]", "");

        return "https://" + cleanName + ".com";
    }
}

