package domino;



public class MainGameLoop {

    //two players
    private GUIHumanPlayer human = new GUIHumanPlayer();
    private ComputerPlayer computer = new ComputerPlayer();


    private boolean putFirstDomino = true;


    private Display display;

    //create game
    private DominoGame game = new DominoGame(human, computer);

    public MainGameLoop(Display display) {

        this.display = display;

        display.initialize(human);

        //set controller
        human.setController(this);
    }



    public void doClick(DominoDisplay dominoDisplay) {

        //System.out.println("Debug: " + dominoDisplay.getDomino() + " clicked");
        //game is over?
        if (game.isOver()){
            gameOver();
            return;
        }

        if (putFirstDomino){ //play the first time

            game.getBoneyard().putDomino(dominoDisplay.getDomino().getFirst(), dominoDisplay.getDomino().getSecond());
            human.removeDomino(dominoDisplay);

            putFirstDomino = false;

            human.draw();

        }else{

            display.addInformation(game.toString());
            display.addInformation(human.toString());

            if (human.numDominoes() != 0){

                boolean done = false;
                while (!done) {

                    //can choose domino
                    if (human.canPut(game.getBoneyard())) {

                        while(true) {

                            Domino domino = human.chooseDomino(game.getBoneyard(), dominoDisplay);

                            if (game.getBoneyard().canPutDomino(domino)) {

                                int otherValue = domino.getFirst();
                                if (human.getChosenValue() == otherValue) {
                                    otherValue = domino.getSecond();
                                }
                                game.getBoneyard().putDomino(human.getChosenValue(), otherValue, human.getChosenRow());
                                human.removeDomino(dominoDisplay);
                                human.draw();

                                display.addInformation("Human put the domino " + domino);

                                done = true;

                                break;

                            }else{
                                display.addInformation("Could not put the domino " + domino);
                                return; //cannot put that domino
                            }
                        }

                    } else { //can not put domino
                        if (game.getBoneyard().isEmpty()){
                            break;
                        }else{

                            Domino domino = game.getBoneyard().draw();
                            human.addDominoGUI(domino, this);

                            display.addInformation("Human drew one domino from boneyard and added the domino " + domino);

                            human.draw();

                            return;
                        }
                    }
                }
            }

            display.addInformation(human.toString());

        }

        display.addInformation(human.toString());
        display.addInformation(game.toString());

        //game is over?
        if (game.isOver()){
            gameOver();
            return;
        }


        game.changeTurn();
        prepareForComputerTurn();

        //computer play
        computerPlay();
    }


    private void prepareForComputerTurn(){

        while (!computer.canPut(game.getBoneyard()) && !game.getBoneyard().isEmpty()){
            Domino domino = game.getBoneyard().draw();
            computer.addDomino(domino);
            display.addInformation("Computer drew one domino from boneyard and added the domino " + domino);
        }
    }


    private void prepareForHumanTurn(){

        while (!human.canPut(game.getBoneyard()) && !game.getBoneyard().isEmpty()){
            Domino domino = game.getBoneyard().draw();
            human.addDominoGUI(domino, this);
            display.addInformation("Human drew one domino from boneyard and added the domino " + domino);
            human.draw();
        }
    }


    private void computerPlay(){

        if (computer.numDominoes() != 0){

            boolean done = false;
            while (!done) {

                //can choose domino
                if (computer.canPut(game.getBoneyard())) {

                    Domino domino = computer.chooseDomino(game.getBoneyard());

                    int otherValue = domino.getFirst();
                    if (computer.getChosenValue() == otherValue) {
                        otherValue = domino.getSecond();
                    }
                    game.getBoneyard().putDomino(computer.getChosenValue(), otherValue, computer.getChosenRow());
                    computer.getDominoes().remove(domino);

                    display.addInformation("Computer put the domino " + domino);

                    done = true;

                    break;

                } else { //can not put domino
                    if (game.getBoneyard().isEmpty()){
                        break;
                    }else{

                        Domino domino = game.getBoneyard().draw();
                        computer.addDomino(domino);

                        display.addInformation("Computer drew one domino from boneyard and added the domino " + domino);

                    }
                }
            }
        }

        display.addInformation(computer.toString());
        display.addInformation(game.toString());

        game.changeTurn();
        prepareForHumanTurn();

        //game is over?
        if (game.isOver()){
            gameOver();
            return;
        }
    }

    private void gameOver(){
        display.addInformation("Game over!");

        display.addInformation(human.toString());
        display.addInformation(computer.toString());

        //check who won?
        Player winner = game.getWinner();

        if (winner == null){
            display.addInformation("Draw!");
        }else{
            display.addInformation("Winner: " + winner.getName());
        }
    }
}
