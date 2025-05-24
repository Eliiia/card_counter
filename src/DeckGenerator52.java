import java.util.ArrayList;

public class DeckGenerator52 implements IDeckGenerator {

    // Generate one set of 52 standard cards.

    public ArrayList<Card> generateDeck() {
        ArrayList<Card> deck = new ArrayList<>();

        for (Suit s : Suit.values()) {
            for (Rank r : Rank.values()) {
                deck.add(new StandardCard(s, r));
            }
        }

        return deck;
    }
}
