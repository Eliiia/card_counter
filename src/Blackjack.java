import java.util.ArrayList;

public class Blackjack {

    // Blackjack is a game!!!

    // A deck manager, for providing a way to get cards in the first place
    DeckManager<StandardCard> dm;

    // Two arrays: for storing the player's and house cards
    private final ArrayList<StandardCard> playerHand = new ArrayList<>();
    private final ArrayList<StandardCard> houseHand = new ArrayList<>();

    // Player's balance
    private int currentBet = 0;
    private int playerBalance;

    // Game state
    public enum GameState {
        // Each GameState allows for only certain actions:
        WaitingForBet, // Do not allow anything other than a bet
        WaitingForFirstMove, // Allow double down, split (conditionally), hit, stand, insurance (conditionally), and surrender
        WaitingForNonFirstMove, // Allow hit or stand
        Win, Loss, Push, PlayerBlackjack, HouseBlackjack // For both win and loss states, only allow new round
        // Attempting to use an invalid one will result in a IllegalStateException.
    }
    private GameState gameState;

    public Blackjack(IDeckGenerator<StandardCard> generator, int balance) {
        dm = new DeckManager<>(generator);
        playerBalance = balance;
        gameState = GameState.Win; // So that newRound() function does not complain.
        newRound();
    }

    public ArrayList<StandardCard> getHouseHand() {
        return houseHand;
    }
    public ArrayList<StandardCard> getPlayerHand() {
        return playerHand;
    }

    public GameState getGameState() {
        return gameState;
    }
    public boolean isGameOver() {
        // Return whether the game is over, and needs to be restarted
        return gameState == GameState.Win || gameState == GameState.Loss
                || gameState == GameState.Push
                || gameState == GameState.PlayerBlackjack
                || gameState == GameState.HouseBlackjack;
    }
    private void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void newRound() throws IllegalStateException {
        // Check for valid state
        if (!isGameOver()) {
            throw new IllegalStateException("Expected Win, Loss, Push, PlayerBlackjack or HouseBlackjack state, currently in " + gameState + " state");
        }

        // Reset bet
        currentBet = 0;

        // Reset hands
        dm.discardCardArray(playerHand);
        dm.discardCardArray(houseHand);
        playerHand.clear();
        houseHand.clear();

        // Set the GameState so that the game can start
        setGameState(GameState.WaitingForBet);

        // Re-shuffle cards when there are less than 12 available
        if (dm.cardsInStack() < 12) {
            dm.mergeShuffle();
            // TODO remove this
            System.out.println("CARDS RE-SHUFFLED: " + (156-dm.cardsInStack()) + " CARDS HAVE BEEN LOST");
        }
    }

    public void placeBet(int bet) throws IllegalArgumentException, IllegalStateException {
        // Check for valid state
        if (gameState != GameState.WaitingForBet) {
            throw new IllegalStateException("Expected WaitingForBet state, currently in " + gameState + " state");
        }

        // Check whether bet valid (divisible by 10): this makes 3:2 payout possible
        // TODO: this is required because of how we implemented bets and balances as ints. do we want this to be the case?
        // TODO part 2: it is a result of 2.5x multiplier for blackjack wins.
        if (bet % 10 != 0) {
            throw new IllegalArgumentException("Bet value must be a multiple of 10");
        }

        // Apply bet
        if (playerBalance >= bet) {
            playerBalance -= bet;
            currentBet = bet;
        }
        else {
            throw new IllegalArgumentException("Attempted to bet " + bet + " while player only has " + playerBalance);
        }

        // Reset hands
        playerHand.add(dm.pullCard());
        playerHand.add(dm.pullCard());
        houseHand.add(dm.pullCard());

        // Check whether player hand is blackjack
        if (isBlackjackHand(playerHand)) {
            // If so, we have won: end the round.
            endRound();
            return;
        }

        // Set new game state
        setGameState(GameState.WaitingForFirstMove);
    }
    public int getPlayerBalance() {
        return playerBalance;
    }

    public void doubledown() throws IllegalStateException {}
    public void split() throws IllegalStateException {}
    public void hit() throws IllegalStateException {
        // Do the "hit" player action in blackjack

        // Check for valid state
        if (gameState != GameState.WaitingForFirstMove && gameState != GameState.WaitingForNonFirstMove) {
            throw new IllegalStateException("Expected WaitingForFirstMove or WaitingForNonFirstMove state, currently in " + gameState + " state");
        }

        // Pull a new card
        playerHand.add(dm.pullCard());
        // Check if this pulls hand over 21!
        if (valueOfHand(playerHand) > 21) {
            // Call endRound() to determine win or loss state
            endRound();
            return;
        }
        // Assuming it's a first move, check if this is a blackjack: if so, end round
        if (gameState == GameState.WaitingForFirstMove && isBlackjackHand(playerHand)) {
            endRound();
            return;
        }
        // If it does not pull hand over 21 or give a blackjack, continue with the game!
        // Set new game state (in case it was a WaitingForFirstMove)
        setGameState(GameState.WaitingForNonFirstMove);

    }
    public void stand() throws IllegalStateException {
        // Do the "stand" player action in blackjack

        // Check for valid state
        if (gameState != GameState.WaitingForFirstMove && gameState != GameState.WaitingForNonFirstMove) {
            throw new IllegalStateException("Expected WaitingForFirstMove or WaitingForNonFirstMove state, currently in " + gameState + " state");
        }

        // We do not need to perform any actions!
        // We can just end the game, see what happens when the dealer pulls everything!
        endRound();
    }
    public void insurance() throws IllegalStateException {

    }
    public void surrender() throws IllegalStateException {

    }

    private void endRound() throws IllegalStateException {
        // We have determined it is the end of the round, and we need to determine whether it is a win or a loss.
        // This should be called in any action that ends the round (e.g. hit when gone over 21, stand, doubledown, insurance, whatever)
        // We then perform actions based on whether it is a win or a loss.

        // Check for valid state
        if (gameState != GameState.WaitingForFirstMove && gameState != GameState.WaitingForNonFirstMove && gameState != GameState.WaitingForBet) {
            throw new IllegalStateException("Expected WaitingForFirstMove or WaitingForNonFirstMove state, currently in " + gameState + " state");
        }

        // Check whether player has two-card blackjack; wins (big) if so
        // We only do this if the dealer's card is not a ten or ace! otherwise, we need to check after the dealer pulls.
        if (cardValue(houseHand.getFirst()) < 10 && isBlackjackHand(playerHand)) {
            // Set new game state: player won with a blackjack.
            setGameState(GameState.PlayerBlackjack);
            payoutBet(2.5);
            return;
        }

        // Check whether player hand is over 21
        if (valueOfHand(playerHand) > 21) {
            // Set new game state: player lost.
            setGameState(GameState.Loss);
            payoutBet(0);
            return;
        }

        // Dealer pulls second card
        houseHand.add(dm.pullCard());

        // Check whether dealer has two-card blackjack; player loses if so
        if (isBlackjackHand(houseHand)) {
            // If player also has a blackjack:
            if (isBlackjackHand(playerHand)) {
                // Tie: called a push.
                // Set new game state: push.
                setGameState(GameState.Push);
                payoutBet(1); // Bet is returned
                return;
            }
            // If player does not have a blackjack:
            else {
                // Not a tie: player wins blackjack.
                // Set new game state: player won with a blackjack.
                setGameState(GameState.PlayerBlackjack);
                payoutBet(2.5);
                return;
            }
        }

        // If not, pull cards based on dealer rules
        while (valueOfHand(houseHand) <= 16) {
            // Keep pulling cards as long as the value of the hand is 16 or lower.
            houseHand.add(dm.pullCard());
        }

        // Decide on simple win/loss outcome
        if (valueOfHand(houseHand) > 21) {
            // If repeated pulling led to >21, the player wins
            setGameState(GameState.Win);
            payoutBet(2);
            return;
        }
        else {
            // Who got higher? We know neither got above 21, and neither got blackjack.
            // Option 1: player got higher, player wins.
            if (valueOfHand(playerHand) > valueOfHand(houseHand)) {
                setGameState(GameState.Win);
                payoutBet(2);
                return;
            }
            // Option 2: dealer got higher, player loses.
            else if (valueOfHand(playerHand) < valueOfHand(houseHand)) {
                setGameState(GameState.Loss);
                payoutBet(0);
                return;
            }
            // Option 3: both got the same, result in a push.
            else { // Only possible scenario left is (==)
                setGameState(GameState.Push);
                payoutBet(1);
                return;
            }
        }

    }
    private void payoutBet(double multiplier) {
        // 0x multiplier if bet is lost ($2 bet, $0 given back)
        // 1x multiplier if bet is returned ($2 bet, so $2 given back)
        // 2x multiplier if won 1:1 ($2 bet, $4 given back)
        // 2.5x multiplier if won 3:2 ($2 bet, $5 given back)

        playerBalance += (int) ((double) currentBet * multiplier);
    }

    public int valueOfHand(ArrayList<StandardCard> hand) {
        // Check the numerical value of a hand based on the values of the cards in the hand
        // Take aces as 11s unless this will lead to >21

        // Get total and count of aces
        int aceCount = 0;
        int total = 0;
        for (int i = 0; i < hand.size(); i++) {
            int cardVal = cardValue(hand.get(i));
            if (cardVal == 11) {
                aceCount++;
            }
            total += cardVal;
        }

        // Check if total is over 21: try to save it if so!
        // We can do this by treating the aces as 1s, rather than 11s: take away 10 for each ace
        while (total > 21 && aceCount > 0) {
            total -= 10; // take an ace from meaning 11 to meaning 1
            aceCount--;
        }

        // Whether we saved it or not, we return the total
        return total;
    }
    private boolean isBlackjackHand(ArrayList<StandardCard> hand) {
        // Check whether the hand is a blackjack: it is two cards and equal to 21.
        if (hand.size() == 2 && valueOfHand(hand) == 21) {
            return true;
        }
        else { return false; }
    }

    private int cardValue(StandardCard card) {
        return (switch (card.getRank()) {
            case ACE -> 11;
            case TWO -> 2;
            case THREE -> 3;
            case FOUR -> 4;
            case FIVE -> 5;
            case SIX -> 6;
            case SEVEN -> 7;
            case EIGHT -> 8;
            case NINE -> 9;
            case TEN, JACK, QUEEN, KING -> 10;
        });
    }

}
