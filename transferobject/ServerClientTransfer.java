package by.bsu.mmf.badthrowgame.transferobject;

import by.bsu.mmf.badthrowgame.enums.RequestPlayerAction;
import by.bsu.mmf.badthrowgame.enums.ResponsePlayerAction;
import by.bsu.mmf.badthrowgame.player.Player;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Lenovo on 08/06/2017.
 */
public class ServerClientTransfer implements Serializable {
    private Player player;
    private List<Player> playerList = new LinkedList<>();

    private List<Player> spectatorList = new LinkedList<>();

    private String idPlayer;


    private String messageChat;
    private ResponsePlayerAction responsePlayerAction;

    private String bet;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public void addPlayerToPlayerList(Player player) {
        playerList.add(player);
    }

    public String getMessageChat() {
        return messageChat;
    }

    public void setMessageChat(String messageChat) {
        this.messageChat = messageChat;
    }

    public ResponsePlayerAction getResponsePlayerAction() {
        return responsePlayerAction;
    }

    public void setResponsePlayerAction(ResponsePlayerAction responsePlayerAction) {
        this.responsePlayerAction = responsePlayerAction;
    }

    public String getBet() {
        return bet;
    }

    public void setBet(String bet) {
        this.bet = bet;
    }


    public String getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(String idPlayer) {
        this.idPlayer = idPlayer;
    }

    public List<Player> getSpectatorList() {
        return spectatorList;
    }

    public void setSpectatorList(List<Player> spectatorList) {
        this.spectatorList = spectatorList;
    }

    public void addSpectatorToSpectatorList(Player spectator) {
        spectatorList.add(spectator);
    }


}
