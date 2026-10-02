package models;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {code Source}.
 * <p>
 * Ensures the constructor and getters return the expected values.
 * </p>
 *
 * @version 1.0
 */
public class SourceTest {

    /**
     * Verifies that the constructor correctly assigns all fields and the getters
     * return those exact values.
     *
     */
    @Test
    public void testConstructorAndGetters() {
        Source s = new Source(
                "tc",
                "TechCrunch",
                "Tech news and analysis",
                "https://techcrunch.com",
                "technology",
                "en",
                "us"
        );

        assertEquals("tc", s.getId());
        assertEquals("TechCrunch", s.getName());
        assertEquals("Tech news and analysis", s.getDescription());
        assertEquals("https://techcrunch.com", s.getUrl());
        assertEquals("technology", s.getCategory());
        assertEquals("en", s.getLanguage());
        assertEquals("us", s.getCountry());
    }

    /**
     * Optional: If {@link Source} overrides {@code toString()}, this test validates that it
     * includes key fields. If not overridden, feel free to remove this test.
     *
     *
     */
    @Test
    public void testToStringIncludesKeyFieldsIfOverridden() {
        Source s = new Source(
                "tc",
                "TechCrunch",
                "Tech news and analysis",
                "https://techcrunch.com",
                "technology",
                "en",
                "us"
        );
        String str = s.toString();
        // This will pass if toString contains these fields. If Source doesn't override toString,
        // you can remove this test or adjust expectations.
        assertTrue(str.contains("TechCrunch") || str.contains("tc") || str.contains("techcrunch.com"));
    }
}
