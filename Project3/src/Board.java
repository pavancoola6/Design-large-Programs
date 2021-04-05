package ScrabbleGame.Project3.model;


import ScrabbleGame.Project3.controller.MainGameLoop;

public class Board {

    //default size
    private static final int SIZE = 15;
    private int size = SIZE;
    private Square[][] squares;

    public Board() {
        squares = new Square[size][size];
    }
    public Board(int size) {
        this.size = size;
        squares = new Square[size][size];
    }


    public Board(Square[][] squares) {
        this.size = squares.length;
        this.squares = squares;
    }


    public boolean putTile(Tile tile, int x, int y) {

        //validate row, column
        if (isValid(x, y)) {
            squares[x][y].setTile(tile);
            return true;
        }

        return false;
    }

    public Tile getTile(int x, int y) {
        return squares[x][y].getTile();
    }

    //get square
    public Square getSquare(int x, int y) {
        return squares[x][y];
    }

    //check if row, column is valid
    public boolean isValid(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    //cell is empty
    public boolean isEmpty(int x, int y) {
        return squares[x][y].getTile() == null;
    }

    public int getScore(int x, int y) {
        if (squares[x][y] == null){
            return 1;//score at letter only
        }
        return squares[x][y].getScore();
    }

    //return string representing the board
    public String toString() {

        String message = "";

        //rows
        for (int x = 0; x < size; x++) {

            for (int y = 0; y < size; y++) {

                Square square = squares[x][y];
                if (square.getTile() != null) {
                    message += String.format("  %c", square.getTile().getLetter());
                }else {
                    message += String.format("%3s", square.getDisplay());
                }
            }

            message += "\n";
        }

        return message;
    }

    //get the size of board
    public int getSize() {
        return size;
    }


    public void setController(MainGameLoop controller){
        for (int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++){
                squares[i][j].setController(controller);
            }
        }
    }


    public void draw(){
        for (int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++){
                squares[i][j].draw();
            }
        }
    }
}

