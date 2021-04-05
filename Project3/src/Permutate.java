package ScrabbleGame.Project3.utility;
import java.util.ArrayList;

public class Permutate {
    private int[] permutation = { 1, 2, 3, 4, 5, 6 };
    private int[] permute_r = { 1, 2, 3, 4, 5, 6 };

    private ArrayList<String> letters;
    private int[] used;

    //constructor
    public Permutate(ArrayList<String> letters, int[] used) {
        this.letters = letters;
        this.used = used;
    }

    //find the permutation
    public void permutate(int position, int currentPos, String others) {

        //current position
        if (position == currentPos) {

            String selectedLettersStr = "";
            for (int i = 0; i < currentPos; i++) {
                selectedLettersStr = selectedLettersStr + others.substring(permute_r[i] - 1, permute_r[i]);
            }
            letters.add(selectedLettersStr);
        }

        for (int i = 0; i < 6; i++) {
            if (used[i] == 0) {
                permute_r[position] = permutation[i];
                used[i]++;
                permutate(position + 1, currentPos, others);
                used[i]--;
            }
        }
    }
}
