package domino;

public class ConsoleDominoGame {


    public void run(){

        //two players
        ConsoleHumanPlayer human = new ConsoleHumanPlayer();
        ComputerPlayer computer = new ComputerPlayer();

        //create game
        DominoGame game = new DominoGame(human, computer);

        //human plays
        Domino domino = human.chooseDominoFromList(game.getBoneyard());
        game.getBoneyard().putDomino(domino.getFirst(), domino.getSecond());
        human.getDominoes().remove(domino);

        System.out.println(human);
        System.out.println(game);

        game.changeTurn();

        //run until game is over
        while (!game.isOver()){

            //computer play
            computerPlay(computer, human, game);

            //game is over?
            if (game.isOver()){
                break;
            }

            System.out.println(game);
            System.out.println(human);

            if (human.numDominoes() != 0){

                boolean done = false;
                while (!done) {

                    //can choose domino
                    if (human.canPut(game.getBoneyard())) {

                        while(true) {

                            domino = human.chooseDomino(game.getBoneyard());

                            if (game.getBoneyard().canPutDomino(domino)) {

                                int otherValue = domino.getFirst();
                                if (human.getChosenValue() == otherValue) {
                                    otherValue = domino.getSecond();
                                }
                                game.getBoneyard().putDomino(human.getChosenValue(), otherValue, human.getChosenRow());
                                human.getDominoes().remove(domino);

                                System.out.println("Human put the domino " + domino);

                                done = true;

                                break;

                            }else{
                                System.out.println("Could not put that domino");
                            }
                        }

                    } else { //can not put domino
                        if (game.getBoneyard().isEmpty()){
                            break;
                        }else{

                            domino = game.getBoneyard().draw();
                            human.addDomino(domino);

                            System.out.println("Human drew one domino from boneyard and added the domino " + domino);

                        }
                    }
                }
            }

            System.out.println(human);

            game.changeTurn();
            prepareForComputerTurn(computer, game);
        }

        //process if game is over
        gameOver(computer, human, game);
    }

    private void computerPlay(ComputerPlayer computer, ConsoleHumanPlayer human, DominoGame game){

        System.out.println(computer);
        System.out.println(game);

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

                    System.out.println("Computer put the domino " + domino);

                    done = true;

                    break;

                } else { //can not put domino
                    if (game.getBoneyard().isEmpty()){
                        break;
                    }else{

                        Domino domino = game.getBoneyard().draw();
                        computer.addDomino(domino);

                        System.out.println("Computer drew one domino from boneyard and added the domino " + domino);

                    }
                }
            }
        }

        System.out.println(computer);

        game.changeTurn();
        prepareForHumanTurn(human, game);

        //game is over?
        if (game.isOver()){
            gameOver(computer, human, game);
        }
    }


    private void gameOver(ComputerPlayer computer, ConsoleHumanPlayer human, DominoGame game) {

        System.out.println("Game over!");

        System.out.println(human);
        System.out.println(computer);

        //check who won?
        Player winner = game.getWinner();

        if (winner == null){
            System.out.println("Draw!");
        }else{
            System.out.println("Winner: " + winner.getName());
        }
    }


    private void prepareForComputerTurn(ComputerPlayer computer, DominoGame game){

        while (!computer.canPut(game.getBoneyard()) && !game.getBoneyard().isEmpty()){
            Domino domino = game.getBoneyard().draw();
            computer.addDomino(domino);
            System.out.println("Computer drew one domino from boneyard and added the domino " + domino);
        }
    }

    private void prepareForHumanTurn(ConsoleHumanPlayer human, DominoGame game){

        while (!human.canPut(game.getBoneyard()) && !game.getBoneyard().isEmpty()){
            Domino domino = game.getBoneyard().draw();
            human.addDomino(domino);
            System.out.println("Human drew one domino from boneyard and added the domino " + domino);
        }
    }
}
