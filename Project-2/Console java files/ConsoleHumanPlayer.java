package domino;

import java.util.Scanner;


public class ConsoleHumanPlayer extends Player {


    private Scanner keyboard = new Scanner(System.in);
    public ConsoleHumanPlayer(){
        super("Human");
    }
    public Domino chooseDominoFromList(Boneyard boneyard) {

        System.out.println("Dominoes:");
        for (int i = 0; i < dominoes.size(); i++) {
            System.out.println((i + 1) + ". " + dominoes.get(i) + " ");
        }

        //asking for domino
        System.out.print("Your selection? ");

        int index = readInt() - 1;
        while (index < 0 || index >= dominoes.size()) {
            System.out.print("Invalid selection. Your selection? ");
            index = readInt() - 1;
        }

        return dominoes.get(index);
    }


    public Domino chooseDomino(Boneyard boneyard){

        Domino domino = chooseDominoFromList(boneyard);

        //choose value
        System.out.print("Enter value: [" + domino.getFirst()+ "/" + domino.getSecond() + "] ");
        int value = readInt();
        while (value != domino.getFirst() && value != domino.getSecond()){
            System.out.print("Enter value: [" + domino.getFirst()+ "/" + domino.getSecond() + "] ");
            value = readInt();
        }

        chosenValue = value;

        //choose row
        System.out.print("Enter row: [" + boneyard.getFirstRow() + "/" + boneyard.getSecondRow() + "] ");
        int row = readInt();
        while (row !=  boneyard.getFirstRow() && row != boneyard.getSecondRow()){
            System.out.print("Enter row: [" + boneyard.getFirstRow() + "/" + boneyard.getSecondRow() + "] ");
            row = readInt();
        }

        chosenRow = row;

        return domino;
    }


    public int readInt(){

        int value;
        //read until user enters valid integer
        do{
            try{
                //convert line to integer
                value = Integer.parseInt(keyboard.nextLine());
                break;
            }catch(Exception e){
                System.out.println("Invalid input");
            }
        }while(true);
        return value;
    }


}
