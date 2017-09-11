package by.bsu.mmf.badthrowgame.dice;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Lenovo on 07/06/2017.
 */
public class Dice implements Serializable{
    private int value;
    public int getValue(){
        return value;
    }
    public void throwDice(){
        value = new Random().nextInt(6)+1;
    }
}
