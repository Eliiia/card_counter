import java.util.ArrayList;

public class DeckGenerator156 implements IDeckGenerator {

    // Generate three sets of 52 standard cards (156 total) together.

    public ArrayList<Card> generateDeck() {
        ArrayList<Card> deck = new ArrayList<>();

        for (int i = 0; i <= 2; i++) {
            for (Suit s : Suit.values()) {
                for (Rank r : Rank.values()) {
                    deck.add(new StandardCard(s, r));
                }
            }
        }

        return deck;
    }
}
