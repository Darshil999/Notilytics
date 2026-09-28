package controllers;

import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;
import play.twirl.api.Content;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;
import static org.junit.Assert.assertTrue;

import models.SearchQuery;
import java.util.ArrayList;
import java.util.List;
public class HomeControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void testIndex() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }
    @Test
    public void renderTemplate() {
        Http.RequestBuilder requestBuilder = new Http.RequestBuilder()
                .method("GET")
                .uri("/");
        Http.Request request = requestBuilder.build();

        List<SearchQuery> searchHistory = new ArrayList<>(); //

        Content html = views.html.index.render("test", searchHistory, request);

        assertEquals("text/html", html.contentType());
        assertTrue(html.body().contains("test"));
    }

}
