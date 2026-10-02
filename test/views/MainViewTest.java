package views;

import org.junit.Test;
import play.twirl.api.Content;

import static org.junit.Assert.*;

/**
 * Unit tests for {@code main.scala.html}.
 * <p>
 * These tests verify that the base layout template correctly renders
 * the title, static assets, and embedded page content.
 * </p>
 *
 * <p>Tests are self-contained and do not require a running Play server.</p>
 *
 * @version 1.0
 */
public class MainViewTest {

    @Test
    public void testMainTemplateBasicRender() {
        Content html = views.html.main.render("Test Page", play.twirl.api.Html.apply("<p>Hello!</p>"));
        String body = html.body();

        assertEquals("text/html", html.contentType());
        assertTrue(body.contains("<title>Test Page</title>"));
        assertTrue(body.contains("<p>Hello!</p>"));
    }

    @Test
    public void testMainTemplateIncludesAssets() {
        Content html = views.html.main.render("Assets Test", play.twirl.api.Html.apply("<div>Assets</div>"));
        String body = html.body();

        assertTrue(body.contains("stylesheets/main.css"));
        assertTrue(body.contains("images/favicon.png"));
        assertTrue(body.contains("javascripts/main.js"));
    }

    @Test
    public void testMainTemplateHandlesEmptyContent() {
        Content html = views.html.main.render("Empty Page", play.twirl.api.Html.apply(""));
        String body = html.body();

        assertEquals("text/html", html.contentType());
        assertTrue(body.contains("<title>Empty Page</title>"));
        assertFalse(body.contains("<p>"));
    }
}
