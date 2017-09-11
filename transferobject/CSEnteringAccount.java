package by.bsu.mmf.badthrowgame.transferobject;

import by.bsu.mmf.badthrowgame.enums.AccountAction;

import java.io.Serializable;

/**
 * Created by Lenovo on 08/05/2017.
 */
public class CSEnteringAccount implements Serializable{
    private AccountAction accountAction;
    private String name;
    private String login;
    private char[] password;

    public CSEnteringAccount(AccountAction accountAction, String login, char[] password) {
        this.accountAction = accountAction;
        this.login = login;
        this.password = password;
        this.name = null;
    }

    public CSEnteringAccount(AccountAction accountAction, String login, char[] password, String name) {
        this.accountAction = accountAction;
        this.name = name;
        this.login = login;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public AccountAction getAccountAction() {
        return accountAction;
    }

    public void setAccountAction(AccountAction accountAction) {
        this.accountAction = accountAction;
    }
}
