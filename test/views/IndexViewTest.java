package views;

import models.Article;
import models.SearchQuery;
import org.junit.Test;
import play.mvc.Http;
import play.twirl.api.Content;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Comprehensive JUnit test suite for {@code index.scala.html}.
 * <p>
 * Validates proper rendering of the NotiLytics main page, including:
 * </p>
 * <ul>
 *   <li>Dynamic display of search results and error messages.</li>
 *   <li>Correct population of radio buttons and dropdown filters.</li>
 *   <li>Safe rendering for null and empty inputs.</li>
 *   <li>Presence of sentiment icons, news-source section, and page layout.</li>
 * </ul>
 *
 * <p><b>Associated Template:</b>
 * {@code index.scala.html(message: String, searchHistory: List[SearchQuery], request: Http.Request)}</p>
 *
 * <p><b>Authors (template & tests):</b>  
 *
 * @version 3.1
 * @since 2025-11-08
 */
public class IndexViewTest {

    /** Helper: creates a fake request for rendering templates. */
    private Http.Request fakeRequest() {
        return new Http.RequestBuilder().method("GET").uri("/").build();
    }

    // ----------------------------------------------------------------
    // CORE VIEW BEHAVIOR
    // ----------------------------------------------------------------

    /** Tests: renders correctly when no search results are available. */
    @Test
    public void testIndexViewWithEmptyResults() {
        SearchQuery queryResult = new SearchQuery("ok", "", "", Collections.emptyList(), 0, ":-|");
        Http.Request request = fakeRequest();

        Content html = views.html.index.render("NotiLytics", List.of(queryResult), request);
        String body = html.body();

        assertEquals("text/html", html.contentType());
        assertTrue(body.contains("NotiLytics"));
        assertTrue(body.contains("Enter search terms"));
        assertFalse(body.contains("Search Results"));
        assertTrue(body.contains("News Sources"));
    }

    /** Tests: renders correctly when multiple articles exist in the list. */
    @Test
    public void testIndexViewWithArticles() {
        Article article1 = new Article(
                "TechCrunch", "John Doe", "AI Revolution",
                "How AI is transforming the world",
                "https://example.com/ai", "2025-11-03"
        );
        Article article2 = new Article(
                "BBC", "Jane Smith", "Play Framework Updates",
                "Play 3.0 release details",
                "https://example.com/play", "2025-10-30"
        );

        SearchQuery queryResult = new SearchQuery("ok", "AI", "", Arrays.asList(article1, article2), 2, ":-|");
        Http.Request request = fakeRequest();

        Content html = views.html.index.render("NotiLytics", List.of(queryResult), request);
        String body = html.body();

        assertEquals("text/html", html.contentType());
        assertTrue(body.contains("AI Revolution"));
        assertTrue(body.contains("Play Framework Updates"));
    }

    /** Tests: displays an error message properly when status is "error". */
    @Test
    public void testIndexViewWithErrorStatus() {
        SearchQuery queryResult = new SearchQuery("error", "", "", Collections.emptyList(), 0, ":-|");
        Http.Request request = fakeRequest();

        Content html = views.html.index.render("Error fetching results", List.of(queryResult), request);
        String body = html.body();

        assertEquals("text/html", html.contentType());
        assertTrue(body.contains("Error fetching results"));
    }

    // ----------------------------------------------------------------
    // DROPDOWNS & SORT CONTROLS
    // ----------------------------------------------------------------

    /** Tests: ensures country, category, and language dropdowns exist. */
    @Test
    public void testDropdownFiltersExist() {
        SearchQuery query = new SearchQuery("ok", "", "", Collections.emptyList(), 0, ":-|");
        Http.Request request = fakeRequest();
        Content html = views.html.index.render("NotiLytics", List.of(query), request);
        String body = html.body();

        assertTrue(body.contains("select name=\"country\""));
        assertTrue(body.contains("select name=\"category\""));
        assertTrue(body.contains("select name=\"language\""));
    }

    /** Tests: verifies the presence of sort-by radio buttons. */
    @Test
    public void testSortByRadioButtonsExist() {
        SearchQuery query = new SearchQuery("ok", "", "", Collections.emptyList(), 0, ":-|");
        Http.Request request = fakeRequest();
        Content html = views.html.index.render("NotiLytics", List.of(query), request);
        String body = html.body();

        assertTrue(body.contains("value=\"publishedAt\""));
        assertTrue(body.contains("value=\"relevancy\""));
        assertTrue(body.contains("value=\"popularity\""));
    }

    /** Tests: validates key country names are in the dropdown. */
    @Test
    public void testCountryDropdownIncludesExpectedCountries() {
        SearchQuery query = new SearchQuery("ok", "", "", Collections.emptyList(), 0, ":-|");
        Http.Request request = fakeRequest();
        Content html = views.html.index.render("NotiLytics", List.of(query), request);
        String body = html.body();

        assertTrue(body.contains("India"));
        assertTrue(body.contains("United States"));
        assertTrue(body.contains("Canada"));
        assertTrue(body.contains("United Kingdom"));
    }

    /** Tests: validates that all category options are present. */
    @Test
    public void testCategoryDropdownIncludesExpectedOptions() {
        SearchQuery query = new SearchQuery("ok", "", "", Collections.emptyList(), 0, ":-|");
        Http.Request request = fakeRequest();
        Content html = views.html.index.render("NotiLytics", List.of(query), request);
        String body = html.body();

        assertTrue(body.contains("Business"));
        assertTrue(body.contains("Entertainment"));
        assertTrue(body.contains("Health"));
        assertTrue(body.contains("Technology"));
    }

    /** Tests: confirms all language options appear in dropdown. */
    @Test
    public void testLanguageDropdownIncludesExpectedOptions() {
        SearchQuery query = new SearchQuery("ok", "", "", Collections.emptyList(), 0, ":-|");
        Http.Request request = fakeRequest();
        Content html = views.html.index.render("NotiLytics", List.of(query), request);
        String body = html.body();

        assertTrue(body.contains("English"));
        assertTrue(body.contains("French"));
        assertTrue(body.contains("Arabic"));
        assertTrue(body.contains("Chinese"));
    }

    // ----------------------------------------------------------------
    // RENDERING SAFETY & VISUAL ELEMENTS
    // ----------------------------------------------------------------

    /** Tests: ensures sentiment icons (:-| etc.) render safely in HTML. */
    @Test
    public void testSentimentIconsRenderSafely() {
        SearchQuery query = new SearchQuery("ok", "", "", Collections.emptyList(), 0, ":-|");
        Http.Request request = fakeRequest();
        Content html = views.html.index.render("NotiLytics – Word Stats :-|", List.of(query), request);
        String body = html.body();

        assertTrue(body.contains(":-|"));
        assertTrue(body.contains("NotiLytics"));
    }

    /** Tests: verifies safe rendering when searchHistory is null. */
    @Test
    public void testRendersSafelyWithNullSearchHistory() {
        Http.Request request = fakeRequest();
        Content html = views.html.index.render("NotiLytics", null, request);
        String body = html.body();

        assertTrue(body.contains("NotiLytics"));
        assertTrue(body.contains("Enter search terms"));
    }
}
