package utils;

import models.Article;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SentimentAnalysis {
    private static final Pattern WORD_PATTERN = Pattern.compile("[a-z']+");
    //private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");
    private static final Map<String, Integer> LEXICON = loadLexicon();

    /**
     * Loads the lexicon from a csv file when the application starts.
     * @return lexicon HashMap
     * @author Finn Kleckner
     */
    private static Map<String, Integer> loadLexicon() {
        Map<String, Integer> lex = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get("app/utils/bangliu.csv"));

            // Skip header (first line)
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                String[] parts = line.split(",");
                String word = parts[1].trim().toLowerCase();
                // System.err.println("Loading word: " + word);
                int sentiment = Integer.parseInt(parts[2].trim());
                lex.put(word, sentiment);
//                System.err.println("Lexicon contains: " + lex.containsKey(word));
//                System.err.println("Lexicon value: " + lex.get(word));
//                System.err.println("Lexicon size: " + lex.size());
//                System.err.println("---------------------------------------");
            }
            //System.err.println("✓ Loaded " + lex.size() + " words from lexicon");
        } catch (IOException e) {
            System.err.println("✗ Failed to load lexicon: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("✗ Invalid number format in lexicon: " + e.getMessage());
        }
        return lex;
    }


    /**
     * This function takes a list of articles and returns a string representing the sentiment of the articles.
     * @param articles list of articles from api call
     * @return happy, sad, or neutral face
     * @author Finn Kleckner
     */
    public static String analyzeSentiment(List<Article> articles) {

        SentimentTuple sentiments = articles.stream().map(SentimentAnalysis::numberOfNegAndPosWords).reduce(new SentimentTuple(0, 0), (a, b) -> new SentimentTuple(a.getPositives() + b.getPositives(), a.getNegatives() + b.getNegatives()));
        int totalWords = sentiments.getPositives() + sentiments.getNegatives();
        int positivePercentage = (int) ((double) sentiments.getPositives() / totalWords * 100);
        int negativePercentage = (int) ((double) sentiments.getNegatives() / totalWords * 100);
        if (positivePercentage > 65) {
            System.err.println("Positive sentiment: " + positivePercentage + " percent ");
            return ":-)";
        } else if (negativePercentage > 65){
            System.err.println("Negative sentiment: " + negativePercentage + " percent ");
            return ":-(";
        } else {
            System.err.println("Neutral sentiment: \n Percent of words positive, negative " + positivePercentage + "," + negativePercentage + "\n Number of words positive, negative " + ", " + sentiments.getPositives() + ", " + sentiments.getNegatives());
            return ":-|";
        }
    }

    /**
     * Extracts a list of lowercase words from a string of text that may contain punctuation.
     * @param text from article description
     * @return list of words
     * @author Finn Kleckner
     */
    public static List<String> extractWords(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        // System.err.println("Extracting words from: " + text);
        return WORD_PATTERN.matcher(text.toLowerCase())
                .results()
                .map(MatchResult::group)
                .toList();

    }

    /**
     * Counts the number of positive and negative words in the article and returns them as a tuple.
     * @param article from api call
     * @return Tuple of positive and negative words.
     * @author Finn Kleckner
     */
    public static SentimentTuple numberOfNegAndPosWords(Article article) {
        //String cleanedContent = HTML_TAG_PATTERN.matchexr(article.getContent()).replaceAll(" ");
        List<String> allWords = Stream.of(
                article.getDescription()
            )
            .flatMap(text -> extractWords(text).stream())
            .toList();

        int negatives = 0;
        int positives = 0;

        for (String word : allWords) {
            System.err.println("Word: " + word);
            System.err.println("Lexicon contains: " + LEXICON.containsKey(word));
            System.err.println("Lexicon value: " + LEXICON.get(word));
            word = word.trim().toLowerCase();
            if (LEXICON.containsKey(word)) {
                if (LEXICON.get(word) == 1) {
                    positives++;
                } else {
                    negatives++;
                }
            }
        }

        return new SentimentTuple(positives, negatives);
    }

}