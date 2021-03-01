package domino;


public class DominoGame {


    private static final int INITIAL_NUM_DOMINOES = 7;
    private Player[] players;
    private int turn = 0;
    private Boneyard boneyard;

    public DominoGame(Player human, Player computer) {
        players = new Player[]{human, computer};
        boneyard = new Boneyard();

        //deliver dominoes
        for (int i = 0; i < INITIAL_NUM_DOMINOES; i++){
            human.addDomino(boneyard.draw());
            computer.addDomino(boneyard.draw());
        }
    }


    public void changeTurn(){
        turn = (turn + 1) % 2;
    }

    public boolean isOver() {

        //The game ends when the boneyard is empty and either
        //- The current player places their last domino.
        //- Both players have taken a turn without placing a domino
        if (boneyard.isEmpty()){

            //no domino left
            if (players[turn].numDominoes() == 0) {
                return true;
            }

            if (!players[0].canPut(boneyard) && !players[1].canPut(boneyard)){
                return true;
            }
        }

        return false;
    }

    public Player getWinner() {

        if (players[0].getTotal() < players[1].getTotal()){
            return players[0];
        }else if (players[0].getTotal() > players[1].getTotal()){
            return players[1];
        }else{
            return null; //draw
        }
    }

    public Boneyard getBoneyard() {
        return boneyard;
    }

    @Override
    public String toString() {

        String message = "domino.Boneyard:\n" + "first row: " + boneyard.getFirstRow() + '\n';
        message += "second row: " + boneyard.getSecondRow() + '\n';

        return message;
    }
}
