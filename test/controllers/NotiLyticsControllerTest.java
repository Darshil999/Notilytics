package controllers;

import models.*;
import org.junit.*;
import org.mockito.*;
import play.data.*;
import play.mvc.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.*;

/**
 * Comprehensive JUnit 4 test suite for {@link NotiLyticsController}.
 * <p>
 * This class validates the behavior of all public and key private methods
 * within {@code NotiLyticsController}, ensuring the following:
 * </p>
 * <ul>
 *     <li>Correct rendering of the index page and session handling.</li>
 *     <li>Proper processing of search form inputs and NewsAPI responses.</li>
 *     <li>Reliable computation of word statistics via asynchronous services.</li>
 *     <li>Correct rendering of source profiles and error fallback behaviors.</li>
 *     <li>Robust handling of empty input, null data, and exceptions.</li>
 * </ul>
 * <p>
 * The tests rely on <strong>Mockito</strong> for dependency mocking and
 * <strong>Play Framework’s MVC</strong> testing utilities for verifying HTTP responses.
 * </p>
 *
 * @version 3.0
 * @since 2025-11-08
 * @see controllers.NotiLyticsController
 */
public class NotiLyticsControllerTest {

    @Mock private FormFactory formFactory;
    @Mock private DynamicForm form;
    @Mock private NewsAPIService newsApiService;
    @Mock private SourcesAPIService sourcesApiService;
    @Mock private Http.Request request;
    @Mock private Http.Session session;

    private NotiLyticsController controller;

    /** Initializes mock dependencies before each test execution. */
    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(request.session()).thenReturn(session);
        controller = new NotiLyticsController(formFactory, newsApiService, sourcesApiService);
    }

    // ---------------------------------------------------------------------
    // INDEX
    // ---------------------------------------------------------------------

    /** Tests: index() returns 200 OK when a session already exists. */
    @Test
    // Test: ensures existing session IDs are reused properly.
    public void index_shouldReturnOk_whenSessionExists() {
        when(session.get("sessionId")).thenReturn(Optional.of("abc"));
        Result result = controller.index(request);
        assertEquals(OK, result.status());
    }

    /** Tests: index() returns 200 OK when no session exists. */
    @Test
    // Test: ensures a new session ID is generated when none exists.
    public void index_shouldReturnOk_whenNoSessionExists() {
        when(session.get("sessionId")).thenReturn(Optional.empty());
        Result result = controller.index(request);
        assertEquals(OK, result.status());
    }

    // ---------------------------------------------------------------------
    // SEARCH
    // ---------------------------------------------------------------------

    /** Tests: search() returns 400 BAD_REQUEST when search term is null. */
    @Test
    // Author: Finn Kleckner, Priya Dhanvi, Wei Huang
    public void search_shouldReturnBadRequest_whenSearchTermNull() throws Exception {
        when(formFactory.form()).thenReturn(form);
        when(form.bindFromRequest(request)).thenReturn(form);
        when(form.get("searchTerm")).thenReturn(null);
        when(form.get("sortBy")).thenReturn("relevancy");

        Result result = controller.search(request).toCompletableFuture().get();
        assertEquals(BAD_REQUEST, result.status());
    }

    /** Tests: search() returns 400 BAD_REQUEST when search term is blank. */
    @Test
    // Author: Finn Kleckner, Priya Dhanvi, Wei Huang
    public void search_shouldReturnBadRequest_whenSearchTermBlank() throws Exception {
        when(formFactory.form()).thenReturn(form);
        when(form.bindFromRequest(request)).thenReturn(form);
        when(form.get("searchTerm")).thenReturn("   ");
        when(form.get("sortBy")).thenReturn("publishedAt");

        Result result = controller.search(request).toCompletableFuture().get();
        assertEquals(BAD_REQUEST, result.status());
    }

    /** Tests: search() returns 200 OK when NewsAPIService returns status "ok". */
    @Test
    // Author: Finn Kleckner, Priya Dhanvi, Wei Huang
    public void search_shouldReturnOk_whenServiceReturnsOk() throws Exception {
        when(formFactory.form()).thenReturn(form);
        when(form.bindFromRequest(request)).thenReturn(form);
        when(form.get("searchTerm")).thenReturn("AI");
        when(form.get("sortBy")).thenReturn("popularity");
        when(session.get("sessionId")).thenReturn(Optional.empty());

        List<Article> many = mockArticles(12);
        SearchQuery sq = new SearchQuery("ok", "AI", "popularity", many, many.size(), "positive");
        when(newsApiService.search("AI", "popularity"))
                .thenReturn(CompletableFuture.completedFuture(sq));

        Result result = controller.search(request).toCompletableFuture().get();
        assertEquals(OK, result.status());
    }

    /** Tests: search() returns 500 INTERNAL_SERVER_ERROR when API returns "error". */
    @Test
    // Author: Finn Kleckner, Priya Dhanvi, Wei Huang
    public void search_shouldReturnInternalServerError_whenStatusError() throws Exception {
        when(formFactory.form()).thenReturn(form);
        when(form.bindFromRequest(request)).thenReturn(form);
        when(form.get("searchTerm")).thenReturn("bad");
        when(form.get("sortBy")).thenReturn("relevancy");
        when(session.get("sessionId")).thenReturn(Optional.of("abc"));

        SearchQuery sq = new SearchQuery("error", "bad", "relevancy",
                Collections.emptyList(), 0, "neutral");
        when(newsApiService.search("bad", "relevancy"))
                .thenReturn(CompletableFuture.completedFuture(sq));

        Result result = controller.search(request).toCompletableFuture().get();
        assertEquals(INTERNAL_SERVER_ERROR, result.status());
    }

    /** Tests: search() handles null articles list gracefully. */
    @Test
    // Author: Wei Huang
    public void search_shouldHandleNullArticlesGracefully() throws Exception {
        when(formFactory.form()).thenReturn(form);
        when(form.bindFromRequest(request)).thenReturn(form);
        when(form.get("searchTerm")).thenReturn("java");
        when(form.get("sortBy")).thenReturn("relevancy");
        when(session.get("sessionId")).thenReturn(Optional.empty());

        SearchQuery sq = new SearchQuery("ok", "java", "relevancy", null, 0, "neutral");
        when(newsApiService.search("java", "relevancy"))
                .thenReturn(CompletableFuture.completedFuture(sq));

        Result result = controller.search(request).toCompletableFuture().get();
        assertEquals(OK, result.status());
    }

    // ---------------------------------------------------------------------
    // WORD STATS
    // ---------------------------------------------------------------------

    /** Tests: wordStats() returns OK when NewsAPIService completes successfully. */
    @Test
    // Author: Priya Dhanvi
    public void wordStats_shouldReturnOk_whenSuccess() throws Exception {
        Map<String, Long> map = Map.of("AI", 5L);
        when(newsApiService.getWordStats("AI"))
                .thenReturn(CompletableFuture.completedFuture(map));

        Result result = controller.wordStats("AI", request).toCompletableFuture().get();
        assertEquals(OK, result.status());
    }

    /** Tests: wordStats() returns INTERNAL_SERVER_ERROR on exception. */
    @Test
    // Author: Priya Dhanvi
    public void wordStats_shouldReturnError_whenExceptionThrown() throws Exception {
        CompletableFuture<Map<String, Long>> failed =
                CompletableFuture.failedFuture(new RuntimeException("boom"));
        when(newsApiService.getWordStats("AI")).thenReturn(failed);

        Result result = controller.wordStats("AI", request).toCompletableFuture().get();
        assertEquals(INTERNAL_SERVER_ERROR, result.status());
    }

    // ---------------------------------------------------------------------
    // SOURCE PROFILE
    // ---------------------------------------------------------------------

    /** Tests: sourceProfile() renders OK when source is found. */
    @Test
    // Author: Darshil Ketankumar Kalyani
    public void sourceProfile_shouldRenderProfile_whenSourcePresent() throws Exception {
        Source src = mockSource();
        when(newsApiService.getSourceById("bbc"))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(src)));
        when(newsApiService.getArticlesBySourceNameOrId("bbc", 10))
                .thenReturn(CompletableFuture.completedFuture(mockArticles(2)));

        Result result = controller.sourceProfile("bbc", request).toCompletableFuture().get();
        assertEquals(OK, result.status());
    }

    /** Tests: sourceProfile() renders OK when source missing but articles exist. */
    @Test
    // Author: Darshil Ketankumar Kalyani
    public void sourceProfile_shouldRenderProfile_whenSourceMissingButArticlesExist() throws Exception {
        when(newsApiService.getSourceById("cnn"))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
        when(newsApiService.getArticlesBySourceNameOrId("cnn", 10))
                .thenReturn(CompletableFuture.completedFuture(mockArticles(2)));

        Result result = controller.sourceProfile("cnn", request).toCompletableFuture().get();
        assertEquals(OK, result.status());
    }

    /** Tests: sourceProfile() returns 404 when both source and articles are missing. */
    @Test
    // Author: Darshil Ketankumar Kalyani
    public void sourceProfile_shouldReturnNotFound_whenNoSourceNoArticles() throws Exception {
        when(newsApiService.getSourceById("none"))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
        when(newsApiService.getArticlesBySourceNameOrId("none", 10))
                .thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));

        Result result = controller.sourceProfile("none", request).toCompletableFuture().get();
        assertEquals(NOT_FOUND, result.status());
    }

    // ---------------------------------------------------------------------
    // SHOW SOURCES
    // ---------------------------------------------------------------------

    /** Tests: showSources() returns OK for valid country, category, and language. */
    @Test
    // Author: Muhammed Zayed Abdul Nasser, Wei Huang
    public void showSources_shouldReturnOk_whenValid() throws Exception {
        when(request.getQueryString("country")).thenReturn("us");
        when(request.getQueryString("category")).thenReturn("tech");
        when(request.getQueryString("language")).thenReturn("en");

        List<Source> srcs = List.of(mockSource());
        when(sourcesApiService.getSources("us", "tech", "en"))
                .thenReturn(CompletableFuture.completedFuture(srcs));

        Result result = controller.showSources(request).toCompletableFuture().get();
        assertEquals(OK, result.status());
    }

    // ---------------------------------------------------------------------
    // PRIVATE METHOD TESTS
    // ---------------------------------------------------------------------

    /** Tests: getOrCreateSessionId() returns existing session ID when available. */
    @Test
    // Author: Wei Huang
    public void getOrCreateSessionId_shouldReturnExisting() throws Exception {
        when(session.get("sessionId")).thenReturn(Optional.of("xyz"));
        var method = NotiLyticsController.class.getDeclaredMethod("getOrCreateSessionId", Http.Request.class);
        method.setAccessible(true);
        String id = (String) method.invoke(controller, request);
        assertEquals("xyz", id);
    }

    /** Tests: getOrCreateSessionId() generates new ID when missing. */
    @Test
    // Author: Wei Huang
    public void getOrCreateSessionId_shouldGenerateNewWhenMissing() throws Exception {
        when(session.get("sessionId")).thenReturn(Optional.empty());
        var method = NotiLyticsController.class.getDeclaredMethod("getOrCreateSessionId", Http.Request.class);
        method.setAccessible(true);
        String id = (String) method.invoke(controller, request);
        assertNotNull(id);
        assertEquals(36, id.length());
    }

    /** Tests: processArticlesWithStreams() safely handles null and enforces 10-item limit. */
    @Test
    // Author: Wei Huang
    public void processArticlesWithStreams_shouldHandleNullAndTrim() throws Exception {
        var method = NotiLyticsController.class.getDeclaredMethod("processArticlesWithStreams", List.class);
        method.setAccessible(true);

        List<Article> result1 = (List<Article>) method.invoke(controller, (Object) null);
        assertTrue(result1.isEmpty());

        List<Article> result2 = (List<Article>) method.invoke(controller, mockArticles(15));
        assertEquals(10, result2.size());

        List<Article> result3 = (List<Article>) method.invoke(controller, mockArticles(5));
        assertEquals(5, result3.size());
    }

    // ---------------------------------------------------------------------
    // MOCK HELPERS
    // ---------------------------------------------------------------------

    /** Utility: Creates mock articles for testing. */
    private List<Article> mockArticles(int n) {
        return IntStream.range(0, n)
                .mapToObj(i -> new Article("Title" + i, "Desc", "url", "image",
                        "2025-01-01", "Src", "id", "srcUrl"))
                .collect(Collectors.toList());
    }

    /** Utility: Creates a mock Source for testing. */
    private Source mockSource() {
        return new Source("id", "BBC", "desc", "url", "tech", "en", "gb");
    }
}
