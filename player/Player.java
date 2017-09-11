package by.bsu.mmf.badthrowgame.player;

import by.bsu.mmf.badthrowgame.dice.DicePack;

import java.io.Serializable;


public class Player implements Serializable {
    private String idPlayer;
    private String namePlayer;
    private int cashPlayer;
    private int gamesPlayed;
    private int gamesWon;
    private boolean isPlaying;

    private DicePack dicePack;



    private boolean isReady;

    public Player(){
        isReady= false;
    };

    public Player(String idPlayer, String namePlayer) {
        this.idPlayer = idPlayer;
        this.namePlayer = namePlayer;
    }

    public DicePack getDicePack() {
        return dicePack;
    }

    public void setDicePack(DicePack dicePack) {
        this.dicePack = dicePack;
    }

    public String getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(String idPlayer) {
        this.idPlayer = idPlayer;
    }


    public String getNamePlayer() {
        return namePlayer;
    }

    public void setNamePlayer(String namePlayer) {
        this.namePlayer = namePlayer;
    }

    public int getCashPlayer() {
        return cashPlayer;
    }

    public void setCashPlayer(int cashPlayer) {
        this.cashPlayer = cashPlayer;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }



    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}
