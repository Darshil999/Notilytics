package utils;


/**
 * Custom Tuple class for returning the number of positive and negative words in an article.
 */
public class SentimentTuple {
    private int positives;
    private int negatives;

    public SentimentTuple(int positives, int negatives) {
        this.positives = positives;
        this.negatives = negatives;
    }

    public int getPositives() {
        return positives;
    }

    public int getNegatives() {
        return negatives;
    }
}
