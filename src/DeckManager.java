import java.util.ArrayList;
import java.util.Collections;

public class DeckManager<C extends Card> {

    // The CardManager manages the location of all cards within the stack and discard pile.
    // It does not keep track of cards outside. It expects these to be returned to the deck.

    // Stack of cards to be used, and a discard pile
    private final ArrayList<C> discard = new ArrayList<>(); // Last-In-First-Out
    private final ArrayList<C> stack; // Order does not matter; it is shuffled anyway, and you don't add to it

    public DeckManager(IDeckGenerator<C> generator) {
        // Instantiate stack full of 52 standard cards and empty discard pile.

        stack = generator.generateDeck();
        shuffle();
    }

    public C pullCard() {
        // Give new card from stack pile to a player
        // Assume that player count is fine

        C card = stack.getLast();
        stack.removeLast();
        return card;
    }

    public C peekCard() {
        // Look at the next card to be pulled without taking them out

        return stack.getLast();
    }

    public C pullCardFromDiscard() {
        // Pull one card from the discard pile

        return this.pullCardsFromDiscard(1).get(0);
    }

    public ArrayList<C> pullCardsFromDiscard(int amount) throws IllegalArgumentException {
        // Pull an amount of cards from the discard pile

        if (amount > cardsInDiscard()) {
            throw new IllegalArgumentException("Attempted to pull " + amount + " cards from discard, while there are only " + this.cardsInDiscard() + " cards in the discard");
        }

        // Pull amount of cards and return them
        ArrayList<C> pulled = new ArrayList<>();

        for (int i = 0; i <= amount; i++) {
            pulled.add(discard.getLast());
            discard.removeLast();
        }

        return pulled;
    }

    public int cardsInStack() {
        // Return how many cards are in the stack

        return stack.size();
    }

    public boolean stackIsEmpty() {
        // Check if the stack is empty

        return stack.isEmpty();
    }

    public int cardsInDiscard() {
        // Return how many cards are in the discard pile

        return discard.size();
    }

    public void discardCard(C card) {
        // Add card to discard pile

        discard.add(card);
    }

    public void discardCardArray(ArrayList<C> cards) {
        // Add card to discard pile

        discard.addAll(cards);
    }

    public void mergeShuffle() {
        // Merge cards from discard pile into stack, and shuffle.
        // TODO what would we want to do if we didn't shuffle?

        stack.addAll(discard);
        discard.clear();
        shuffle();
    }

    private void shuffle() {
        Collections.shuffle(stack);
    }
}

