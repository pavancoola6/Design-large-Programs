package domino;

import java.util.ArrayList;
import java.util.List;


public class Player implements PlayerInf{


    protected List<Domino> dominoes;
    private String name;
    protected int chosenRow;
    protected int chosenValue;

    public Player(String name){
        this.name = name;
        dominoes = new ArrayList<>();
    }


    public void addDomino(Domino domino){
        dominoes.add(domino);
    }

    public int numDominoes(){
        return dominoes.size();
    }

    public String getName() {
        return name;
    }

    public boolean canPut(Boneyard boneyard){

        for (Domino domino: dominoes){
            if (boneyard.canPutDomino(domino)){
                return true;
            }
        }
        return false;
    }

    public int getTotal(){

        int total = 0;
        for (Domino domino: dominoes){
            total += domino.getTotal();
        }
        return total;
    }


    public List<Domino> getDominoes() {
        return dominoes;
    }

    public int getChosenRow() {
        return chosenRow;
    }

    public int getChosenValue() {
        return chosenValue;
    }

    @Override
    public String toString() {
        String message = "name: " + name + '\n';

        message += "Dominoes: ";
        for (Domino domino: dominoes){
            message += domino + " ";
        }

        message += '\n';

        return message;
    }
}
