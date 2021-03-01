package domino;


public class ComputerPlayer extends Player {


    public ComputerPlayer(){
        super("Computer");
    }


    public Domino chooseDomino(Boneyard boneyard){
        for (Domino domino: dominoes){
            if (boneyard.canPutDomino(domino)){

                //choose
                if (boneyard.canPutDomino(domino.getFirst(), domino.getSecond(), boneyard.getFirstRow())){

                    chosenRow = boneyard.getFirstRow();
                    chosenValue = domino.getFirst();
                }else if (boneyard.canPutDomino(domino.getFirst(), domino.getSecond(), boneyard.getSecondRow())){

                    chosenRow = boneyard.getSecondRow();
                    chosenValue = domino.getFirst();
                }else if (boneyard.canPutDomino(domino.getSecond(), domino.getFirst(), boneyard.getFirstRow())){

                    chosenRow = boneyard.getFirstRow();
                    chosenValue = domino.getSecond();
                }else if (boneyard.canPutDomino(domino.getSecond(), domino.getFirst(), boneyard.getSecondRow())){

                    chosenRow = boneyard.getSecondRow();
                    chosenValue = domino.getSecond();
                }

                return domino;
            }
        }
        return null;
    }

}
