
public class Main {
    public static void main(String[] args) {

        // Generalised test: make sure that the DeckManager can list 52 cards.
        DeckManager dm = new DeckManager(new DeckGenerator52());

        while (!dm.stackIsEmpty()) {
            System.out.println(dm.cardsInStack() + " - " + dm.cardsInDiscard() + " - " + dm.stackIsEmpty() + " - " + dm.peekCard());
            dm.discardCard(dm.pullCard());
        }

    }
}