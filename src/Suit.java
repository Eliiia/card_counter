public enum Suit {
    HEARTS, SPADES, CLUBS, DIAMONDS;

    public String toString() {
        String n = this.name();
        return n.substring(0,1).toUpperCase() + n.substring(1).toLowerCase();
    }
}
