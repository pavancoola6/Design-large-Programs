package ScrabbleGame.Project3.model;



public class Tile {
    private char letter;
    private int score;
    public Tile(char letter, int score) {
        this.letter = letter;
        this.score = score;
    }

    public char getLetter() {
        return letter;
    }

    public int getScore() {
        return score;
    }

}
