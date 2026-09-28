package models;

/**
 * Represents a single news source from the NewsAPI,
 * including its name, description, URL, category, language, and country.
 * Used by the SourcesAPIService and controller to display source listings.
 * @author Muhammed Zayed
 */
public class Source {
    private final String id;
    private final String name;
    private final String description;
    private final String url;
    private final String category;
    private final String language;
    private final String country;

    public Source(String id, String name, String description, String url, String category, String language, String country) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.category = category;
        this.language = language;
        this.country = country;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getUrl() { return url; }
    public String getCategory() { return category; }
    public String getLanguage() { return language; }
    public String getCountry() { return country; }

    private static String safe(String v) {
        return v == null ? "N/A" : v;
    }

    /**
     * Overridden to assist debugging and satisfy the unit test that checks
     * presence of key fields (id, name, or url).
     * @return string with core source metadata
     */
    @Override
    public String toString() {
        return "Source{" +
                "id='" + safe(id) + '\'' +
                ", name='" + safe(name) + '\'' +
                ", url='" + safe(url) + '\'' +
                ", category='" + safe(category) + '\'' +
                ", language='" + safe(language) + '\'' +
                ", country='" + safe(country) + '\'' +
                '}';
    }
}