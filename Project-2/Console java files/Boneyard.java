package domino;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Boneyard {
    private static final int MAX_VALUE = 6;
    private List<Domino> dominoes;
    private int firstRow = -1;
    private int secondRow = -1;
    public Boneyard(){

        dominoes = new ArrayList<>();

        //create domino
        for (int first = 0; first <= MAX_VALUE; first++){
            for (int second = first; second <= MAX_VALUE; second++ ){
                dominoes.add(new Domino(first, second));
            }
        }

        //shuffle to create random positions
        Collections.shuffle(dominoes);
    }


    public Domino draw(){
        if (dominoes.size() > 0){
            return dominoes.remove(0);
        }
        return null;
    }



    public void putDomino(int end, int other, int row){
        if (end == row || end == 0 || row == 0){
            if (row == firstRow){
                firstRow = other;
            }else{
                secondRow = other;
            }
        }
    }


    public void putDomino(int end, int other){
        firstRow = end;
        secondRow = other;
    }


    public boolean canPutDomino(int end, int other, int row){
        return (end == row) || (end == 0 || row == 0);
    }


    public boolean canPutDomino(Domino domino){
        return canPutDomino(domino.getFirst(), domino.getSecond(), firstRow) ||
                canPutDomino(domino.getSecond(), domino.getFirst(), firstRow) ||
                canPutDomino(domino.getFirst(), domino.getSecond(), secondRow) ||
                canPutDomino(domino.getSecond(), domino.getFirst(), secondRow);

    }


    public boolean isEmpty(){
        return dominoes.isEmpty();
    }


    public int getFirstRow() {
        return firstRow;
    }


    public int getSecondRow() {
        return secondRow;
    }
}
