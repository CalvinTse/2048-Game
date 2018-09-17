package lab4_206_06.uwaterloo.ca.lab4_206_06;

import android.content.Context;
import android.widget.ImageView;

import java.util.LinkedList;

/**
 * Created by Calvin on 2017-07-01.
 */

public abstract class GameBlockTemplate extends ImageView {

    public GameBlockTemplate(Context myCont) {
        super(myCont);
    }

    public abstract void setDestination();
    public abstract void move();
}
