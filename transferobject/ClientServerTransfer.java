package by.bsu.mmf.badthrowgame.transferobject;

import by.bsu.mmf.badthrowgame.enums.RequestPlayerAction;

import java.io.Serializable;

/**
 * Created by Lenovo on 08/05/2017.
 */
public class ClientServerTransfer implements Serializable {
    private String idPlayer;
    private String messagePlayer;
    private RequestPlayerAction actionPlayer;

    private String bet;

    public String getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(String idPlayer) {
        this.idPlayer = idPlayer;
    }

    public String getMessagePlayer() {
        return messagePlayer;
    }

    public void setMessagePlayer(String messagePlayer) {
        this.messagePlayer = messagePlayer;
    }

    public RequestPlayerAction getRequestActionPlayer() {
        return actionPlayer;
    }

    public void setRequestActionPlayer(RequestPlayerAction actionPlayer) {
        this.actionPlayer = actionPlayer;
    }

    public String getBet() {
        return bet;
    }

    public void setBet(String bet) {
        this.bet = bet;
    }
}
