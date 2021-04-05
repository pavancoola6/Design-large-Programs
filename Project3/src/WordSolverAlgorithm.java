package ScrabbleGame.Project3;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import ScrabbleGame.Project3.model.Board;
import ScrabbleGame.Project3.model.Location;
import ScrabbleGame.Project3.model.Square;
import ScrabbleGame.Project3.model.Tile;
import ScrabbleGame.Project3.utility.GameLoader;
import ScrabbleGame.Project3.utility.Permutate;


public class WordSolverAlgorithm {

    //scrabble_tiles.txt
    private static final String TILES = "scrabble_tiles.txt";

    // a board
    private Board board;

    // dictionary
    private HashMap<String, String> dictionary;

    // loader
    private GameLoader loader = new GameLoader();

    //letter used
    private int[] lettersUsed = {0, 0, 0, 0, 0, 0};

    private ArrayList<String> letters = new ArrayList<String>();

    //available tiles
    private List<Tile> tiles;

    //input letters
    private String tray;

    //score found
    private int score = 0;

    //letter solved
    private String letterSolution = "";

    //locations of letters
    private ArrayList<Location> positionSolution = new ArrayList<Location>();

    //score by character
    private HashMap<Character, Integer> scores = new HashMap<>();

    //permutate
    private Permutate permutate;

    // constructor
    public WordSolverAlgorithm(HashMap<String, String> dictionary, List<Tile> tiles, Board board,
                               HashMap<Character, Integer> scores) {

        this.dictionary = dictionary;
        this.tiles = tiles;
        this.board = board;
        this.scores = scores;

        permutate = new Permutate(letters, lettersUsed);
    }


    // constructor
    public WordSolverAlgorithm(String dictionaryFilename) {

        // dictionary
        dictionary = loader.loadDictionary(dictionaryFilename);

        permutate = new Permutate(letters, lettersUsed);

        tiles = loader.loadTiles(TILES);

        //put as the score in hash
        for (Tile t : tiles) {
            scores.put(Character.toLowerCase(t.getLetter()), t.getScore());
            scores.put(Character.toUpperCase(t.getLetter()), t.getScore());
        }
    }

    public void doAlgorithm(boolean changeBoard) {

        ArrayList<Location> startLocations = this.findStartPositions();

        ArrayList<String> inputLetters = new ArrayList<String>();

        // process all seven input letters
        int whiteLoc = this.tray.indexOf("*");

        if (whiteLoc >= 0)
        {
            for (char whiteChar = 'a'; whiteChar <= 'z'; whiteChar = (char) ((int) whiteChar + 1))
            {
                String tmpStr = this.tray.replace('*', whiteChar);
                inputLetters.add(tmpStr);
            }
        } else {
            inputLetters.add(this.tray);
        }

        // solve
        for (int inputCtr = 0; inputCtr < inputLetters.size(); inputCtr++) {

            String inputLettersTmp = inputLetters.get(inputCtr);

            for (int idx = 0; idx < startLocations.size(); idx++) {

                //start position
                Location pos = startLocations.get(idx);

                for (int idxLetters = 0; idxLetters < 7; idxLetters++) {
                    String firstLetter = inputLettersTmp.substring(idxLetters, idxLetters + 1);
                    String otherLettrs = inputLettersTmp.substring(0, idxLetters)
                            + inputLettersTmp.substring(idxLetters + 1);

                    //find letter up, down, left, right
                    String up = this.findUp(pos);
                    String down = this.findDown(pos);
                    String left = this.findLeft(pos);
                    String right = this.findRight(pos);

                    String colWord = up + firstLetter + down;
                    String rowWord = left + firstLetter + right;

                    //column, row
                    boolean col = false;
                    boolean row = false;

                    if (dictionary.get(colWord) != null) {

                        if (dictionary.get(rowWord) != null) {

                            col = true;
                            row = true;

                            if (!(board.isEmpty(board.getSize() / 2, board.getSize() / 2))) {

                                int letterScore = scores.get(firstLetter.charAt(0));

                                if (board.getScore(pos.x, pos.y) <= 3)
                                {
                                    letterScore = letterScore * board.getScore(pos.x, pos.y);
                                }

                                int colWordScore = 0;
                                for (int i = 0; i < up.length(); i++) {
                                    colWordScore = colWordScore +  scores.get(up.charAt(i));
                                }
                                for (int i = 0; i < down.length(); i++) {
                                    colWordScore = colWordScore +  scores.get(down.charAt(i));
                                }
                                colWordScore = colWordScore + letterScore;

                                int rowWordScore = 0;
                                for (int i = 0; i < left.length(); i++) {
                                    rowWordScore = rowWordScore +  scores.get(left.charAt(i));
                                }
                                for (int i = 0; i < right.length(); i++) {
                                    rowWordScore = rowWordScore +  scores.get(right.charAt(i));
                                }
                                rowWordScore = rowWordScore + letterScore;

                                // when the new letter is on double-word or triple-word
                                if (board.getScore(pos.x, pos.y) >= 4) {
                                    colWordScore = colWordScore * (board.getScore(pos.x, pos.y) - 2);
                                    rowWordScore = rowWordScore * (board.getScore(pos.x, pos.y) - 2);
                                }

                                // blank is present
                                int scoreByWhite = 0;

                                if (whiteLoc >= 0) {
                                    String whiteLetter = inputLettersTmp.substring(whiteLoc, whiteLoc + 1);
                                    if (firstLetter.equals(whiteLetter)) {
                                        // the first letter used is white
                                        if (inputLettersTmp.indexOf(whiteLetter) == inputLettersTmp
                                                .lastIndexOf(whiteLetter)) {
                                            // notice: only one blank allowed
                                            scoreByWhite = 2 * letterScore; // the blank is used twice
                                            if (board.getScore(pos.x, pos.y) >= 4) {
                                                scoreByWhite = scoreByWhite
                                                        * (board.getScore(pos.x, pos.y) - 2);
                                            }
                                        }
                                    }
                                }

                                if (colWordScore + rowWordScore - scoreByWhite > this.score) {
                                    this.score = colWordScore + rowWordScore - scoreByWhite;
                                    this.letterSolution = firstLetter;
                                    this.positionSolution.clear();
                                    this.positionSolution.add(pos);
                                }
                            }
                        } else {
                            col = false;
                            row = true;
                        }
                    } else {
                        // vertical word is invalid
                        if (dictionary.get(rowWord) != null) {
                            col = true;
                            row = false;
                        } else {
                            // the new letter has two invalid words
                            // this move is invalid and cannot be turned into valid
                            continue;
                        }
                    }

                    // according to new word direction to make decisions
                    if (col == true) {
                        for (int moreLetter = 1; moreLetter <= 6; moreLetter++) {

                            for (int moreLetterUP = 0; moreLetterUP <= moreLetter; moreLetterUP++) {
                                ArrayList<Location> feasiblePosList = this.findPositionsColumn(pos,
                                        moreLetterUP, moreLetter - moreLetterUP);
                                if (feasiblePosList != null && feasiblePosList.size() > 0) {
                                    String existingStrUP = "";
                                    if (moreLetterUP == 0) {
                                        existingStrUP = up;
                                    } else {
                                        existingStrUP = this.findUp(feasiblePosList.get(0));
                                    }

                                    String existingStrDN = "";
                                    if (moreLetterUP == moreLetter) {
                                        existingStrDN = down;
                                    } else {
                                        existingStrDN = this
                                                .findDown(feasiblePosList.get(feasiblePosList.size() - 1));
                                    }

                                    letters.clear();

                                    for (int i = 0; i < 6; i++) {
                                        lettersUsed[i] = 0;
                                    }

                                    permutate.permutate(0, moreLetter, otherLettrs);

                                    for (int idxPerm = 0; idxPerm < letters.size(); idxPerm++) {

                                        int scoreRowWords = 0;
                                        int scoreColWord = 0;
                                        int scoreByWhite = 0;
                                        int scoreFullUse = 0;

                                        String currentSolLetters = "";
                                        ArrayList<Location> currentSolPositions = new ArrayList<Location>();

                                        ArrayList<String> rowWords = new ArrayList<String>();

                                        String selectedLetterStr = letters.get(idxPerm);

                                        int newWordColScoreMul = 1; // check whether new letter is on double-word or
                                        // triple-word
                                        String newWordCol = existingStrUP;
                                        for (int i = 0; i < existingStrUP.length(); i++) {
                                            scoreColWord = scoreColWord +  scores.get(existingStrUP.charAt(i));
                                        }

                                        int idxSelectedLetter = 0;
                                        int posX = feasiblePosList.get(0).x;
                                        int endX = feasiblePosList.get(feasiblePosList.size() - 1).x;
                                        if (posX > pos.x) {
                                            posX = pos.x;
                                        }
                                        if (endX < pos.x) {
                                            endX = pos.x;
                                        }

                                        boolean isEligible = true;
                                        while (posX <= endX) {
                                            if (board.isEmpty(posX, pos.y)) {
                                                String newPutLetter = "";
                                                if (posX == pos.x) {
                                                    newPutLetter = firstLetter;
                                                } else {
                                                    newPutLetter = selectedLetterStr.substring(idxSelectedLetter,
                                                            idxSelectedLetter + 1);
                                                    idxSelectedLetter = idxSelectedLetter + 1;
                                                }
                                                newWordCol = newWordCol + newPutLetter;

                                                Location tilePosTpm = new Location(posX, pos.y);
                                                String ltStrTmp = this.findLeft(tilePosTpm);
                                                String rtStrTmp = this.findRight(tilePosTpm);
                                                String rowWordTmp = ltStrTmp + newPutLetter + rtStrTmp;
                                                if (dictionary.get(rowWordTmp) != null) {
                                                    currentSolLetters = currentSolLetters + newPutLetter;
                                                    currentSolPositions.add(tilePosTpm);

                                                    // value increase
                                                    int score_Letter = 0;
                                                    if (board.getScore(tilePosTpm.x, tilePosTpm.y) == 4
                                                            || (board.getScore(tilePosTpm.x, tilePosTpm.y) == 5)) {
                                                        score_Letter =  scores.get(newPutLetter.charAt(0));
                                                        if ((board.getScore(tilePosTpm.x, tilePosTpm.y)
                                                                - 2) > newWordColScoreMul) {
                                                            newWordColScoreMul = board.getScore(tilePosTpm.x, tilePosTpm.y)
                                                                    - 2;
                                                        }
                                                    } else {
                                                        score_Letter =  scores.get(newPutLetter.charAt(0))
                                                                * board.getScore(tilePosTpm.x, tilePosTpm.y);
                                                    }
                                                    scoreColWord = scoreColWord + score_Letter;

                                                    if (rowWordTmp.length() >= 2) {
                                                        rowWords.add(rowWordTmp); // store the word

                                                        int scoreRowWordTmp = 0;
                                                        for (int i = 0; i < ltStrTmp.length(); i++) {
                                                            scoreRowWordTmp = scoreRowWordTmp
                                                                    +  scores.get(ltStrTmp.charAt(i));
                                                        }
                                                        for (int i = 0; i < rtStrTmp.length(); i++) {
                                                            scoreRowWordTmp = scoreRowWordTmp
                                                                    +  scores.get(rtStrTmp.charAt(i));
                                                        }
                                                        scoreRowWordTmp = scoreRowWordTmp + score_Letter;

                                                        if ((board.getScore(tilePosTpm.x, tilePosTpm.y) == 4)
                                                                || (board.getScore(tilePosTpm.x, tilePosTpm.y) == 5)) {
                                                            scoreRowWordTmp = scoreRowWordTmp
                                                                    * (board.getScore(tilePosTpm.x, tilePosTpm.y) - 2);
                                                        }

                                                        scoreRowWords = scoreRowWords + scoreRowWordTmp;
                                                    } else {
                                                        rowWords.add("");
                                                    }

                                                } else {
                                                    isEligible = false;
                                                    break;
                                                }

                                            } else {
                                                newWordCol = newWordCol
                                                        + board.getTile(posX, pos.y).getLetter();

                                                //System.out.println(board
                                                //		.getTile(posX, pos.y).getLetter());

                                                scoreColWord = scoreColWord +  scores.get(board
                                                        .getTile(posX, pos.y).getLetter());
                                            }

                                            posX = posX + 1;
                                        }

                                        if (isEligible == true) {
                                            newWordCol = newWordCol + existingStrDN;
                                            for (int i = 0; i < existingStrDN.length(); i++) {
                                                scoreColWord = scoreColWord
                                                        +  scores.get(existingStrDN.charAt(i));
                                            }

                                            if (dictionary.get(newWordCol) != null) {

                                                scoreColWord = scoreColWord * newWordColScoreMul;
                                                if (whiteLoc >= 0) {
                                                    String whiteLetter = inputLettersTmp.substring(whiteLoc,
                                                            whiteLoc + 1);
                                                    int ctrSameWithWthite = 0;
                                                    for (int i = 0; i < inputLettersTmp.length(); i++) {
                                                        if (inputLettersTmp.substring(i, i + 1).equals(whiteLetter)) {
                                                            ctrSameWithWthite = ctrSameWithWthite + 1;
                                                        }
                                                    }

                                                    ctrSameWithWthite = ctrSameWithWthite - 1;
                                                    int ctrSameWithWthite_Sol = 0;
                                                    for (int i = 0; i < currentSolLetters.length(); i++) {
                                                        if (currentSolLetters.substring(i, i + 1).equals(whiteLetter)) {
                                                            ctrSameWithWthite_Sol = ctrSameWithWthite_Sol + 1;
                                                        }
                                                    }

                                                    if (ctrSameWithWthite_Sol > ctrSameWithWthite) {

                                                        int minScoreFromWhite = Integer.MAX_VALUE;

                                                        for (int i = 0; i < currentSolLetters.length(); i++) {
                                                            if (currentSolLetters.substring(i, i + 1)
                                                                    .equals(whiteLetter)) {
                                                                int rowScoreWhite = 0;
                                                                int colScoreWhite = 0;

                                                                Location tmpPosition = currentSolPositions.get(i);

                                                                // letter score
                                                                int scoreWhite_letter = 0;
                                                                if (board.getScore(tmpPosition.x, tmpPosition.y) <= 3) {

                                                                    scoreWhite_letter =  scores.get(whiteLetter
                                                                            .charAt(0))
                                                                            * board.getScore(tmpPosition.x, tmpPosition.y);
                                                                } else {
                                                                    scoreWhite_letter =  scores.get(whiteLetter
                                                                            .charAt(0));
                                                                }

                                                                if (rowWords.get(i).length() >= 2) {
                                                                    if (board.getScore(tmpPosition.x, tmpPosition.y) <= 3) {
                                                                        rowScoreWhite = scoreWhite_letter;
                                                                    } else {
                                                                        rowScoreWhite = scoreWhite_letter
                                                                                * (board.getScore(tmpPosition.x, tmpPosition.y)
                                                                                - 2);
                                                                    }
                                                                }

                                                                colScoreWhite = scoreWhite_letter * newWordColScoreMul;

                                                                if ((rowScoreWhite
                                                                        + colScoreWhite) < minScoreFromWhite) {
                                                                    minScoreFromWhite = rowScoreWhite + colScoreWhite;
                                                                }
                                                            }

                                                        }

                                                        scoreByWhite = minScoreFromWhite;
                                                    }
                                                }

                                                // BINGO
                                                if (currentSolLetters.length() == 7) {
                                                    scoreFullUse = 50;
                                                }

                                                if (scoreFullUse + scoreColWord + scoreRowWords
                                                        - scoreByWhite > this.score) {
                                                    this.score = scoreFullUse + scoreColWord + scoreRowWords
                                                            - scoreByWhite;
                                                    this.letterSolution = currentSolLetters;
                                                    this.positionSolution = currentSolPositions;
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }

                    if (row == true)
                    {
                        for (int moreLetter = 1; moreLetter <= 6; moreLetter++) {
                            for (int moreLetterLT = 0; moreLetterLT <= moreLetter; moreLetterLT++) {
                                ArrayList<Location> feasiblePosList = this.findPositionsRow(pos,
                                        moreLetterLT, moreLetter - moreLetterLT);
                                if (feasiblePosList != null && feasiblePosList.size() > 0) {
                                    String existingStrLT = "";
                                    if (moreLetterLT == 0) {
                                        existingStrLT = left;
                                    } else {
                                        existingStrLT = this.findLeft(feasiblePosList.get(0));
                                    }

                                    String existingStrRT = "";
                                    if (moreLetterLT == moreLetter) {
                                        existingStrRT = right;
                                    } else {
                                        existingStrRT = this
                                                .findRight(feasiblePosList.get(feasiblePosList.size() - 1));
                                    }

                                    letters.clear();

                                    for (int i = 0; i < 6; i++) {
                                        lettersUsed[i] = 0;
                                    }

                                    permutate.permutate(0, moreLetter, otherLettrs);

                                    for (int idxPerm = 0; idxPerm < letters.size(); idxPerm++) {

                                        int scoreColWords = 0;
                                        int scoreRowWord = 0;
                                        int scoreByWhite = 0;
                                        int scoreFullUse = 0;

                                        String currentSolLetters = "";
                                        ArrayList<Location> currentSolPositions = new ArrayList<Location>();

                                        ArrayList<String> colWords = new ArrayList<String>();

                                        String selectedLetterStr = letters.get(idxPerm);

                                        int newWordRowScoreMul = 1;

                                        String newWordRow = existingStrLT;
                                        for (int i = 0; i < existingStrLT.length(); i++) {
                                            scoreRowWord = scoreRowWord +  scores.get(existingStrLT.charAt(i));
                                        }

                                        int idxSelectedLetter = 0;
                                        int posY = feasiblePosList.get(0).y;
                                        int endY = feasiblePosList.get(feasiblePosList.size() - 1).y;
                                        if (posY > pos.y) {
                                            posY = pos.y;
                                        }
                                        if (endY < pos.y) {
                                            endY = pos.y;
                                        }

                                        boolean isEligible = true;
                                        while (posY <= endY) {
                                            if (board.isEmpty(pos.x, posY)) {
                                                String newPutLetter = "";
                                                if (posY == pos.y) {
                                                    newPutLetter = firstLetter;
                                                } else {
                                                    newPutLetter = selectedLetterStr.substring(idxSelectedLetter,
                                                            idxSelectedLetter + 1);
                                                    idxSelectedLetter = idxSelectedLetter + 1;
                                                }
                                                newWordRow = newWordRow + newPutLetter;

                                                Location tilePosTpm = new Location(pos.x, posY);

                                                String upStrTmp = this.findUp(tilePosTpm);
                                                String dnStrTmp = this.findDown(tilePosTpm);
                                                String colWordTmp = upStrTmp + newPutLetter + dnStrTmp;

                                                if (dictionary.get(colWordTmp) != null) {
                                                    currentSolLetters = currentSolLetters + newPutLetter;
                                                    currentSolPositions.add(tilePosTpm);

                                                    int letterScore = 0;
                                                    if ((board.getScore(tilePosTpm.x, tilePosTpm.y) == 4)
                                                            || (board.getScore(tilePosTpm.x, tilePosTpm.y) == 5)) {
                                                        letterScore =  scores.get(newPutLetter.charAt(0));
                                                        if ((board.getScore(tilePosTpm.x, tilePosTpm.y)
                                                                - 2) > newWordRowScoreMul) {
                                                            newWordRowScoreMul = board.getScore(tilePosTpm.x, tilePosTpm.y)
                                                                    - 2;
                                                        }
                                                    } else {

                                                        letterScore =  scores.get(newPutLetter.charAt(0))
                                                                * board.getScore(tilePosTpm.x, tilePosTpm.y);
                                                    }
                                                    scoreRowWord = scoreRowWord + letterScore;

                                                    if (colWordTmp.length() >= 2) {
                                                        colWords.add(colWordTmp);

                                                        int scoreColWordTmp = 0;
                                                        for (int i = 0; i < upStrTmp.length(); i++) {
                                                            scoreColWordTmp = scoreColWordTmp
                                                                    +  scores.get(upStrTmp.charAt(i));
                                                        }
                                                        for (int i = 0; i < dnStrTmp.length(); i++) {
                                                            scoreColWordTmp = scoreColWordTmp
                                                                    +  scores.get(dnStrTmp.charAt(i));
                                                        }
                                                        scoreColWordTmp = scoreColWordTmp + letterScore;

                                                        if ((board.getScore(tilePosTpm.x, tilePosTpm.y) == 4)
                                                                || (board.getScore(tilePosTpm.x, tilePosTpm.y) == 5)) {
                                                            scoreColWordTmp = scoreColWordTmp
                                                                    * (board.getScore(tilePosTpm.x, tilePosTpm.y) - 2);
                                                        }

                                                        scoreColWords = scoreColWords + scoreColWordTmp;
                                                    } else {
                                                        colWords.add("");
                                                    }

                                                } else {
                                                    isEligible = false;
                                                    break;
                                                }

                                            } else {
                                                newWordRow = newWordRow
                                                        + board.getTile(pos.x, posY).getLetter();

                                                scoreRowWord = scoreRowWord
                                                        +  scores.get(board.getTile(pos.x, posY).getLetter());
                                            }

                                            posY = posY + 1;
                                        }

                                        if (isEligible == true) {
                                            newWordRow = newWordRow + existingStrRT;
                                            for (int i = 0; i < existingStrRT.length(); i++) {
                                                scoreRowWord = scoreRowWord
                                                        +  scores.get(existingStrRT.charAt(i));
                                            }

                                            if (dictionary.get(newWordRow) != null) {
                                                scoreRowWord = scoreRowWord * newWordRowScoreMul;

                                                if (whiteLoc >= 0) {
                                                    String whiteLetter = inputLettersTmp.substring(whiteLoc,
                                                            whiteLoc + 1);

                                                    int ctrSameWithWhite = 0;
                                                    for (int i = 0; i < inputLettersTmp.length(); i++) {
                                                        if (inputLettersTmp.substring(i, i + 1).equals(whiteLetter)) {
                                                            ctrSameWithWhite = ctrSameWithWhite + 1;
                                                        }
                                                    }

                                                    ctrSameWithWhite = ctrSameWithWhite - 1;

                                                    int ctrSameWithWthite_Sol = 0;
                                                    for (int i = 0; i < currentSolLetters.length(); i++) {
                                                        if (currentSolLetters.substring(i, i + 1).equals(whiteLetter)) {
                                                            ctrSameWithWthite_Sol = ctrSameWithWthite_Sol + 1;
                                                        }
                                                    }

                                                    if (ctrSameWithWthite_Sol > ctrSameWithWhite) {

                                                        int minScoreFromWhite = Integer.MAX_VALUE;

                                                        for (int i = 0; i < currentSolLetters.length(); i++) {

                                                            if (currentSolLetters.substring(i, i + 1)
                                                                    .equals(whiteLetter)) {
                                                                int rowScoreWhite = 0;
                                                                int colScoreWhite = 0;

                                                                Location tmpPosition = currentSolPositions.get(i);

                                                                int scoreWhite_letter = 0;
                                                                if (board.getScore(tmpPosition.x, tmpPosition.y) <= 3) {
                                                                    scoreWhite_letter =  scores.get(whiteLetter
                                                                            .charAt(0))
                                                                            * board.getScore(tmpPosition.x, tmpPosition.y);
                                                                } else {
                                                                    scoreWhite_letter =  scores.get(whiteLetter
                                                                            .charAt(0));
                                                                }

                                                                if (colWords.get(i).length() >= 2) {
                                                                    if (board.getScore(tmpPosition.x, tmpPosition.y) <= 3) {
                                                                        rowScoreWhite = scoreWhite_letter;
                                                                    } else {
                                                                        rowScoreWhite = scoreWhite_letter
                                                                                * (board.getScore(tmpPosition.x, tmpPosition.y)
                                                                                - 2);
                                                                    }
                                                                }

                                                                colScoreWhite = scoreWhite_letter * newWordRowScoreMul;

                                                                if ((rowScoreWhite
                                                                        + colScoreWhite) < minScoreFromWhite) {
                                                                    minScoreFromWhite = rowScoreWhite + colScoreWhite;
                                                                }
                                                            }

                                                        }

                                                        scoreByWhite = minScoreFromWhite;
                                                    }
                                                }

                                                if (currentSolLetters.length() == 7) {
                                                    scoreFullUse = 50;
                                                }

                                                if (scoreFullUse + scoreRowWord + scoreColWords
                                                        - scoreByWhite > this.score) {
                                                    this.score = scoreFullUse + scoreRowWord + scoreColWords
                                                            - scoreByWhite;
                                                    this.letterSolution = currentSolLetters;
                                                    this.positionSolution = currentSolPositions;
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }

                }
            }
        }

        //put to board
        for (int i = 0; i < this.letterSolution.length(); i++) {

            Location tilePos = this.positionSolution.get(i);

            char letter = this.letterSolution.substring(i, i + 1).charAt(0);

            if (changeBoard) {
                board.putTile(new Tile(letter, scores.get(letter)),
                        tilePos.x, tilePos.y);
            }
        }
    }

    // find start positions
    private ArrayList<Location> findStartPositions() {

        // map of locations
        HashMap<String, Location> locMap = new HashMap<String, Location>();

        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                if (board.isEmpty(x, y)) {
                    if ((x - 1 >= 0 && !(board.isEmpty(x - 1, y)))
                            || (x + 1 < board.getSize() && !(board.isEmpty(x + 1, y)))
                            || (y - 1 >= 0 && !(board.isEmpty(x, y - 1)))
                            || (y + 1 < board.getSize() && !(board.isEmpty(x, y + 1)))) {
                        locMap.put(x + "," + y, new Location(x, y));
                    }
                }
            }
        }

        // put at center if the board is empty
        ArrayList<Location> rst = new ArrayList<Location>();

        if (locMap.isEmpty()) // an empty board
        {
            rst.add(new Location(board.getSize() / 2, board.getSize() / 2));
        } else {
            rst.addAll(locMap.values());
        }

        return rst;
    }

    // find the word immediately above a position
    private String findUp(Location tilePos) {

        String rst = "";

        int x = tilePos.x - 1;
        while (x >= 0 && !(board.isEmpty(x, tilePos.y))) {
            rst = board.getTile(x, tilePos.y).getLetter() + rst;
            x = x - 1;
        }
        return rst;
    }

    //find the word immediately below a position
    private String findDown(Location tilePos) {

        String rst = "";

        int x = tilePos.x + 1;
        while (x < board.getSize() && !(board.isEmpty(x, tilePos.y))) {
            rst = rst + board.getTile(x, tilePos.y).getLetter();
            x = x + 1;
        }
        return rst;
    }

    // find the word immediately to the left of a position
    private String findLeft(Location tilePos) {

        String rst = "";

        int y = tilePos.y - 1;
        while (y >= 0 && !(board.isEmpty(tilePos.x, y))) {
            rst = board.getTile(tilePos.x, y).getLetter() + rst;
            y = y - 1;
        }
        return rst;
    }

    //find the word immediately to the right of a position
    private String findRight(Location tilePos) {

        String rst = "";

        int y = tilePos.y + 1;
        while (y < board.getSize() && !(board.isEmpty(tilePos.x, y))) {
            rst = rst + board.getTile(tilePos.x, y).getLetter();
            y = y + 1;
        }
        return rst;
    }

    //find positions in a column
    private ArrayList<Location> findPositionsColumn(Location startTilePos,
                                                    int moreLetterUp, int moreLetterDown) {

        ArrayList<Location> rst = new ArrayList<Location>();

        int ctr = 0;
        int x = startTilePos.x - 1;
        while (x >= 0 && ctr < moreLetterUp) {
            if (board.isEmpty(x, startTilePos.y)) {
                rst.add(0, new Location(x, startTilePos.y));
                ctr = ctr + 1;
            }

            x = x - 1;
        }
        if (ctr < moreLetterUp) {
            // no enough space
            // return an empty arrayList
            rst.clear();
            return rst;
        }

        ctr = 0;
        x = startTilePos.x + 1;
        while (x < board.getSize() && ctr < moreLetterDown) {
            if (board.isEmpty(x, startTilePos.y)) {
                rst.add(new Location(x, startTilePos.y));
                ctr = ctr + 1;
            }
            x = x + 1;
        }
        if (ctr < moreLetterDown) {
            rst.clear();
            return rst;
        }

        return rst;
    }

    // find positions in a row
    private ArrayList<Location> findPositionsRow(Location startTilePos, int moreLetterLeft, int moreLetterRight) {

        ArrayList<Location> locations = new ArrayList<Location>();

        int ctr = 0;
        int y = startTilePos.y - 1;

        while (y >= 0 && ctr < moreLetterLeft) {
            if (board.isEmpty(startTilePos.x, y)) {
                locations.add(0, new Location(startTilePos.x, y));
                ctr = ctr + 1;
            }

            y = y - 1;
        }
        if (ctr < moreLetterLeft) {
            locations.clear();
            return locations;
        }

        ctr = 0;
        y = startTilePos.y + 1;

        while (y < board.getSize() && ctr < moreLetterRight) {
            if (board.isEmpty(startTilePos.x, y)) {
                locations.add(new Location(startTilePos.x, y));
                ctr = ctr + 1;
            }

            y = y + 1;
        }

        if (ctr < moreLetterRight) {
            locations.clear();
            return locations;
        }

        return locations;
    }

    //solve the problem
    public void solve(String tray){

        this.tray = tray;

        //solve
        doAlgorithm(false);
    }

    // solve
    public void solve() throws Exception {

        Scanner keyboard = new Scanner(System.in);

        // solve many cases
        while (keyboard.hasNext()) {

            // board
            Square[][] squares = loader.loadSquares(keyboard);

            // board
            board = new Board(squares);

            // tray
            tray = keyboard.next();

            // solve and output
            System.out.println("Input Board:");
            System.out.println(board);

            //solve
            doAlgorithm(true);

            System.out.println("Tray: " + tray);

            System.out.println("Solution has " + score + " points");

            System.out.println("Solution Board:");
            System.out.println(board);

            //reset
            lettersUsed = new int[]{ 0, 0, 0, 0, 0, 0 };
            letters = new ArrayList<String>();
            score = 0;
            letterSolution = "";
            positionSolution = new ArrayList<Location>();
            permutate = new Permutate(letters, lettersUsed);
        }
    }

    public int getScore() {
        return score;
    }

    public ArrayList<Location> getPositionSolution() {
        return positionSolution;
    }

    public String getLetterSolution() {
        return letterSolution;
    }
}