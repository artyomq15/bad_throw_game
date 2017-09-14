package by.bsu.mmf.badthrowgame.client;

import by.bsu.mmf.badthrowgame.dice.DicePack;
import by.bsu.mmf.badthrowgame.enums.AccountAction;
import by.bsu.mmf.badthrowgame.enums.RequestPlayerAction;
import by.bsu.mmf.badthrowgame.enums.ResponsePlayerAction;
import by.bsu.mmf.badthrowgame.enums.ViewTypes;
import by.bsu.mmf.badthrowgame.player.Player;
import by.bsu.mmf.badthrowgame.transferobject.CSEnteringAccount;
import by.bsu.mmf.badthrowgame.transferobject.ClientServerTransfer;
import by.bsu.mmf.badthrowgame.transferobject.SCEnteringAccount;
import by.bsu.mmf.badthrowgame.transferobject.ServerClientTransfer;

import static by.bsu.mmf.badthrowgame.validation.RegisterValidation.*;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Lenovo on 08/06/2017.
 */
public class ClientHandlerGUI extends Thread {
    private Socket clientSocket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;


    // private ClientHandler clientHandler;


    private JPanel jpanel;
    private JPanel signInPanel;
    private JTextArea playersList;
    private JTextArea chatList;
    private JTextField messageField;
    private JButton sendMessageButton;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JPanel signUpPanel;
    private JTextField nameRegisterField;
    private JTextField loginRegisterField;
    private JPasswordField passwordRegisterField;
    private JPasswordField passwordConfirmField;
    private JButton enterAccount;
    private JButton registerNewAccountButton;
    private JButton registerButton;
    private JPanel gamePanel;
    private JTabbedPane accountMainPanel;
    private JButton backToSigningInButton;
    private JButton enterTheGameButton;
    private JButton exitFromTheGameButton;
    private JButton readyButton;
    private JLabel betLabel;
    private JButton throwButton;
    private JLabel totalField;
    private JButton plusBet;
    private JButton minusBet;
    private JLabel betLabel1;
    private JTextArea spectatorsList;
    private JButton changeNameButton;
    private JLabel nameProfile;
    private JButton addCashButton;
    private JButton refreshButton;
    private JLabel cashProfileField;
    private JLabel gamesProfileField;
    private JLabel gamesWonProfileField;
    private JLabel winRateProfileField;
    private JTextArea dicesValues;
    private JTextArea statisticsArea;
    private JButton refreshStatisticsButton;
    private JTextArea playerDicesArea;
    private JTextArea computerDicesArea;
    private JButton throwButtonVsComputer;
    private JLabel playerTotal;
    private JLabel computerTotal;
    private JLabel resultMessage;
    private JButton startGameVsComputerButton;
    private JLabel playerLabel;
    private JLabel computerLabel;
    private JLabel dicesLabel;

    private String idClient;

    private final Map<ResponsePlayerAction, Consumer<ServerClientTransfer>> dispatch = new HashMap<>();

    DicePack playerDicePack;
    DicePack computerDicePack;

    public ClientHandlerGUI(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        outStream = new ObjectOutputStream(clientSocket.getOutputStream());
        inStream = new ObjectInputStream(clientSocket.getInputStream());

        chatList.setLineWrap(true);
        chatList.setWrapStyleWord(true);

        enterTheGameButton.setVisible(true);
        exitFromTheGameButton.setVisible(false);

        readyButton.setVisible(false);
        plusBet.setVisible(false);
        minusBet.setVisible(false);
        throwButton.setVisible(false);
        totalField.setVisible(false);

        betLabel.setVisible(false);
        betLabel1.setVisible(false);

        throwButtonVsComputer.setVisible(false);

        enterAccount.addActionListener(e -> {
            String login = loginField.getText();
            char[] password = passwordField.getPassword();
            enterAccount(login, password);
        });

        sendMessageButton.addActionListener(e -> {
            sendMessage();

        });

        registerNewAccountButton.addActionListener(e -> {
            setView(ViewTypes.REGISTER_VIEW);

        });

        backToSigningInButton.addActionListener(e -> {

            setView(ViewTypes.LOGIN_VIEW);

        });

        registerButton.addActionListener(e -> {

            if (passwordConfirmValidation(passwordRegisterField.getPassword(),passwordConfirmField.getPassword()) && loginRegisterValidation(loginRegisterField.getText())) {
                registerAccount(nameRegisterField.getText(), loginRegisterField.getText(), passwordRegisterField.getPassword());
            } else {
                passwordRegisterField.setText("");
                passwordConfirmField.setText("");
            }

        });

        enterTheGameButton.addActionListener(e -> {

            enterMultiplayer();

            dicesValues.setText("");

        });

        exitFromTheGameButton.addActionListener(e -> {
            exitMultiplayer();

            enterTheGameButton.setVisible(true);
            exitFromTheGameButton.setVisible(false);

            playersList.setText("");
            spectatorsList.setText("");

            readyButton.setVisible(false);
            plusBet.setVisible(false);
            minusBet.setVisible(false);
            throwButton.setVisible(false);
            totalField.setVisible(false);

            betLabel.setVisible(false);
            betLabel1.setVisible(false);

        });

        plusBet.addActionListener(e -> {

            changeBet(String.valueOf(Integer.parseInt(betLabel.getText()) + 50));

        });

        minusBet.addActionListener(e -> {

            if (Integer.parseInt(betLabel.getText()) > 50)
                changeBet(String.valueOf(Integer.parseInt(betLabel.getText()) - 50));

        });

        readyButton.addActionListener(e -> {

            setReady();

        });

        refreshButton.addActionListener(e -> {

            refreshProfileInfo();

        });

        throwButton.addActionListener(e -> {

            makeThrow();

        });

        refreshStatisticsButton.addActionListener(e -> {

            refreshStatistics();

        });


        throwButtonVsComputer.addActionListener(e -> {

            makeThrowVsComputer(playerDicePack, computerDicePack);

        });

        startGameVsComputerButton.addActionListener(e -> {

            playerDicePack = new DicePack();
            computerDicePack = new DicePack();
            throwButtonVsComputer.setVisible(true);
            startGameVsComputerButton.setVisible(false);
            playerDicesArea.setText("");
            computerDicesArea.setText("");
            resultMessage.setText("");
            playerTotal.setText("0");
            computerTotal.setText("0");

        });

        initGUI();

        startClient();


    }

    public void initGUI() {
        JFrame jframe = new JFrame("Bad Throw Game");
        jframe.setContentPane(jpanel);
        jframe.setSize(500, 500);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.pack();
        jframe.setVisible(true);
    }

    public void startClient() {
        this.start();
    }

    public void enterAccount(String login, char[] password) {
        CSEnteringAccount csea = new CSEnteringAccount(AccountAction.SIGNING_IN, login, password);
        try {
            outStream.writeObject(csea);
            outStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void registerAccount(String name, String login, char[] password) {
        CSEnteringAccount csea = new CSEnteringAccount(AccountAction.SIGNING_UP, login, password, name);
        try {
            outStream.writeObject(csea);
            outStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendMessage() {
        ClientServerTransfer cst = new ClientServerTransfer();
        cst.setRequestActionPlayer(RequestPlayerAction.SEND_MESSAGE);
        if (!messageField.getText().equals("")) {
            cst.setMessagePlayer(messageField.getText());
            cst.setIdPlayer(idClient);
            messageField.setText("");
        }
        sendToServer(cst);
    }

    public void enterMultiplayer() {
        ClientServerTransfer cst = new ClientServerTransfer();
        cst.setRequestActionPlayer(RequestPlayerAction.ENTER_MULTIPLAYER);
        cst.setIdPlayer(idClient);
        sendToServer(cst);
    }

    public void exitMultiplayer() {
        ClientServerTransfer cst = new ClientServerTransfer();
        cst.setRequestActionPlayer(RequestPlayerAction.EXIT_MULTIPLAYER);
        cst.setIdPlayer(idClient);
        sendToServer(cst);
    }

    public void setReady() {
        ClientServerTransfer cst = new ClientServerTransfer();
        cst.setRequestActionPlayer(RequestPlayerAction.SET_READY);
        cst.setIdPlayer(idClient);
        sendToServer(cst);
    }

    public void changeBet(String bet) {
        ClientServerTransfer cst = new ClientServerTransfer();
        cst.setRequestActionPlayer(RequestPlayerAction.CHANGE_BET);
        cst.setIdPlayer(idClient);
        cst.setBet(bet);
        sendToServer(cst);
    }

    public void refreshProfileInfo() {
        ClientServerTransfer cst = new ClientServerTransfer();
        cst.setRequestActionPlayer(RequestPlayerAction.REFRESH_PROFILE);
        cst.setIdPlayer(idClient);
        sendToServer(cst);
    }

    public void refreshStatistics() {
        ClientServerTransfer cst = new ClientServerTransfer();
        cst.setRequestActionPlayer(RequestPlayerAction.REFRESH_STATISTICS);
        cst.setIdPlayer(idClient);
        sendToServer(cst);
    }

    public void makeThrow() {
        ClientServerTransfer cst = new ClientServerTransfer();
        cst.setRequestActionPlayer(RequestPlayerAction.MAKE_THROW);
        cst.setIdPlayer(idClient);
        sendToServer(cst);
    }

    public void makeThrowVsComputer(DicePack player, DicePack computer) {
        if (!player.isEmpty()) {
            player.makeThrow();
            player.getDices().stream().forEach(dice -> playerDicesArea.append(dice.getValue() + " . "));
            player.countTotal();
            playerTotal.setText(String.valueOf(player.getTotal()));
            playerDicesArea.append("\n");
        } else {
            while (!computer.isEmpty()) {
                computer.makeThrow();
                computer.getDices().stream().forEach(dice -> computerDicesArea.append(dice.getValue() + " . "));
                computer.countTotal();
                computerTotal.setText(String.valueOf(computer.getTotal()));
                computerDicesArea.append("\n");

            }

            throwButtonVsComputer.setVisible(false);
            startGameVsComputerButton.setVisible(true);
            if (player.getTotal() > computer.getTotal()) {
                resultMessage.setText("WIN");
            } else if (player.getTotal() < computer.getTotal()) {
                resultMessage.setText("LOSS");
            } else resultMessage.setText("DRAW");

        }
    }

    public void sendToServer(ClientServerTransfer cst) {
        try {
            outStream.reset();
            outStream.writeObject(cst);
            outStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setView(ViewTypes viewType) {
        switch (viewType) {
            case LOGIN_VIEW: {
                signInPanel.setVisible(true);
                signUpPanel.setVisible(false);
                gamePanel.setVisible(false);
                break;
            }
            case REGISTER_VIEW: {
                signInPanel.setVisible(false);
                signUpPanel.setVisible(true);
                gamePanel.setVisible(false);
                break;
            }
            case GAME_VIEW: {
                signInPanel.setVisible(false);
                signUpPanel.setVisible(false);
                gamePanel.setVisible(true);
            }
        }
    }

    public Consumer<ServerClientTransfer> sendMessageResponse() {
        return sct -> {
            chatList.append(sct.getMessageChat() + "\n");
        };
    }

    public Consumer<ServerClientTransfer> enterPlayersResponse() {
        return sct -> {
            refreshLists(sct);
            betLabel.setText(sct.getBet());

            if (idClient.equals(sct.getIdPlayer())) {
                enterTheGameButton.setVisible(false);
                exitFromTheGameButton.setVisible(true);

                readyButton.setVisible(true);
                plusBet.setVisible(true);
                minusBet.setVisible(true);

                betLabel.setVisible(true);
                betLabel1.setVisible(true);
            }

        };
    }

    public Consumer<ServerClientTransfer> enterSpectatorsResponse() {
        return sct -> {
            refreshLists(sct);
            betLabel.setText(sct.getBet());

            if (idClient.equals(sct.getIdPlayer())) {

                enterTheGameButton.setVisible(false);
                exitFromTheGameButton.setVisible(true);

                readyButton.setVisible(false);
                plusBet.setVisible(false);
                minusBet.setVisible(false);

                betLabel.setVisible(true);
                betLabel1.setVisible(true);
            }

        };
    }

    public Consumer<ServerClientTransfer> exitMultiplayerResponse() {
        return sct -> {
            refreshLists(sct);
        };
    }

    public Consumer<ServerClientTransfer> setReadyResponse() {
        return sct -> {
            refreshLists(sct);
            plusBet.setVisible(false);
            minusBet.setVisible(false);

            if (idClient.equals(sct.getIdPlayer())) {

                readyButton.setVisible(false);
                plusBet.setVisible(false);
                minusBet.setVisible(false);
                throwButton.setVisible(true);
                totalField.setVisible(true);
            }
        };
    }

    public Consumer<ServerClientTransfer> changeBetResponse() {
        return sct -> {
            betLabel.setText(sct.getBet());
        };
    }

    public Consumer<ServerClientTransfer> refreshProfileResponse() {
        return sct -> {
            Player player = sct.getPlayer();
            nameProfile.setText(player.getNamePlayer());
            cashProfileField.setText(String.valueOf(player.getCashPlayer()));
            gamesProfileField.setText(String.valueOf(player.getGamesPlayed()));
            gamesWonProfileField.setText(String.valueOf(player.getGamesWon()));
            if (player.getGamesPlayed() != 0) {
                winRateProfileField.setText(String.valueOf(100 * player.getGamesWon() / player.getGamesPlayed()) + " %");
            }
        };
    }

    public Consumer<ServerClientTransfer> makeThrowResponse() {
        return sct -> {
            sct.getPlayerList().stream().forEach(player1 -> System.out.print(player1.getIdPlayer() + "...."));
            System.out.println("idClient - " + idClient + " PlayerID " + sct.getIdPlayer());
            if (idClient.equals(sct.getIdPlayer())) {
                Player player = sct.getPlayerList().stream().filter(pl -> pl.getIdPlayer().equals(idClient)).findFirst().orElse(null);
                DicePack dicePack = player.getDicePack();
                if (!dicePack.isEmpty()) {
                    totalField.setText(String.valueOf(dicePack.getTotal()));
                    dicePack.getDices().stream().forEach(dice -> dicesValues.append(dice.getValue() + " . "));
                    dicesValues.append("\n");
                } else {
                    throwButton.setVisible(false);
                }
            }

            refreshLists(sct);
        };
    }

    public Consumer<ServerClientTransfer> refreshStatisticsResponse() {
        return sct -> {
            statisticsArea.setText("");
            List<Player> players = sct.getPlayerList();
            players.stream().forEach(player -> {
                int percent = 0;
                if (player.getGamesPlayed() != 0) percent = 100 * player.getGamesWon() / player.getGamesPlayed();
                statisticsArea.append(player.getNamePlayer() + "  |  " + player.getGamesPlayed() + "/" + player.getGamesWon() + "  |  " + percent + "%  |  " + player.getCashPlayer() + "$ \n");
            });
        };
    }

    public void load(ResponsePlayerAction type, Consumer<ServerClientTransfer> handle) {
        dispatch.put(type, handle);
    }

    public void initDispatcher() {
        load(ResponsePlayerAction.SEND_MESSAGE, sendMessageResponse());
        load(ResponsePlayerAction.ENTER_PLAYERS, enterPlayersResponse());
        load(ResponsePlayerAction.ENTER_SPECTATORS, enterSpectatorsResponse());
        load(ResponsePlayerAction.EXIT_MULTIPLAYER, exitMultiplayerResponse());
        load(ResponsePlayerAction.SET_READY, setReadyResponse());
        load(ResponsePlayerAction.CHANGE_BET, changeBetResponse());
        load(ResponsePlayerAction.REFRESH_PROFILE, refreshProfileResponse());
        load(ResponsePlayerAction.MAKE_THROW, makeThrowResponse());
        load(ResponsePlayerAction.REFRESH_STATISTICS, refreshStatisticsResponse());
    }

    public void handle(ServerClientTransfer sct) {
        dispatch.get(sct.getResponsePlayerAction()).accept(sct);
    }

    public void refreshLists(ServerClientTransfer sct) {
        playersList.setText("");
        sct.getPlayerList().stream().forEach((p) -> {
            if (p.isReady()) playersList.append(" + ");
            playersList.append(p.getNamePlayer() + " / $" + p.getCashPlayer());
            if (p.getDicePack() != null) {
                playersList.append(" [" + p.getDicePack().getTotal() + "] ");
            }
            playersList.append("\n");
        });
        spectatorsList.setText("");
        sct.getSpectatorList().stream().forEach((p) -> {
            spectatorsList.append(p.getNamePlayer() + "\n");
        });
    }

    public void run() {
        try {

            ViewTypes viewType = ViewTypes.LOGIN_VIEW;
            setView(viewType);

            boolean entered = false;
            while (!entered) {
                SCEnteringAccount scea = (SCEnteringAccount) inStream.readObject();

                switch (scea.getMessage()) {
                    case SUCCESSFUL_ENTERING: {
                        entered = true;
                        idClient = scea.getId();
                        viewType = ViewTypes.GAME_VIEW;
                        break;
                    }
                    case UNSUCCESSFUL_ENTERING: {
                        passwordField.setText("");
                        break;
                    }
                    case SUCCESSFUL_REGISTER: {
                        viewType = ViewTypes.LOGIN_VIEW;
                        setView(viewType);
                        break;
                    }
                    case UNSUCCESSFUL_REGISTER: {
                        loginRegisterField.setText("");
                        passwordRegisterField.setText("");
                        passwordConfirmField.setText("");
                        break;
                    }

                }


            }

            setView(viewType);
            refreshProfileInfo();
            refreshStatistics();


            initDispatcher();

            ServerClientTransfer sct;
            while (true) {
                sct = (ServerClientTransfer) inStream.readObject();
                handle(sct);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Socket clientSocket = new Socket(InetAddress.getLocalHost(), 8080);
        new ClientHandlerGUI(clientSocket);

    }
}
