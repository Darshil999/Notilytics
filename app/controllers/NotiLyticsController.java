package controllers;

import play.mvc.*;
import play.data.*;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import models.*;

/**
 * NotiLyticsController
 * This controller manages the main application logic for the NotiLytics project.
 * It handles user interactions from the web interface, processes form inputs,
 * and communicates with the NewsApiService to fetch and display relevant news articles.
 *
 * Key Responsibilities:
 *  • Render the homepage (index) that displays the search form.
 *  • Handle form submissions (search) from users.
 *  • Retrieve user inputs such as "searchTerm" and "sortBy" using FormFactory.
 *  • Call the NewsApiService to fetch a list of matching news articles.
 *  • Gracefully handle empty input, missing results, or API errors.
 *  • Pass the results or error messages to the index.scala.html view for rendering.
 */
public class NotiLyticsController extends Controller {

    private final FormFactory formFactory;
    private final NewsAPIService newsApiService;
    private final SourcesAPIService sourcesApiService;

    private static final int MAX_SEARCH_QUERIES = 10;
    private static final int ARTICLES_PER_SEARCH = 10;

    /**
     * Thread-safe storage for user search histories.
     * Maps session ID to list of SearchQuery objects.
     */
    private static final ConcurrentHashMap<String, List<SearchQuery>> sessionHistories =
            new ConcurrentHashMap<>();

    @Inject
    public NotiLyticsController(FormFactory formFactory, NewsAPIService newsApiService, SourcesAPIService sourcesApiService) {
        this.formFactory = formFactory;
        this.newsApiService = newsApiService;
        this.sourcesApiService = sourcesApiService;
    }

    /**
     * Renders the homepage with search form and search history
     *
     * @param request the HTTP request
     * @return index.scala.html render
     */
    public Result index(Http.Request request) {
        String sessionId = getOrCreateSessionId(request);
        List<SearchQuery> searchHistory = sessionHistories.getOrDefault(sessionId, new ArrayList<>());

        return ok(views.html.index.render("Welcome to NotiLytics", searchHistory, request));
    }

    /**
     * Search functionality
     *
     * @param request the HTTP request containing searchTerm and sortBy parameters
     * @return CompletionStage of Result with asynchronously rendered page
     */
    public CompletionStage<Result> search(Http.Request request) {
        DynamicForm form = formFactory.form().bindFromRequest(request);
        String searchTerm = form.get("searchTerm");
        String sortBy = form.get("sortBy");

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return CompletableFuture.completedFuture(badRequest(views.html.index.render(
                "Welcome to NotiLytics", List.of(new SearchQuery("ok")),
                request
            )));
        }
        
        String sessionId = getOrCreateSessionId(request);

        CompletionStage<SearchQuery> promiseOfQR = newsApiService.search(searchTerm, sortBy);

        return promiseOfQR.thenApply(searchQuery -> {
            if (searchQuery.getStatus().equals("ok")) {
                List<Article> limitedArticles = processArticlesWithStreams(searchQuery.getArticles());
                SearchQuery newSearch = new SearchQuery(
                        "ok",
                        searchTerm,
                        sortBy,
                        limitedArticles,
                        searchQuery.getTotalResults(),
                        searchQuery.getSentiment()
                );

                List<SearchQuery> searchHistory = sessionHistories.getOrDefault(sessionId, new ArrayList<>());

                List<SearchQuery> updatedHistory = new ArrayList<>();
                updatedHistory.add(newSearch);
                updatedHistory.addAll(searchHistory);

                if (updatedHistory.size() > MAX_SEARCH_QUERIES) {
                    updatedHistory = updatedHistory.stream()
                            .limit(MAX_SEARCH_QUERIES)
                            .collect(Collectors.toList());
                }

                sessionHistories.put(sessionId, updatedHistory);

                Result response = ok(views.html.index.render(
                        "Welcome to NotiLytics",
                        updatedHistory,
                        request
                ));

                response = response.addingToSession(request, "sessionId", sessionId);

                return response;
            } else {
                return internalServerError("Error fetching results: " + searchQuery.getStatus());
            }
        });
    }

    /**
     * Word statistics functionality - computes word frequency using Java 8 Streams
     * 
     * @param query The search query
     * @param request the HTTP request
     * @return CompletionStage of Result with word statistics
     */
    public CompletionStage<Result> wordStats(String query, Http.Request request) {
        return newsApiService.getWordStats(query)
                .thenApply(stats -> 
                    ok(views.html.wordstats.render(
                        "Word Statistics",
                        query,
                        stats,
                        request
                    ))
                )
                .exceptionally(ex -> {
                    System.err.println("Error fetching word stats: " + ex.getMessage());
                    return internalServerError("Error fetching word stats");
                });
    }

    /**
     * Source profile functionality - displays articles from a specific source
     * Works with both sourceId and sourceName for maximum compatibility
     * 
     * @param id The source identifier or name
     * @param request the HTTP request
     * @return CompletionStage of Result with source profile page
     */
    public CompletionStage<Result> sourceProfile(String id, Http.Request request) {
        CompletionStage<Optional<Source>> srcStage = newsApiService.getSourceById(id);
        CompletionStage<List<Article>> artsStage = newsApiService.getArticlesBySourceNameOrId(id, 10);

        return srcStage.thenCombine(artsStage, (optSource, articles) -> {
            if (optSource.isPresent()) {
                return ok(views.html.sourceProfile.render(optSource.get(), articles, request));
            } else if (!articles.isEmpty()) {
                Article firstArticle = articles.get(0);
                Source basicSource = new Source(
                    null,
                    firstArticle.getSourceName(),
                    "Articles from " + firstArticle.getSourceName(),
                    firstArticle.getSourceUrl(),
                    "general",
                    "en",
                    "us"
                );
                return ok(views.html.sourceProfile.render(basicSource, articles, request));
            } else {
                return notFound(views.html.index.render("Source not found: " + id, new ArrayList<>(), request));
            }
        });
    }

    /**
     * Displays available news sources filtered by country, category, or language.
     * This is individual task (c): News Sources
     * 
     * Allows users to view and filter news sources using three criteria:
     * country, category, and language. When filters are applied, it retrieves
     * and displays the sources from the NewsAPI based on the selected filters.
     *
     * @param request the HTTP request
     * @return CompletionStage of Result with sources list
     */
    public CompletionStage<Result> showSources(Http.Request request) {
        String country = request.getQueryString("country");
        String category = request.getQueryString("category");
        String language = request.getQueryString("language");

        return sourcesApiService.getSources(country, category, language)
                .thenApply(sources -> ok(views.html.sources.render(
                        "NotiLytics - News Sources",
                        sources,
                        request
                )));
    }

    /**
     * Gets the session ID from the request or creates a new one
     *
     */
    private String getOrCreateSessionId(Http.Request request) {
        Optional<String> existingSessionId = request.session().get("sessionId");
        if (existingSessionId.isPresent()) {
            return existingSessionId.get();
        }
        return UUID.randomUUID().toString();
    }

    /**
     * Processes articles using Java 8+ Streams API
     *
     */
    private List<Article> processArticlesWithStreams(List<Article> articles) {
        if (articles == null) {
            return new ArrayList<>();
        }

        return articles.stream()
                .limit(ARTICLES_PER_SEARCH)
                .collect(Collectors.toList());
    }
}
