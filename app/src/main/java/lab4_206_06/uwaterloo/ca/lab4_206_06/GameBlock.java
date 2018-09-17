package lab4_206_06.uwaterloo.ca.lab4_206_06;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.Random;

import static android.graphics.Color.BLACK;

/**
 * Created by Calvin on 2017-06-24.
 */
class GameBlock extends GameBlockTemplate {
    private final float IMAGE_SCALE = 1.16f;
    private GameLoopTask.GameDirection myDir;
    private RelativeLayout myRelativeLayout;

    private final float SLOT_ISOLATION = GameLoopTask.SLOT_ISO;
    private float positionX;
    private float velocityX;
    private float positionY;
    private float velocityY;

    private TextView blockNumber;
    private int blockValue;
    private int targetCoordX;
    private int targetCoordY;
    private boolean removedBlock;
    private boolean createBlock;
    private int changeBlockValue;


    //Constructor for the gamebloack
    public GameBlock(Context myCont, int coordX, int coordY, RelativeLayout myRL){
        super(myCont);
        //USes the parent class to set the gameblock
        this.setImageResource(R.drawable.gameblock);
        this.setPivotX(0);
        this.setPivotY(0);
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);
        this.setX(coordX);
        this.setY(coordY);

        //Stets the starting position and speed of the block
        //And any other variables
        velocityX = 20;
        velocityY = 20;
        positionX = coordX;
        positionY = coordY;
        targetCoordY = coordY / (int)SLOT_ISOLATION; //Want to coordinates
        targetCoordX = coordX / (int)SLOT_ISOLATION; //Want to coordinates
        removedBlock = false;
        createBlock = false;


        myDir = GameLoopTask.GameDirection.UNDETERMINED;

        Random randomStartInteger = new Random();
        blockValue = (randomStartInteger.nextInt(2)+ 1) * 2; //Generates a radnom number 2, 4 on each spawn
        myRelativeLayout = myRL;
        myRelativeLayout.addView(this);    //Adds the bloack to the layout
        blockNumber = new TextView(myCont);    //creates a text view for the bloack values
        blockNumber.setX(positionX + (int)(SLOT_ISOLATION / 4.7));   //positions the text view to be aligned with the blaock
        blockNumber.setY(positionY + (int)(SLOT_ISOLATION / 4.8)); //positions the text view to be aligned with the blaock

        blockNumber.setText(Integer.toString(blockValue));   //Sets the block value on the textView
        blockNumber.setTextSize(64f);
        blockNumber.setTextColor(BLACK);
        blockNumber.bringToFront();  //Brings the textview in front of the block
        myRelativeLayout.addView(blockNumber); //Ads the btext to the layout
    }

    //Sets the direction of the block if the bloack is in one of the corners
    public void setBlockDirection(GameLoopTask.GameDirection newDir){
        //Only change the directions when the bloack is in a corner
        myDir = newDir;
    }

    public void setDestination(){
    }

    //getter mothod for the target x coord
    public int getTargetCoordX(){
        return targetCoordX;
    }

    //getter mothod for the target y coord
    public int getTargetCoordY(){

        return targetCoordY;
    }

    //getter mothod for the current x coord
    public int getXCoord(){

        return (int)(positionX / SLOT_ISOLATION); // Divides the position by the iso to get it in coordinate form
    }

    //getter mothod for the current y coord
    public int getYCoord(){

        return (int)(positionY / SLOT_ISOLATION); // Divides the position by the iso to get it in coordinate form
    }

    //set the value for the bloack to be removed
    public void setRemoveBlock() {
        removedBlock = true;
    }

    //gets the value for the bloack to be removed
    public boolean getRemoveElement(){
        return removedBlock;
    }

    //Sets the text of the bloack, which gets called after all blocks in position
    public void setText(){
        blockNumber.setText(Integer.toString(blockValue));
    }

    //Gets the vlaue fo the block
    public int getBlockValue(){
        return blockValue;
    }

    //sets a temporary bloack value
    public void setBlockValue(int value){
        changeBlockValue = value;
    }

    //Return the true of the bloack has stoopped moving
    public boolean createBlock(){
        return createBlock;
    }

    //Sets the target x coord
    public void setTargetCoordX(int coordX){
        targetCoordX = coordX;
    }

    //sets the target y coord
    public void setTargetCoordY(int coordY){
        targetCoordY = coordY;
    }

    //Method will move the block
    public void move() {

        //Switch case for the different directions
        //This will move the bloack depending on which state the bloack is in
            switch (myDir) {
                case LEFT:
                    createBlock = false;
                    //Constant Velocity Displacement
                    positionX -= velocityX;
                    //Adds the Newtonic accleration adds 5 each tick
                    velocityX += 10;

                    //Boundary Checking
                    if (positionX < (targetCoordX * SLOT_ISOLATION)) {
                        positionX = (targetCoordX * SLOT_ISOLATION);
                        //Sets the new vlaue of the bloack if the merged happened
                        if(changeBlockValue != 0){
                            blockValue = changeBlockValue;
                            changeBlockValue = 0;
                        }
                        createBlock = true;
                        if(removedBlock){
                            //if merged remove the bloack form the layout
                            myRelativeLayout.removeView(blockNumber);
                            myRelativeLayout.removeView(this);
                        }
                        //Resets the velocity back to the original
                        //Withough acceleration
                        velocityX = 20;
                    }

                    //Update the image position
                    this.setX(positionX);
                    blockNumber.setX(positionX + (int) (SLOT_ISOLATION / 4.7));

                    break;
                case RIGHT:
                    createBlock = false;
                    //Constant Velocity Displacement
                    positionX += velocityX;
                    //Adds the Newtonic accleration adds 5 each tick
                    velocityX += 10;

                    //Boundary Checking
                    if (positionX > (targetCoordX  * SLOT_ISOLATION)){
                        positionX = (targetCoordX  * SLOT_ISOLATION);
                        //Sets the new vlaue of the bloack if the merged happened
                        if(changeBlockValue != 0){
                            blockValue = changeBlockValue;
                            changeBlockValue = 0;
                        }
                        createBlock = true;
                        if(removedBlock){
                            //if merged remove the bloack form the layout
                            myRelativeLayout.removeView(blockNumber);
                            myRelativeLayout.removeView(this);
                        }
                        //Resets the velocity back to the original
                        //Withough acceleration
                        velocityX = 20;
                    }

                    //Update the image position
                    this.setX(positionX);
                    blockNumber.setX(positionX + (int) (SLOT_ISOLATION / 4.7));
                    break;
                case UP:
                    createBlock = false;
                    //Constant Velocity Displacement
                    positionY -= velocityY;
                    //Adds the Newtonic accleration adds 5 each tick
                    velocityY += 10;

                    //Boundary Checking
                    if (positionY < (targetCoordY  * SLOT_ISOLATION)) {
                        positionY = (targetCoordY  * SLOT_ISOLATION);
                        //Sets the new vlaue of the bloack if the merged happened
                        if(changeBlockValue != 0){
                            blockValue = changeBlockValue;
                            changeBlockValue = 0;
                        }
                        createBlock = true;
                        if(removedBlock){
                            //if merged remove the bloack form the layout
                            myRelativeLayout.removeView(blockNumber);
                            myRelativeLayout.removeView(this);
                            //removeLinkedList = true;
                        }
                        //Resets the velocity back to the original
                        //Withough acceleration
                        velocityY = 20;
                    }

                    //Update the image position
                    this.setY(positionY);
                    blockNumber.setY(positionY + (int)(SLOT_ISOLATION / 4.8));

                    break;
                case DOWN:
                    createBlock = false;
                    //Constant Velocity Displacement
                    positionY += velocityY;
                    //Adds the Newtonic accleration adds 5 each tick
                    velocityY += 10;

                    //Boundary Checking
                    if (positionY > (targetCoordY  * SLOT_ISOLATION)) {
                        positionY = (targetCoordY  * SLOT_ISOLATION);
                        //Sets the new vlaue of the bloack if the merged happened
                        if(changeBlockValue != 0){
                            blockValue = changeBlockValue;
                            changeBlockValue = 0;
                        }
                        createBlock = true;
                        if(removedBlock){
                            //if merged remove the bloack form the layout
                            myRelativeLayout.removeView(blockNumber);
                            myRelativeLayout.removeView(this);
                        }
                        //Resets the velocity back to the original
                        //Withough acceleration
                        velocityY = 20;
                    }

                    //Update the image position
                    this.setY(positionY);
                    blockNumber.setY(positionY + (int)(SLOT_ISOLATION / 4.8));
                    break;
                default:
                    //Do nothing if the block is not in any other state
                    break;
            }
        }
    }

