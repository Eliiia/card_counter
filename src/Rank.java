public enum Rank {
    ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING;

    public String toString() {
        String n = this.name();
        return n.substring(0,1).toUpperCase() + n.substring(1).toLowerCase();
    }
}
