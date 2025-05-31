
public class Main {
    public static void main(String[] args) {

        // Sanity check: make sure that the DeckManager can list 52 cards.
        /*DeckManager dm = new DeckManager(new DeckGenerator52());

        while (!dm.stackIsEmpty()) {
            System.out.println(dm.cardsInStack() + " - " + dm.cardsInDiscard() + " - " + dm.stackIsEmpty() + " - " + dm.peekCard());
            dm.discardCard(dm.pullCard());
        }*/

        // Sanity check: make sure that a blackjack game can start and be played
        Blackjack bj = new Blackjack(new DeckGenerator156(), 500);
        while (bj.getPlayerBalance() >= 20) {
            bj.placeBet(20);

            System.out.println(bj.valueOfHand(bj.getPlayerHand()) + " | " + bj.getPlayerBalance() + " | " + bj.getPlayerHand() + " | " + bj.getGameState());

            // Play with dealer rules
            while (!bj.isGameOver()) {
                if (bj.valueOfHand(bj.getPlayerHand()) <= 16) {
                    bj.hit();
                }
                else {
                    bj.stand();
                }
            }

            System.out.println(bj.valueOfHand(bj.getPlayerHand()) + " | " + bj.getPlayerBalance() + " | " + bj.getPlayerHand() + " | " + bj.getGameState());

            System.out.println("--- --- --- --- ---");
            bj.newRound();
        }
    }
}