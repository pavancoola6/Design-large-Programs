package domino;

import java.util.List;

/**
 * player interface
 */
public interface PlayerInf {
    /**
     * add domino to player
     * @param domino domino
     */
    public void addDomino(Domino domino);

    /**
     * get number of dominoes of this player
     * @return number of dominoes of this player
     */
    public int numDominoes();

    /**
     * get name of player
     * @return name of player
     */
    public String getName();

    /**
     * can put the domino to domino.Boneyard
     * @param boneyard a domino.Boneyard
     * @return true if can put the domino to domino.Boneyard
     */
    public boolean canPut(Boneyard boneyard);

    /**
     * get total values of this domino
     * @return total value
     */
    public int getTotal();

    /**
     * getter of dominoes
     * @return dominoes
     */
    public List<Domino> getDominoes();

    /**
     * getter method of chosenRow
     * @return chosenRow
     */
    public int getChosenRow();

    /**
     * getter method of chosenValue
     * @return chosenValue
     */
    public int getChosenValue();
}
