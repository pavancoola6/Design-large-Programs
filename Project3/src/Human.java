package ScrabbleGame.Project3.model;


import ScrabbleGame.Project3.controller.MainGameLoop;
import ScrabbleGame.Project3.view.TileCanvas;

import java.util.ArrayList;
import java.util.List;


public class Human extends Player {

    //list of tile canvases
    private List<TileCanvas> tileCanvasList = new ArrayList<>();

    //chosen tile canvas
    private TileCanvas chosenTileCanvas;

    //chosen position
    private int chosenPosition;

    // constructor

    public Human() {
        super("Human");
    }

    // 7 tiles
    public void deliverTiles(List<Tile> tiles){
        this.tiles = tiles;

        int index = 0;
        for (Tile tile: tiles){
            tileCanvasList.add(new TileCanvas(tile, index));
            index++;
        }
    }

    public void draw(){
        for (int i = 0; i < tileCanvasList.size(); i++){
            tileCanvasList.get(i).draw();
        }
    }


    public List<TileCanvas> getTileCanvasList() {
        return tileCanvasList;
    }

    public void setController(MainGameLoop controller){
        for (int i = 0; i < tileCanvasList.size(); i++){
            tileCanvasList.get(i).setController(controller);
        }
    }

    //clear all highlight
    public void clearHighlight(){
        for (int i = 0; i < tileCanvasList.size(); i++){
            tileCanvasList.get(i).setHighlight(false);
        }
    }

    //getter method of chosenTileCanvas
    public TileCanvas getChosenTileCanvas() {
        return chosenTileCanvas;
    }

    //setter method of chosenTileCanvas
    public void setChosenTileCanvas(TileCanvas chosenTileCanvas) {
        this.chosenTileCanvas = chosenTileCanvas;
    }

    public int getChosenPosition() {
        return chosenPosition;
    }

    public void setChosenPosition(int chosenPosition) {
        this.chosenPosition = chosenPosition;
    }

}
