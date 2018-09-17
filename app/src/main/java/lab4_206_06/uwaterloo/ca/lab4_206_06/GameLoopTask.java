package lab4_206_06.uwaterloo.ca.lab4_206_06;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Calvin on 2017-06-24.
 */

class GameLoopTask extends TimerTask {

    public enum GameDirection{LEFT, RIGHT, UP, DOWN, UNDETERMINED};

    public GameDirection currentGameDirection;

    private Activity myActivity;
    private Context myContext;
    private RelativeLayout myRelativeLayout;
    private GameBlock newBlock;
    private ArrayList<Integer> blockNumbers;
    private ArrayList<GameBlock> removeBlocks = new ArrayList<>();
    private LinkedList<GameBlock> gameBlockList;
    private int targetCoordX;
    private int targetCoordY;
    private int slotCount;
    private int blockCount;
    private int createBlock;
    private int emptySlot;
    private boolean endGame = false;
    private boolean win = false;
    public static final float SLOT_ISO = (float) (Resources.getSystem().getDisplayMetrics().widthPixels) * 0.25f;


    //Constructor creates a gameblock by calling the createBlock()
    public GameLoopTask(Activity myAct, RelativeLayout myRL, Context myCont){
        myActivity = myAct;
        myRelativeLayout = myRL;
        myContext = myCont;
        gameBlockList = new LinkedList<>();
        this.createBlock();
    }

    //Creates the gameblock in a random area where no block will be
    private void createBlock(){
        emptySlot = 0;
        Random random = new Random();

        //Array that will map the blocks on to the grid
        boolean [][] blockLocations =
                {{false, false, false, false},
                {false, false, false, false},
                {false, false, false, false},
                {false, false, false, false}};

        //If the there is a bloack on the coordinate then set the location to true
        for(GameBlock i: gameBlockList){
            blockLocations[i.getTargetCoordX()][i.getTargetCoordY()] = true;
        }

        //Counts the Number of empty spots
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                //If there is no bloack in the grid increment the number of empty slots
                if(blockLocations[i][j] == false){
                    emptySlot++;
                }
            }
        }

        //Create an array size of the empty slot which will hold the coordinates
        int[][] emptySpace = new int [2][emptySlot];
        //Generate random integer
        int randomSpot = random.nextInt(emptySlot);
        int indexOfEmptySpace = 0;

        //Stores the coordinates in the 2d array of the size of the empty slot
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(blockLocations[i][j] == false){
                    emptySpace[0][indexOfEmptySpace] = i;
                    emptySpace[1][indexOfEmptySpace] = j;
                    indexOfEmptySpace++;
                }
            }
        }

        //Game block creation from the gameblock class
        //Generates a random block coordinate in a spot where there is no blocks
        int generateRandomX = (int)((SLOT_ISO) * emptySpace[0][randomSpot]);
        int generateRandomY = (int)((SLOT_ISO) * emptySpace[1][randomSpot]);

        //Creates a new game Bloack object at the random generated coordinate
        newBlock = new GameBlock(myContext, generateRandomX, generateRandomY, myRelativeLayout);
        //Adds that block to a gamebloack list
        gameBlockList.add(newBlock);
    }



    //Sets the direction of the block in the gamebloack class and the variable in this class
    //Changing through the accelerometer handler
    public void setDirection(GameDirection gameDirection){
        currentGameDirection = gameDirection;
        blockNumbers = new ArrayList<>();

        //Iterate through each gameblock on the block
        for(GameBlock i: gameBlockList){
            //Keep track of each blocks, slot, block, and the block values in front of it
            slotCount = 0;
            blockCount = 0;
            blockNumbers.clear();

            //Each direction will cause a different look ahead algorithm
            if(currentGameDirection == GameLoopTask.GameDirection.LEFT){
                //Target target for x coord will be set to zero because it is the coordinate that is furthest to left
                //Target y target will be the current y coord
                targetCoordX = 0;
                targetCoordY = i.getYCoord();

                //start from the furthest left x coord 0 and add the bloack values from left to right until the current x coordinate
                for(int j = targetCoordX; j < i.getXCoord(); j++){
                    //If the location is occupied with a block then increment the block count by 1
                    //Then add the bloack value the the bloack numbers array
                    if(isOccupied(j, targetCoordY)){
                        blockCount++;
                        blockNumbers.add(getBlockValue(j, targetCoordY));
                    } else {
                        //If no block occupied add 0
                        blockNumbers.add(0);
                    }
                    //Keep a count of the number of slots in front
                    slotCount++;
                }

                //Add the current bloaks value
                blockNumbers.add(i.getBlockValue());

                //starts at the furthest point and compares non zero values ahead of it
                for(int j = 0; j < blockNumbers.size() - 1; j++){
                    if(blockNumbers.get(j) != 0){
                        //Looks that the values to comapre with the first value
                        for(int k = j + 1; k < blockNumbers.size(); k++){
                            if(blockNumbers.get(k) != 0){
                                //If the values are equal to each other set the block value of the bloack in front
                                //Then decrement blockCount and set out of for loop
                                //If the bloack getting compared is the cuurent bloack set ti to remove
                                if((blockNumbers.get(j)).equals(blockNumbers.get(k))){
                                    setBlockValue(j, targetCoordY, blockNumbers.get(j) * 2);
                                    if(i.getXCoord() == k){
                                        i.setRemoveBlock();
                                    }
                                    blockCount--;
                                    j = k;  //Look at the next value after the compared number to skip the nmerge number
                                    k = blockNumbers.size();
                                } else { //If there is another non- zero value the exit loop.
                                    k = blockNumbers.size();
                                }
                            }
                        }
                    }
                }
                targetCoordX = i.getXCoord() - (slotCount - blockCount);
                i.setTargetCoordX(targetCoordX);
                if (i.getRemoveElement()) {
                    removeBlocks.add(i);
                }
            } else if(currentGameDirection == GameLoopTask.GameDirection.RIGHT){ //Right Condition
                targetCoordX = 3;
                targetCoordY = i.getYCoord();

                //start from the furthest right x coord 3 and add the bloack values from right to left until the current x coordinate
                for(int j = targetCoordX; j > i.getXCoord(); j--){
                    //Target target for x coord will be set to 3 because it is the coordinate that is furthest to right
                    //Target y target will be the current y coord
                    if(isOccupied(j, targetCoordY)){
                        blockCount++;
                        blockNumbers.add(getBlockValue(j, targetCoordY));
                    } else {
                        blockNumbers.add(0);
                    }
                    //Keep a count of the number of slots in front
                    slotCount++;
                }
                blockNumbers.add(i.getBlockValue());

                //starts at the furthest point and compares non zero values ahead of it
                for(int j = 0; j < blockNumbers.size() - 1; j++){
                    if(blockNumbers.get(j) != 0){
                        //Looks that the values to comapre with the first value
                        for(int k = j + 1; k < blockNumbers.size(); k++){
                            if(blockNumbers.get(k) != 0){
                                //If the values are equal to each other set the block value of the bloack in front
                                //Then decrement blockCount and set out of for loop
                                //If the bloack getting compared is the cuurent bloack set ti to remove
                                if((blockNumbers.get(j)).equals(blockNumbers.get(k))){
                                    setBlockValue(3 - j, targetCoordY, blockNumbers.get(j)*2);
                                    if(i.getXCoord() == 3 - k){
                                        i.setRemoveBlock();
                                    }
                                    blockCount--;
                                    j = k;  //Look at the next value after the compared number to skip the nmerge number
                                    k = blockNumbers.size();
                                } else {  //If there is another non- zero value the exit loop.
                                    k = blockNumbers.size();
                                }
                            }
                        }
                    }
                }

                //Calculates and sets the target coordinate for the block
                targetCoordX = i.getXCoord() + (slotCount - blockCount);
                i.setTargetCoordX(targetCoordX);
                //Adds the merge bloack to the remove bloack list
                if (i.getRemoveElement()) {
                    removeBlocks.add(i);
                }
            } else if(currentGameDirection == GameLoopTask.GameDirection.UP){
                targetCoordX = i.getXCoord();
                targetCoordY = 0;

                //Target target for y coord will be set to 0 because it is the coordinate
                //Target x target will be the current y coord
                for(int j = targetCoordY; j < i.getYCoord(); j++){
                    if(isOccupied(targetCoordX, j)){
                        blockCount++;
                        blockNumbers.add(getBlockValue(targetCoordX, j));
                    } else {
                        blockNumbers.add(0);
                    }
                    //Keep a count of the number of slots in front
                    slotCount++;
                }

                blockNumbers.add(i.getBlockValue());

                //starts at the furthest point and compares non zero values ahead of it
                for(int j = 0; j < blockNumbers.size() - 1; j++){
                    if(blockNumbers.get(j) != 0){
                        //Looks that the values to comapre with the first value
                        for(int k = j + 1; k < blockNumbers.size(); k++){
                            if(blockNumbers.get(k) != 0){
                                //If the values are equal to each other set the block value of the bloack in front
                                //Then decrement blockCount and set out of for loop
                                //If the bloack getting compared is the cuurent bloack set ti to remove
                                if((blockNumbers.get(j)).equals(blockNumbers.get(k))){
                                    setBlockValue(targetCoordX, j, blockNumbers.get(j)*2);
                                    if(i.getYCoord() == k){
                                        i.setRemoveBlock();
                                    }
                                    blockCount--;
                                    j = k;  //Look at the next value after the compared number to skip the nmerge number
                                    k = blockNumbers.size();
                                } else {  //If there is another non- zero value the exit loop.
                                    k = blockNumbers.size();
                                }
                            }
                        }
                    }
                }

                //Calculates and sets the target coordinate for the block
                targetCoordY = i.getYCoord() - (slotCount - blockCount);
                i.setTargetCoordY(targetCoordY);
                //Adds the merge bloack to the remove bloack list
                if (i.getRemoveElement()) {
                    removeBlocks.add(i);
                }
            } else if(currentGameDirection == GameLoopTask.GameDirection.DOWN) {
                targetCoordX = i.getXCoord();
                targetCoordY = 3;

                for(int j = targetCoordY; j > i.getYCoord(); j--){
                    //Target target for y coord will be set to 3 because it is the coordinate
                    //Target x target will be the current y coord
                    if(isOccupied(targetCoordX, j)){
                        blockCount++;
                        blockNumbers.add(getBlockValue(targetCoordX, j));
                    } else {
                        blockNumbers.add(0);
                    }
                    slotCount++;
                }


                blockNumbers.add(i.getBlockValue());

                //starts at the furthest point and compares non zero values ahead of it
                for(int j = 0; j < blockNumbers.size() - 1; j++){
                    if(blockNumbers.get(j) != 0){
                        //Looks that the values to comapre with the first value
                        for(int k = j + 1; k < blockNumbers.size(); k++){
                            if(blockNumbers.get(k) != 0){
                                //If the values are equal to each other set the block value of the bloack in front
                                //Then decrement blockCount and set out of for loop
                                //If the bloack getting compared is the cuurent bloack set ti to remove
                                if((blockNumbers.get(j)).equals(blockNumbers.get(k))){
                                    setBlockValue(targetCoordX, 3 - j, blockNumbers.get(j)*2);
                                    if(i.getYCoord() == 3 - k){
                                        i.setRemoveBlock();
                                    }
                                    blockCount--;
                                    j = k;  //Look at the next value after the compared number to skip the nmerge number
                                    k = blockNumbers.size();
                                } else { //If there is another non- zero value the exit loop.
                                    k = blockNumbers.size();
                                }
                            }
                        }
                    }
                }

                //Calculates and sets the target coordinate for the block
                targetCoordY = i.getYCoord() + (slotCount - blockCount);
                i.setTargetCoordY(targetCoordY);
                //Adds the merge bloack to the remove bloack list
                if (i.getRemoveElement()) {
                    removeBlocks.add(i);
                }
            }
        }

        //Checsk if the blocks will move if no movement dont spawn blocks
        if(blockMovement()){
            for(GameBlock i : gameBlockList){
                //Sets the diresction for each block
                i.setBlockDirection(gameDirection);
            }
        }
    }

    //Method to determine if there is a bloack in the coordinate
    public boolean isOccupied(int xCoord, int yCoord){
        for(GameBlock i: gameBlockList){
            //Iterates through each bloack and checks for a coordinate match
            if(i.getXCoord() == xCoord){
                if(i.getYCoord() == yCoord){
                    return true;
                }
            }
        }
        return false;
    }

    //Method to determine if the bloacks moved
    public boolean blockMovement(){
        for(GameBlock i : gameBlockList){
            //Compares the cuurent poisition to the target and if no movement for every block then return false
            if(i.getXCoord() != i.getTargetCoordX() || i.getYCoord() != i.getTargetCoordY()){
                return true;
            }
        }
        return false;
    }

    //Gets the block value of the block on the coordinate
    public int getBlockValue(int xCoord, int yCoord) {
        for (GameBlock i : gameBlockList) {
            if (i.getXCoord() == xCoord) {
                if (i.getYCoord() == yCoord) {
                    //return the bloacks coordinate
                    return i.getBlockValue();
                }
            }
        }
        return 0;
    }

    //Determines the end of game based on if there are any more possible moves
    private boolean checkEndOfGame(int direction){
        if(direction == 0) {
            //Checks the horizontal location
            ArrayList<Integer> rowValues = new ArrayList<>();
            for(int y = 0; y < 4; y++){
                rowValues.clear();
                targetCoordX = 0;
                targetCoordY = y;

                //Stores the vlaues for each bloack in the row
                for (int j = targetCoordX; j < 4; j++) {
                    rowValues.add(getBlockValue(j, targetCoordY));
                }

                //If there are two adjacent bloacks there can be merge
                for(int x = 0; x < rowValues.size() - 1; x++){
                    if(rowValues.get(x).equals(rowValues.get(x+1))){
                        //return false if there is a merge
                        return false;
                    }
                }
            }
            return true; //return true if no merge
        } else if(direction == 1){
            //Checks the vertical location
            ArrayList<Integer> colValues = new ArrayList<>();
            for(int x = 0; x < 4; x++){
                colValues.clear();
                targetCoordX = x;
                targetCoordY = 0;

                //Stores the vlaues for each bloack in the row
                for (int j = targetCoordY; j < 4; j++) {
                    colValues.add(getBlockValue(targetCoordX, j));
                }

                //If there are two adjacent bloacks there can be merge
                for(int y = 0; y < colValues.size() - 1; y++){
                    if(colValues.get(y).equals(colValues.get(y+1))){
                        //return false if there is a merge
                        return false;
                    }
                }
            }
            return true; //return true if no merge
        }
        return false;
    }

    private boolean endGame(){
        int[][] valueArray = new int[4][4];
        for(GameBlock i : gameBlockList){
            valueArray[i.getXCoord()][i.getYCoord()] = i.getBlockValue();
        }

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 3; j++){
                Log.d("y: ", Integer.toString(valueArray[i][j]) + " " + Integer.toString(valueArray[i][j + 1]));
                if(valueArray[i][j] == valueArray[i][j+1]){
                    return false;
                }
            }
        }
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 3; j++) {
                Log.d("x: ", Integer.toString(valueArray[i][j]) + " " + Integer.toString(valueArray[i][j + 1]));
                if (valueArray[j][i] == valueArray[j + 1][i]) {
                    return false;
                }
            }
        }
        return true;
    }

    //Sets the bloack Value for the block
    public void setBlockValue(int xCoord, int yCoord,int value) {
        //Takes a set of coordinates and sets the bloack values for the blaock at that coord
        for (GameBlock i : gameBlockList) {
            //Iterates to find the block
            if (i.getXCoord() == xCoord) {
                if (i.getYCoord() == yCoord) {
                    //Calss the set block method from the gamebloack
                    i.setBlockValue(value);
                }
            }
        }
    }

    //Remember, you need to run this on UI Thread.
    //You will need a reference of the MainActivity in order to do so.
    //Method calls the move method in gameblock to move the bloack
    public void run() {
        myActivity.runOnUiThread(
            new Runnable() {
                public void run() {
                    createBlock = 0;
                    //Runs the move method
                    for(GameBlock i: gameBlockList){
                        i.move();
                    }
                    //After each iteration check to sett if the bloacks are all in position
                    for(GameBlock i: gameBlockList){
                        //If bloacks are in position then incerement a counter by 1
                        if(i.createBlock()){
                            createBlock++;
                        }
                        //Checks for the winning bloack of 256
                        if(i.getBlockValue() == 256){
                            win = true;
                        }
                    }
                    //create a temporaory location
                    int tempLocationX = 0;
                    int tempLocationY = 0;
                    //If all bloacks are in position then set the text for bloacks that have merged
                    if(createBlock >= gameBlockList.size()){
                        for(GameBlock i: removeBlocks){
                            tempLocationX = i.getXCoord();  //Gets the x coordinate after moving
                            tempLocationY = i.getYCoord();  //Gets y coordinate after moving
                            for(GameBlock j: gameBlockList){
                                //Iterates to see the bloacks that have merged and changed the values to double
                                if(i != j){
                                    if (j.getXCoord() == tempLocationX) {
                                        if (j.getYCoord() == tempLocationY) {
                                            //Sets the tecxt of the bloack
                                            j.setText();
                                        }
                                    }
                                }
                            }
                        }
                        //takes the list of bloacks to be removed and removed them form gameblaock list
                        for(GameBlock i: removeBlocks){
                            gameBlockList.remove(i);
                            i = null;
                        }//creates a new bloack
                        createBlock();
                        // tif there is a 256 on the grid then send a toast to screen sayin that player won
                        if(win){
                            Toast toast = Toast.makeText(myContext, "YOU WIN!!!!", Toast.LENGTH_LONG);
                            toast.show();
                        }

                        //checks to see if the game is lost when all bloacks are filled on the grid
                        if(gameBlockList.size() >= 16){
                            //calls the end of game mothod to see if any other moves are possible
                            //if no moves possible the end the game
                            if(endGame()){
                                //call the toast to end the game
                                Toast toast1 = Toast.makeText(myContext, "GAME OVER YOU LOSE!!!!", Toast.LENGTH_SHORT);
                                toast1.show();
                            }
                        }
                    }
                }
            }
        );
    }
}
