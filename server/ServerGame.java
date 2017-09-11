package by.bsu.mmf.badthrowgame.server;

import by.bsu.mmf.badthrowgame.player.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lenovo on 09/11/2017.
 */
public class ServerGame {
    public static final Map<ServerHandler, Player> players = Collections.synchronizedMap(new HashMap<>());
    public static final Map<ServerHandler, Player> spectators = Collections.synchronizedMap(new HashMap<>());

    public static Boolean gameActive = false;
    public static Integer gameBet = 100;
    public static Integer winCash = 0;
}
