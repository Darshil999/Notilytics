package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import play.libs.ws.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SourcesAPIService}.
 * <p>
 * Validates that API URLs are correctly built, JSON responses
 * are parsed into {@link Source} objects, and error cases
 * return empty lists.
 * </p>
 *
 * @author Muhammed Zayed
 * @version 1.0
 */
public class SourcesAPIServiceTest {

    private WSClient wsClient;
    private WSRequest wsRequest;
    private WSResponse wsResponse;
    private SourcesAPIService service;
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        wsClient = mock(WSClient.class);
        wsRequest = mock(WSRequest.class);
        wsResponse = mock(WSResponse.class);
        service = new SourcesAPIService(wsClient);
        mapper = new ObjectMapper();
    }

    /**
     * Helper to configure mock request and response.
     */
    private void stubRequest(String expectedUrl, int status, JsonNode json) {
        when(wsClient.url(expectedUrl)).thenReturn(wsRequest);
        when(wsRequest.get()).thenReturn(CompletableFuture.completedFuture(wsResponse));
        when(wsResponse.getStatus()).thenReturn(status);
        if (status == 200) when(wsResponse.asJson()).thenReturn(json);
    }

    /**
     * Verifies that a successful HTTP 200 response
     * correctly parses all fields into {@link Source} objects.
     */
    @Test
    public void testGetSourcesParsesSuccessfully() throws Exception {
        String expectedUrl = "https://newsapi.org/v2/top-headlines/sources?"
                + "apiKey=0eb7128c8d5c41d4a367f4dc22da97bb"
                + "&country=us&category=business&language=en";

        String json = "{ \"sources\": [" +
                "{ \"id\": \"cnn\", \"name\": \"CNN\", \"description\": \"News\", " +
                "\"url\": \"https://cnn.com\", \"category\": \"general\", \"language\": \"en\", \"country\": \"us\" }," +
                "{ \"id\": \"bbc\", \"name\": \"BBC News\", \"description\": \"UK News\", " +
                "\"url\": \"https://bbc.co.uk\", \"category\": \"general\", \"language\": \"en\", \"country\": \"gb\" }]}";

        JsonNode jsonNode = mapper.readTree(json);
        stubRequest(expectedUrl, 200, jsonNode);

        CompletionStage<List<Source>> stage = service.getSources("us", "business", "en");
        List<Source> sources = stage.toCompletableFuture().join();

        verify(wsClient, times(1)).url(expectedUrl);
        assertEquals(2, sources.size());
        assertEquals("CNN", sources.get(0).getName());
        assertEquals("bbc", sources.get(1).getId());
        assertEquals("https://bbc.co.uk", sources.get(1).getUrl());
    }

    /**
     * Ensures that missing fields in JSON default to fallback values.
     */
    @Test
    public void testGetSourcesDefaultsForMissingFields() throws Exception {
        String expectedUrl = "https://newsapi.org/v2/top-headlines/sources?"
                + "apiKey=0eb7128c8d5c41d4a367f4dc22da97bb";
        String json = "{ \"sources\": [ { } ] }";

        JsonNode jsonNode = mapper.readTree(json);
        stubRequest(expectedUrl, 200, jsonNode);

        List<Source> sources = service.getSources("", "", "").toCompletableFuture().join();

        assertEquals(1, sources.size());
        Source s = sources.get(0);
        assertEquals("N/A", s.getId());
        assertEquals("Unnamed", s.getName());
        assertEquals("No description", s.getDescription());
        assertEquals("#", s.getUrl());
        assertEquals("-", s.getCategory());
        assertEquals("-", s.getLanguage());
        assertEquals("-", s.getCountry());
    }

    /**
     * Verifies that non-200 HTTP responses return an empty list.
     */
    @Test
    public void testGetSourcesReturnsEmptyListOnError() {
        String expectedUrl = "https://newsapi.org/v2/top-headlines/sources?"
                + "apiKey=0eb7128c8d5c41d4a367f4dc22da97bb&country=ca";

        stubRequest(expectedUrl, 500, null);

        List<Source> sources = service.getSources("ca", "", "").toCompletableFuture().join();
        assertTrue(sources.isEmpty());
    }

    /**
     * Verifies that the URL is correctly constructed when filters are omitted.
     */
    @Test
    public void testGetSourcesWithNoFiltersBuildsCorrectUrl() throws Exception {
        String expectedUrl = "https://newsapi.org/v2/top-headlines/sources?"
                + "apiKey=0eb7128c8d5c41d4a367f4dc22da97bb";
        String json = "{ \"sources\": [] }";
        JsonNode jsonNode = mapper.readTree(json);
        stubRequest(expectedUrl, 200, jsonNode);

        List<Source> sources = service.getSources(null, null, null).toCompletableFuture().join();

        verify(wsClient).url(expectedUrl);
        assertNotNull(sources);
        assertTrue(sources.isEmpty());
    }
}
