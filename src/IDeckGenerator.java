import java.util.ArrayList;

// ALMOST a factory strategy!
// Interface for deck generators; those that can generate an ArrayList of cards.
public interface IDeckGenerator<C extends Card> {
    ArrayList<C> generateDeck();
}
