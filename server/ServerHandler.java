package by.bsu.mmf.badthrowgame.server;

import by.bsu.mmf.badthrowgame.datasource.ServerDAO;
import by.bsu.mmf.badthrowgame.dice.DicePack;
import by.bsu.mmf.badthrowgame.enums.AccountAction;
import by.bsu.mmf.badthrowgame.enums.AccountSuccess;
import by.bsu.mmf.badthrowgame.enums.RequestPlayerAction;
import by.bsu.mmf.badthrowgame.enums.ResponsePlayerAction;
import by.bsu.mmf.badthrowgame.player.Player;
import by.bsu.mmf.badthrowgame.transferobject.CSEnteringAccount;
import by.bsu.mmf.badthrowgame.transferobject.ClientServerTransfer;
import by.bsu.mmf.badthrowgame.transferobject.SCEnteringAccount;
import by.bsu.mmf.badthrowgame.transferobject.ServerClientTransfer;

import static by.bsu.mmf.badthrowgame.server.ServerGame.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


/**
 * Created by Lenovo on 09/07/2017.
 */
public class ServerHandler extends Thread {
    private Server server;

    private String idClient;

    private ServerDAO serverDAO;

    private final Map<RequestPlayerAction, Consumer<ClientServerTransfer>> dispatch = new HashMap<>();

    public ServerHandler(Socket socket) {
        try {
            server = new Server(socket);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        serverDAO = new ServerDAO();
    }


    public void sendToClient(ObjectOutputStream outStream, ServerClientTransfer sct) {
        try {
            outStream.reset();
            outStream.writeObject(sct);
            outStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void broadcast(ServerClientTransfer sct) {
        players.entrySet().stream().forEach((c) -> {
            sendToClient(c.getKey().server.getOutStream(), sct);
        });
        spectators.entrySet().stream().forEach((c) -> {
            sendToClient(c.getKey().server.getOutStream(), sct);
        });
    }

    public void broadcastMessage(String messageSender, String message) {
        ServerClientTransfer sct = new ServerClientTransfer();
        sct.setMessageChat(messageSender + " : " + message);
        sct.setResponsePlayerAction(ResponsePlayerAction.SEND_MESSAGE);

        broadcast(sct);

    }

    public void broadcastPlayers(ResponsePlayerAction requestPlayerAction) {
        ServerClientTransfer sct = new ServerClientTransfer();
        players.entrySet().stream().forEach((cl) -> {
            sct.addPlayerToPlayerList(cl.getValue());
        });
        spectators.entrySet().stream().forEach((cl) -> {
            sct.addSpectatorToSpectatorList(cl.getValue());
        });
        sct.setResponsePlayerAction(requestPlayerAction);
        sct.setIdPlayer(idClient);
        sct.setBet(String.valueOf(gameBet));
        broadcast(sct);
    }

    public void broadcastBet(String bet) {
        synchronized (gameBet) {
            gameBet = Integer.parseInt(bet);
        }

        ServerClientTransfer sct = new ServerClientTransfer();
        sct.setIdPlayer(idClient);
        sct.setBet(String.valueOf(gameBet));
        sct.setResponsePlayerAction(ResponsePlayerAction.CHANGE_BET);
        broadcast(sct);
    }

    public void sendProfileInfo(Player player) {
        ServerClientTransfer sct = new ServerClientTransfer();
        sct.setIdPlayer(idClient);
        sct.setPlayer(player);
        sct.setResponsePlayerAction(ResponsePlayerAction.REFRESH_PROFILE);
        sendToClient(server.getOutStream(), sct);

    }

    public void sendStatistics(List<Player> playerList) {
        ServerClientTransfer sct = new ServerClientTransfer();
        sct.setIdPlayer(idClient);
        sct.setPlayerList(playerList);
        sct.setResponsePlayerAction(ResponsePlayerAction.REFRESH_STATISTICS);
        sendToClient(server.getOutStream(), sct);
    }

    public void findWinner() {
        if (players.entrySet().stream().allMatch(player -> player.getValue().getDicePack().isEmpty()) && winCash != 0) {
            ArrayList<String> winnersId = new ArrayList<>();
            int maxTotal = 0;
            for (Player player : players.values()) {
                if (maxTotal < player.getDicePack().getTotal()) {
                    maxTotal = player.getDicePack().getTotal();
                }
            }
            for (Player player : players.values()) {
                if (maxTotal == player.getDicePack().getTotal()) {
                    winnersId.add(player.getIdPlayer());
                }
            }
            for (String id : winnersId) {
                serverDAO.putBetCash(id, winCash / winnersId.size());
                serverDAO.addGameWon(id);
            }


            winCash = 0;
            winnersId.clear();


        }
    }


    public Consumer<ClientServerTransfer> enterMultiplayerRequest() {
        return cst -> {
            serverDAO.setPlaying(cst.getIdPlayer(), true);
            Player player = serverDAO.findPlayerById(cst.getIdPlayer());
            if (gameActive) {
                synchronized (spectators) {
                    spectators.put(this, player);
                }
                broadcastPlayers(ResponsePlayerAction.ENTER_SPECTATORS);
            } else {
                synchronized (players) {
                    players.put(this, player);
                }
                gameActive = false;
                broadcastPlayers(ResponsePlayerAction.ENTER_PLAYERS);
            }
        };
    }

    public Consumer<ClientServerTransfer> exitMultiplayerRequest() {
        return cst -> {
            serverDAO.setPlaying(cst.getIdPlayer(), false);
            if (players.containsKey(this)) {
                synchronized (players) {
                    players.remove(this);
                }
                findWinner();
            } else {
                synchronized (spectators) {
                    spectators.remove(this);
                }
            }
            if (players.size() == 0) gameActive = false;
            broadcastPlayers(ResponsePlayerAction.EXIT_MULTIPLAYER);
        };
    }

    public Consumer<ClientServerTransfer> sendMessageRequest() {
        return cst -> {
            String message = cst.getMessagePlayer();
            String sender = cst.getIdPlayer();

            Player player = serverDAO.findPlayerById(sender);
            if (player.isPlaying()) {
                String senderName = player.getNamePlayer();
                broadcastMessage(senderName, message);
            }
        };
    }

    public Consumer<ClientServerTransfer> setReadyRequest() {
        return cst -> {
            synchronized (players) {
                players.get(this).setReady(true);
            }
            if (players.entrySet().stream().allMatch((p) -> p.getValue().isReady())) {
                gameActive = true;
                players.entrySet().stream().forEach((p) -> {
                    serverDAO.takeBetCash(p.getValue().getIdPlayer(), gameBet);
                    serverDAO.addGamePlayed(p.getValue().getIdPlayer());
                    p.getValue().setDicePack(new DicePack());
                    winCash += gameBet;
                });
            } else {
                winCash = 0;
            }

            System.out.println(winCash);
            broadcastPlayers(ResponsePlayerAction.SET_READY);
        };
    }

    public Consumer<ClientServerTransfer> changeBetRequest() {
        return cst -> {
            broadcastBet(cst.getBet());
        };
    }

    public Consumer<ClientServerTransfer> refreshProfileRequest() {
        return cst -> {
            Player player = serverDAO.findPlayerById(cst.getIdPlayer());
            sendProfileInfo(player);
        };
    }

    public Consumer<ClientServerTransfer> makeThrowRequest() {
        return cst -> {
            if (cst.getIdPlayer().equals(idClient)) {
                DicePack dicePack = players.get(this).getDicePack();
                dicePack.countTotal();
                if (!dicePack.isEmpty()) {
                    dicePack.makeThrow();
                }
            }
            findWinner();
            broadcastPlayers(ResponsePlayerAction.MAKE_THROW);
        };
    }

    public Consumer<ClientServerTransfer> refreshStatisticsRequest() {
        return cst -> {
            List<Player> playerList = serverDAO.findAll();
            sendStatistics(playerList);
        };
    }

    public void load(RequestPlayerAction type, Consumer<ClientServerTransfer> handle) {
        dispatch.put(type, handle);
    }

    public void initDispatcher() {
        load(RequestPlayerAction.ENTER_MULTIPLAYER, enterMultiplayerRequest());
        load(RequestPlayerAction.EXIT_MULTIPLAYER, exitMultiplayerRequest());
        load(RequestPlayerAction.SEND_MESSAGE, sendMessageRequest());
        load(RequestPlayerAction.SET_READY, setReadyRequest());
        load(RequestPlayerAction.CHANGE_BET, changeBetRequest());
        load(RequestPlayerAction.REFRESH_PROFILE, refreshProfileRequest());
        load(RequestPlayerAction.MAKE_THROW, makeThrowRequest());
        load(RequestPlayerAction.REFRESH_STATISTICS, refreshStatisticsRequest());
    }

    public void handle(ClientServerTransfer cst) {
        dispatch.get(cst.getRequestActionPlayer()).accept(cst);
    }

    public void run() {
        try {


            boolean entered = false;
            while (!entered) {
                CSEnteringAccount csea = (CSEnteringAccount) server.getInStream().readObject();
                SCEnteringAccount scea = new SCEnteringAccount();


                if (csea.getAccountAction() == AccountAction.SIGNING_IN) {

                    idClient = serverDAO.enterAccount(csea.getLogin(), csea.getPassword());

                    if (!idClient.equals("")) {
                        scea.setId(idClient);
                        scea.setMessage(AccountSuccess.SUCCESSFUL_ENTERING);
                        entered = true;
                    } else {
                        scea.setId("");
                        scea.setMessage(AccountSuccess.UNSUCCESSFUL_ENTERING);
                    }


                } else if (csea.getAccountAction() == AccountAction.SIGNING_UP) {

                    AccountSuccess accountSuccess = serverDAO.createAccount(csea.getName(), csea.getLogin(), csea.getPassword());
                    scea.setId("");
                    scea.setMessage(accountSuccess);

                }

                server.getOutStream().writeObject(scea);
                server.getOutStream().flush();


            }


            initDispatcher();

            ClientServerTransfer cst;
            while (true) {
                cst = (ClientServerTransfer) server.getInStream().readObject();
                handle(cst);
            }

        } catch (SocketException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            serverDAO.setPlaying(idClient, false);
            synchronized (players) {
                players.remove(this);
            }
            synchronized (spectators) {
                spectators.remove(this);
            }
            if (players.size() == 0) {
                gameActive = false;
                winCash = 0;
            }
            broadcastPlayers(ResponsePlayerAction.EXIT_MULTIPLAYER);

            try {
                server.getOutStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.getClientSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
