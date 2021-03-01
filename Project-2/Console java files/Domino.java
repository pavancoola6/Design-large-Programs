package domino;


public class Domino {


    private int[] values;

    public Domino(int first, int second){
        values = new int[]{first, second};
    }
    public int getFirst(){
        return values[0];
    }
    public int getSecond(){
        return values[1];
    }

    public int getTotal(){

        //special case: 0 - 0
        if (values[0] == values[1] && values[0] == 0){
            return 12;
        }

        return values[0] + values[1];
    }

    @Override
    public String toString() {
        return values[0] + "-" + values[1];
    }
}
