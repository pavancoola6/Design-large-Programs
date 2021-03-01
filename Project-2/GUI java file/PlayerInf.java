package domino;

import java.util.List;

public interface PlayerInf {

    public void addDomino(Domino domino);
    public int numDominoes();
    public String getName();
    public boolean canPut(Boneyard boneyard);
    public int getTotal();
    public List<Domino> getDominoes();

    public int getChosenRow();

    public int getChosenValue();
}
