package by.bsu.mmf.badthrowgame.dice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lenovo on 07/07/2017.
 */
public class DicePack implements Serializable{
    private List<Dice> dices = new ArrayList<>();
    private int total;

    public DicePack() {
        for (int i = 0; i < 5; ++i) dices.add(new Dice());
        total = 0;
    }

    public int getTotal(){
        return total;
    }

    public void makeThrow() {
        dices.stream().forEach((d) ->d.throwDice());
    }

    public boolean checkDices() {
        int sizeB = dices.size();
        Dice[] toDelete = dices.stream().filter((d)->d.getValue()==2 || d.getValue() == 5).toArray(Dice[]::new);
        Arrays.stream(toDelete).forEach((d)->removeDice(d));
        return sizeB == dices.size();
    }

    public void countTotal(){
        if (checkDices()){
            dices.stream().forEach((d)->{
                total += d.getValue();
            });
        }
    }

    public void removeDice(Dice dice) {
        dices.remove(dice);
    }

    public boolean isEmpty() {
        boolean isEmpty = false;
        if (dices.size() == 0) isEmpty = true;
        return isEmpty;
    }

    public List<Dice> getDices() {
        return dices;
    }

    public void setDices(List<Dice> dices) {
        this.dices = dices;
    }
}
