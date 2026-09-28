package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link NewsAPIService}.
 * Covers word statistics, source fetching, URL helpers,
 * and resilience under malformed or null inputs.
 *
 * @author Muhammed Zayed
 * @version 2.1
 */
public class NewsAPIServiceTest {

    private WSClient wsClient;
    private WSRequest wsRequest;
    private WSResponse wsResponse;
    private NewsAPIService service;
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        wsClient = mock(WSClient.class);
        wsRequest = mock(WSRequest.class);
        wsResponse = mock(WSResponse.class);
        service = new NewsAPIService(wsClient);
        mapper = new ObjectMapper();
    }

    /** Utility to mock a WS request/response. */
    private void stubRequest(String expectedUrl, int status, JsonNode body) {
        when(wsClient.url(expectedUrl)).thenReturn(wsRequest);
        when(wsRequest.get()).thenReturn(CompletableFuture.completedFuture(wsResponse));
        when(wsResponse.getStatus()).thenReturn(status);
        if (status == 200 && body != null) when(wsResponse.asJson()).thenReturn(body);
    }

    /** Verifies correct word counting and sorting. */
    @Test
    public void testGetWordStatsCountsAndSortsWordsCorrectly() throws Exception {
        String query = "java";
        String expectedUrl = "https://newsapi.org/v2/everything?q=" + query +
                "&apiKey=0eb7128c8d5c41d4a367f4dc22da97bb&pageSize=50&sortBy=publishedAt";
        String json = "{ \"status\":\"ok\", \"articles\":[ " +
                "{ \"description\":\"Java AI rocks! Java news is cool.\" }, " +
                "{ \"description\":\"AI rocks again with Java updates.\" } ] }";

        JsonNode body = mapper.readTree(json);
        stubRequest(expectedUrl, 200, body);

        Map<String, Long> stats = service.getWordStats(query).toCompletableFuture().join();

        verify(wsClient).url(expectedUrl);
        assertTrue(stats.containsKey("java"));
        assertEquals(Long.valueOf(3), stats.get("java"));
    }

    /** Checks that non-alphabetic characters are ignored. */
    @Test
    public void testGetWordStatsIgnoresNonAlphabeticCharacters() throws Exception {
        String query = "symbols";
        String expectedUrl = "https://newsapi.org/v2/everything?q=" + query +
                "&apiKey=0eb7128c8d5c41d4a367f4dc22da97bb&pageSize=50&sortBy=publishedAt";
        String json = "{ \"articles\":[ {\"description\":\"C++ & Java! Python-3 rocks?\"} ]}";
        JsonNode body = mapper.readTree(json);
        stubRequest(expectedUrl, 200, body);

        Map<String, Long> stats = service.getWordStats(query).toCompletableFuture().join();
        assertTrue(stats.keySet().containsAll(Arrays.asList("c", "java", "python")));
    }

    /** Ensures null or blank descriptions are skipped. */
    @Test
    public void testGetWordStatsHandlesEmptyOrNullDescriptions() throws Exception {
        String query = "empty";
        String expectedUrl = "https://newsapi.org/v2/everything?q=" + query +
                "&apiKey=0eb7128c8d5c41d4a367f4dc22da97bb&pageSize=50&sortBy=publishedAt";
        String json = "{ \"articles\":[ {\"description\":null}, {\"description\":\"   \"} ]}";
        JsonNode body = mapper.readTree(json);
        stubRequest(expectedUrl, 200, body);

        Map<String, Long> stats = service.getWordStats(query).toCompletableFuture().join();
        assertTrue(stats.isEmpty());
    }

    /** Confirms top 50 word limit is enforced. */
    @Test
    public void testGetWordStatsLimitsToTop50Words() throws Exception {
        String query = "limit";
        String expectedUrl = "https://newsapi.org/v2/everything?q=" + query +
                "&apiKey=0eb7128c8d5c41d4a367f4dc22da97bb&pageSize=50&sortBy=publishedAt";
        StringBuilder jsonBuilder = new StringBuilder("{\"articles\":[");
        for (int i = 1; i <= 100; i++) {
            jsonBuilder.append("{\"description\":\"word").append(i).append(" example\"}");
            if (i < 100) jsonBuilder.append(",");
        }
        jsonBuilder.append("]}");
        JsonNode body = mapper.readTree(jsonBuilder.toString());
        stubRequest(expectedUrl, 200, body);

        Map<String, Long> stats = service.getWordStats(query).toCompletableFuture().join();
        assertEquals(50, stats.size());
    }

    /** Returns empty map for non-200 API responses. */
    @Test
    public void testGetWordStatsReturnsEmptyMapOnNon200Response() throws Exception {
        String query = "fail";
        String expectedUrl = "https://newsapi.org/v2/everything?q=" + query +
                "&apiKey=0eb7128c8d5c41d4a367f4dc22da97bb&pageSize=50&sortBy=publishedAt";
        stubRequest(expectedUrl, 500, null);

        Map<String, Long> stats = service.getWordStats(query).toCompletableFuture().join();
        assertTrue(stats.isEmpty());
    }

    /** Tests constructSourceUrlFromArticle normal and fallback cases. */
    @Test
    public void testConstructSourceUrlFromArticleVariants() throws Exception {
        var m = NewsAPIService.class
                .getDeclaredMethod("constructSourceUrlFromArticle", String.class, String.class, String.class);
        m.setAccessible(true);
        assertTrue(((String) m.invoke(service, "bbc", "BBC News", "https://www.bbc.com/news")).contains("bbc.com"));
        assertEquals("https://cnn.com", m.invoke(service, "cnn", "CNN News", "#"));
    }

    /** Tests all branches in extractDomainFromUrl. */
    @Test
    public void testExtractDomainFromUrlCoversAllCases() throws Exception {
        var m = NewsAPIService.class.getDeclaredMethod("extractDomainFromUrl", String.class);
        m.setAccessible(true);
        assertEquals("https://www.google.com", m.invoke(service, "https://www.google.com/search?q=test"));
        assertNull(m.invoke(service, "#"));
        assertNull(m.invoke(service, ""));
        assertNull(m.invoke(service, "not_a_url"));
    }

    /** Verifies constructSourceUrl fallback behavior. */
    @Test
    public void testConstructSourceUrlVariants() throws Exception {
        var m = NewsAPIService.class.getDeclaredMethod("constructSourceUrl", String.class, String.class);
        m.setAccessible(true);
        assertEquals("https://bbc.com", m.invoke(service, "bbc", "BBC News"));
        assertEquals("https://cnnnews.com", m.invoke(service, "", "CNN News"));
        assertEquals("#", m.invoke(service, "", "No source"));
        assertEquals("#", m.invoke(service, null, null));
    }

    /** Checks parsing of sources from listSources(). */
    @Test
    public void testListSourcesParsesSourcesCorrectly() throws Exception {
        String url = "https://newsapi.org/v2/top-headlines/sources?apiKey=0eb7128c8d5c41d4a367f4dc22da97bb&category=tech";
        JsonNode body = mapper.readTree(
                "{\"sources\":[{\"id\":\"bbc\",\"name\":\"BBC News\",\"description\":\"desc\",\"url\":\"url\",\"category\":\"tech\",\"language\":\"en\",\"country\":\"gb\"}]}");
        stubRequest(url, 200, body);

        List<Source> list = service.listSources(Optional.of("tech"), Optional.empty(), Optional.empty())
                .toCompletableFuture().join();
        assertEquals("bbc", list.get(0).getId());
    }

    /** Returns empty list for listSources() non-200 response. */
    @Test
    public void testListSourcesHandlesNon200() throws Exception {
        String url = "https://newsapi.org/v2/top-headlines/sources?apiKey=0eb7128c8d5c41d4a367f4dc22da97bb";
        stubRequest(url, 404, null);
        List<Source> list = service.listSources(Optional.empty(), Optional.empty(), Optional.empty())
                .toCompletableFuture().join();
        assertTrue(list.isEmpty());
    }

    /** Ensures getSourceById() finds the correct source. */
    @Test
    public void testGetSourceByIdFindsSource() throws Exception {
        NewsAPIService spy = spy(service);
        Source src = new Source("bbc", "BBC", "desc", "url", "tech", "en", "gb");
        doReturn(CompletableFuture.completedFuture(List.of(src)))
                .when(spy).listSources(any(), any(), any());
        Optional<Source> found = spy.getSourceById("bbc").toCompletableFuture().join();
        assertTrue(found.isPresent());
    }

    /** Ensures getSourceById() returns empty when not found. */
    @Test
    public void testGetSourceByIdReturnsEmptyWhenMissing() throws Exception {
        NewsAPIService spy = spy(service);
        doReturn(CompletableFuture.completedFuture(Collections.emptyList()))
                .when(spy).listSources(any(), any(), any());
        Optional<Source> result = spy.getSourceById("none").toCompletableFuture().join();
        assertFalse(result.isPresent());
    }

    /** Tests parsing of articles from getLatestArticlesForSource(). */
    @Test
    public void testGetLatestArticlesForSourceParsesArticles() throws Exception {
        String sourceId = "bbc";
        String url = "https://newsapi.org/v2/top-headlines?sources=" + sourceId +
                "&apiKey=0eb7128c8d5c41d4a367f4dc22da97bb&pageSize=5";
        String json = "{\"articles\":[{\"title\":\"T1\",\"description\":\"D1\",\"url\":\"https://bbc.com/a1\"," +
                "\"publishedAt\":\"2025-01-01\",\"source\":{\"id\":\"bbc\",\"name\":\"BBC\"}}]}";
        JsonNode body = mapper.readTree(json);
        stubRequest(url, 200, body);

        List<Article> list = service.getLatestArticlesForSource(sourceId, 5).toCompletableFuture().join();
        assertEquals(1, list.size());
    }

    /** Returns empty list when getLatestArticlesForSource() fails. */
    @Test
    public void testGetLatestArticlesForSourceHandlesNon200() throws Exception {
        String url = "https://newsapi.org/v2/top-headlines?sources=x&apiKey=0eb7128c8d5c41d4a367f4dc22da97bb&pageSize=3";
        stubRequest(url, 500, null);
        assertTrue(service.getLatestArticlesForSource("x", 3).toCompletableFuture().join().isEmpty());
    }

    /** Returns cached articles directly in getArticlesBySourceNameOrId(). */
    @Test
    public void testGetArticlesBySourceNameOrIdReturnsDirectlyIfNonEmpty() throws Exception {
        NewsAPIService spy = spy(service);
        List<Article> existing = List.of(new Article("BBC", "bbc", "url", "a", "t", "d", "u", "p", 1.0, 1.0));
        doReturn(CompletableFuture.completedFuture(existing))
                .when(spy).getLatestArticlesForSource(eq("bbc"), anyInt());
        assertEquals(1, spy.getArticlesBySourceNameOrId("bbc", 5).toCompletableFuture().join().size());
    }

    /** Ensures fallback works in getArticlesBySourceNameOrId(). */
    @Test
    public void testGetArticlesBySourceNameOrIdFallsBackProperly() throws Exception {
        NewsAPIService spy = spy(service);
        doReturn(CompletableFuture.completedFuture(Collections.emptyList()))
                .when(spy).getLatestArticlesForSource(eq("cnn"), anyInt());
        String expectedUrl = "https://newsapi.org/v2/everything?q=cnn&apiKey=0eb7128c8d5c41d4a367f4dc22da97bb&pageSize=5&sortBy=publishedAt";
        String json = "{\"articles\":[{\"title\":\"T1\",\"description\":\"D1\",\"url\":\"https://cnn.com/a1\"," +
                "\"publishedAt\":\"2025-01-01\",\"source\":{\"id\":\"cnn\",\"name\":\"CNN\"}}]}";
        JsonNode body = mapper.readTree(json);
        stubRequest(expectedUrl, 200, body);

        List<Article> result = spy.getArticlesBySourceNameOrId("cnn", 5).toCompletableFuture().join();
        assertEquals("CNN", result.get(0).getSourceName());
    }

    /** Returns empty list when fallback request fails. */
    @Test
    public void testGetArticlesBySourceNameOrIdHandlesNon200Fallback() throws Exception {
        NewsAPIService spy = spy(service);
        doReturn(CompletableFuture.completedFuture(Collections.emptyList()))
                .when(spy).getLatestArticlesForSource(eq("none"), anyInt());
        String expectedUrl = "https://newsapi.org/v2/everything?q=none&apiKey=0eb7128c8d5c41d4a367f4dc22da97bb&pageSize=2&sortBy=publishedAt";
        stubRequest(expectedUrl, 404, null);
        assertTrue(spy.getArticlesBySourceNameOrId("none", 2).toCompletableFuture().join().isEmpty());
    }

    /** Triggers exception branch in extractDomainFromUrl(). */
    @Test
    public void testExtractDomainFromUrlTriggersException() throws Exception {
        var m = NewsAPIService.class.getDeclaredMethod("extractDomainFromUrl", String.class);
        m.setAccessible(true);
        assertNull(m.invoke(service, "ht!tp://%%%bad_url%%%"));
    }

    /** Covers cleanName fallback in constructSourceUrl(). */
    @Test
    public void testConstructSourceUrlCleansSourceNameWithSpecialChars() throws Exception {
        var m = NewsAPIService.class.getDeclaredMethod("constructSourceUrl", String.class, String.class);
        m.setAccessible(true);
        assertEquals("https://cnnnews24.com", m.invoke(service, "", "CNN @ News 24!"));
    }

    /** Covers branch when articleUrl is '#' in constructSourceUrlFromArticle(). */
    @Test
    public void testConstructSourceUrlFromArticleHandlesHashUrl() throws Exception {
        var m = NewsAPIService.class.getDeclaredMethod(
                "constructSourceUrlFromArticle", String.class, String.class, String.class);
        m.setAccessible(true);
        assertEquals("https://abc.com", m.invoke(service, "abc", "ABC News", "#"));
    }

    /** Covers domain==null fallback path in constructSourceUrlFromArticle(). */
    @Test
    public void testConstructSourceUrlFromArticleWhenDomainIsNull() throws Exception {
        var method = NewsAPIService.class.getDeclaredMethod(
                "constructSourceUrlFromArticle", String.class, String.class, String.class);
        method.setAccessible(true);
        assertEquals("https://cnn.com", method.invoke(service, "cnn", "CNN", "ht!tp://%%%bad_url%%%"));
    }

        /**
     * Covers early-return branch in extractDomainFromUrl()
     * when URL is "#", empty, or null (line 327).
     *
     * @author Muhammed Zayed
     */
    @Test
    public void testExtractDomainFromUrlHandlesEmptyAndHash() throws Exception {
        var m = NewsAPIService.class.getDeclaredMethod("extractDomainFromUrl", String.class);
        m.setAccessible(true);

        // Test all early-return cases
        assertNull(m.invoke(service, (Object) "#"));     // "#"
        assertNull(m.invoke(service, (Object) ""));      // empty string
        assertNull(m.invoke(service, (Object) null));    // null
    }


}
