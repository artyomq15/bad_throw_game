package by.bsu.mmf.badthrowgame.transferobject;

import by.bsu.mmf.badthrowgame.enums.AccountSuccess;

import java.io.Serializable;

/**
 * Created by Lenovo on 08/05/2017.
 */
public class SCEnteringAccount implements Serializable {
    private AccountSuccess message;
    private String id;

    public AccountSuccess getMessage() {
        return message;
    }

    public void setMessage(AccountSuccess message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
