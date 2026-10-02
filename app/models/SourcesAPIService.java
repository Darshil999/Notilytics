package models;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.ws.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.CompletionStage;

/**
 * SourcesAPIService
 * -----------------
 * Fetches available news sources from the NewsAPI based on optional filters
 * like country, category, or language.
 *
 * Example API endpoint:
 * https://newsapi.org/v2/top-headlines/sources?apiKey=API_KEY&country=us&category=sports&language=en
 *
 */
@Singleton
public class SourcesAPIService {

    private final WSClient ws;
    private final String apiKey = "0eb7128c8d5c41d4a367f4dc22da97bb";
    private final String baseUrl = "https://newsapi.org/v2/top-headlines/sources?";

    @Inject
    public SourcesAPIService(WSClient ws) {
        this.ws = ws;
    }

    public CompletionStage<List<Source>> getSources(String country, String category, String language) {
        StringBuilder url = new StringBuilder(baseUrl)
                .append("apiKey=").append(apiKey);

        if (country != null && !country.isEmpty()) url.append("&country=").append(country);
        if (category != null && !category.isEmpty()) url.append("&category=").append(category);
        if (language != null && !language.isEmpty()) url.append("&language=").append(language);

        return ws.url(url.toString()).get().thenApply(response -> {
            if (response.getStatus() == 200) {
                JsonNode json = response.asJson();
                List<Source> sources = new ArrayList<>();
                for (JsonNode node : json.findPath("sources")) {
                    String id = node.findPath("id").asText("N/A");
                    String name = node.findPath("name").asText("Unnamed");
                    String desc = node.findPath("description").asText("No description");
                    String urlStr = node.findPath("url").asText("#");
                    String cat = node.findPath("category").asText("-");
                    String lang = node.findPath("language").asText("-");
                    String ctry = node.findPath("country").asText("-");
                    sources.add(new Source(id, name, desc, urlStr, cat, lang, ctry));
                }
                return sources;
            } else {
                return Collections.emptyList();
            }
        });
    }
}
