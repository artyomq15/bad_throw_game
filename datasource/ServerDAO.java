package by.bsu.mmf.badthrowgame.datasource;

import by.bsu.mmf.badthrowgame.enums.AccountSuccess;
import by.bsu.mmf.badthrowgame.player.Player;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 08/16/2017.
 */
public class ServerDAO implements AbstractDAO {

    public static final String SELECT_ALL_PLAYERS = "SELECT * FROM client ORDER BY 100*gamesWon/games DESC";
    public static final String SELECT_PLAYER_ID = "SELECT idClient FROM account WHERE login=? AND password=?";

    public static final String SELECT_PLAYER_BY_ID = "SELECT * FROM client WHERE idClient=?";

    public static final String SELECT_MAX_ID = "SELECT MAX(idClient) FROM account";
    public static final String CHECK_UNIQUE_LOGIN = "SELECT idClient FROM account WHERE login=?";

    public static final String CREATE_ACCOUNT = "INSERT INTO account (idClient, login, password) VALUES (?,?,?)";
    public static final String CREATE_CLIENT = "INSERT INTO client (name, idClient) VALUES (?,?)";

    public static final String SET_PLAYING = "UPDATE client SET isPlaying=? WHERE idClient=?";

    public static final String TAKE_BET_CASH = "UPDATE client SET cash=cash-? WHERE idClient=?";
    public static final String PUT_BET_CASH = "UPDATE client SET cash=cash+? WHERE idClient=?";

    public static final String ADD_GAME_PLAYED = "UPDATE client SET games=games+1 WHERE idClient=?";
    public static final String ADD_GAME_WON = "UPDATE client SET gamesWon=gamesWon+1 WHERE idClient=?";



    @Override
    public List<Player> findAll() {
        List<Player> players = new ArrayList<>();
        Connection connection = null;
        MysqlDataSource mysqlDataSource = DataSourceFactory.getDataSource();
        Statement statement = null;
        try {
            connection = mysqlDataSource.getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_PLAYERS);
            while (resultSet.next()) {
                Player player = new Player();
                player.setNamePlayer(resultSet.getString(1));
                player.setCashPlayer(resultSet.getInt(2));
                player.setGamesPlayed(resultSet.getInt(3));
                player.setGamesWon(resultSet.getInt(4));
                player.setPlaying(resultSet.getBoolean(5));
                player.setIdPlayer(resultSet.getString(6));
                players.add(player);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return players;
    }

    @Override
    public String enterAccount(String login, char[] password) {
        String idPlayer = null;
        String passwordString = String.valueOf(password);

        Connection connection = null;
        MysqlDataSource mysqlDataSource = DataSourceFactory.getDataSource();
        PreparedStatement statement = null;

        try {
            connection = mysqlDataSource.getConnection();
            statement = connection.prepareStatement(SELECT_PLAYER_ID);
            statement.setString(1, login);
            statement.setString(2, passwordString);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("entering...");
            if (resultSet.next()) {
                idPlayer = resultSet.getString(1);
            } else {
                idPlayer = "";
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return idPlayer;
    }

    @Override
    public AccountSuccess createAccount(String name, String login, char[] password) {
        AccountSuccess accountSuccess = AccountSuccess.UNSUCCESSFUL_REGISTER;
        String max = null;
        ResultSet resultSet;

        String passwordString = String.valueOf(password);

        Connection connection = null;
        MysqlDataSource mysqlDataSource = DataSourceFactory.getDataSource();
        PreparedStatement statement = null;
        try {
            connection = mysqlDataSource.getConnection();

            statement = connection.prepareStatement(SELECT_MAX_ID);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                max = resultSet.getString(1);
                System.out.println(max);
            }

            Integer maxId = Integer.parseInt(max);
            max = String.valueOf(++maxId);


            statement = connection.prepareStatement(CHECK_UNIQUE_LOGIN);
            statement.setString(1, login);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                accountSuccess = AccountSuccess.UNSUCCESSFUL_REGISTER;

            } else {
                statement = connection.prepareStatement(CREATE_ACCOUNT);
                statement.setString(1, max);
                statement.setString(2, login);
                statement.setString(3, passwordString);
                statement.executeUpdate();

                statement = connection.prepareStatement(CREATE_CLIENT);
                statement.setString(1, name);
                statement.setString(2, max);
                statement.executeUpdate();
                accountSuccess = AccountSuccess.SUCCESSFUL_REGISTER;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return accountSuccess;
    }

    @Override
    public Player findPlayerById(String id) {
        Player player = new Player();

        Connection connection = null;
        MysqlDataSource mysqlDataSource = DataSourceFactory.getDataSource();
        PreparedStatement statement = null;

        try {
            connection = mysqlDataSource.getConnection();
            statement = connection.prepareStatement(SELECT_PLAYER_BY_ID);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                player.setNamePlayer(resultSet.getString(1));
                player.setCashPlayer(resultSet.getInt(2));
                player.setGamesPlayed(resultSet.getInt(3));
                player.setGamesWon(resultSet.getInt(4));
                player.setPlaying(resultSet.getBoolean(5));
                player.setIdPlayer(resultSet.getString(6));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return player;
    }

    public void setPlaying(String id, boolean isPlaying) {
        Connection connection = null;
        MysqlDataSource mysqlDataSource = DataSourceFactory.getDataSource();
        PreparedStatement statement = null;

        try {
            connection = mysqlDataSource.getConnection();
            statement = connection.prepareStatement(SET_PLAYING);
            statement.setBoolean(1, isPlaying);
            statement.setString(2, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    @Override
    public void takeBetCash(String id, int cash){
        changeCash(id, cash, TAKE_BET_CASH);
    }

    @Override
    public void putBetCash(String id, int cash){
        changeCash(id, cash, PUT_BET_CASH);
    }

    public void changeCash(String id, int cash, String sql){
        Connection connection = null;
        MysqlDataSource mysqlDataSource = DataSourceFactory.getDataSource();
        PreparedStatement statement = null;

        try {
            connection = mysqlDataSource.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, cash);
            statement.setString(2, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void addGamePlayed(String id){
        addGame(id, ADD_GAME_PLAYED);
    }

    @Override
    public void addGameWon(String id){
        addGame(id, ADD_GAME_WON);
    }

    public void addGame(String id, String sql){
        Connection connection = null;
        MysqlDataSource mysqlDataSource = DataSourceFactory.getDataSource();
        PreparedStatement statement = null;
        try{
            connection = mysqlDataSource.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            statement.executeUpdate();
        }catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

}



