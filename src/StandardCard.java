// A simple card with two elements to it.
public class StandardCard implements Card {
    private final Suit suit;
    private final Rank rank;

    public StandardCard(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public String toString() {
        return getSuit() + " of " + getRank();
    }
}

