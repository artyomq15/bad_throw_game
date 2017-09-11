package by.bsu.mmf.badthrowgame.datasource;

import by.bsu.mmf.badthrowgame.enums.AccountSuccess;
import by.bsu.mmf.badthrowgame.player.Player;

import java.util.List;

/**
 * Created by Lenovo on 08/16/2017.
 */
public interface AbstractDAO {
    List<? extends Player> findAll();
    /*List<? extends Player> findPlaying();*/
    Player findPlayerById(String id);
    AccountSuccess createAccount(String name, String login, char[] password);
    String enterAccount(String login, char[] password);
    void setPlaying(String id, boolean isPlaying);
    void takeBetCash(String id, int cash);
    void putBetCash(String id, int cash);
    void addGamePlayed(String id);
    void addGameWon(String id);




}
