package com.codecool.dao;

import com.codecool.model.Session;
import com.codecool.model.User;

import java.sql.*;

public class Dao {

    private static final String URL = "jdbc:sqlite:src/main/resources/db/usersDb.db";
    private static final String DRIVER_CLASS_NAME = "org.sqlite.JDBC";

    private Connection connection;

    public Dao() throws DaoException {

        try {
            Class.forName(DRIVER_CLASS_NAME);
            this.connection = DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            String message = "Couldn't find db driver";
            throw new DaoException(message, e);
        } catch (SQLException e) {
            String message = "Couldn't establish connection";
            throw new DaoException(message, e);
        }
    }

    public void closeConnections() throws DaoException{
        try {
            connection.close();
        } catch (SQLException e) {
            String message = "Couldn't close connection or connection is already closed";
            throw new DaoException(message, e);
        }
    }

    public void addUser(User user) throws DaoException{
        String statement = "INSERT INTO users(id, user_name, password) VALUES(?,?,?);";
        try(PreparedStatement preStatement = connection.prepareStatement(statement)){

            preStatement.setString(1, user.getId());
            preStatement.setString(2, user.getUserName());
            preStatement.setString(3, user.getPassword());
            preStatement.executeUpdate();
            preStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            String message = "Cannot execute insert statement";
            throw new DaoException(message, e);
        }
    }

    public User getUser(String id) throws DaoException{
        String statement = "SELECT user_name, password from users WHERE id = ?;";

        try(PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setString(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            User user = new User(id, rs.getString("user_name"), rs.getString("password"));
            return user;
        } catch (SQLException e) {
            String message = "Cannot execute select statement";
            throw new DaoException(message, e);
        }
    }

    public int addSession(Session session) throws DaoException {
        String statement = "INSERT INTO sessions(id, user_id) VALUES(?,?);";
        try(PreparedStatement preStatement = connection.prepareStatement(statement)) {
            preStatement.setString(1, session.getId());
            preStatement.setString(2, session.getUser().getId());
            int results = preStatement.executeUpdate();
            preStatement.close();
            return results;


        } catch (SQLException e) {
            String message = "Cannot execute insert statement";
            throw new DaoException(message, e);
        }
    }

    public void addSessionAndUser(Session session) throws DaoException{
        User user = session.getUser();
        addUser(user);
        addSession(session);
    }

    public int deleteSession(String id) throws DaoException {
        String statement = "DELETE FROM sessions WHERE id = ?;";

        try(PreparedStatement preparedStatement = connection.prepareStatement(statement)) {

            preparedStatement.setString(1, id);
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            return result;
        } catch (SQLException e) {
            String message = "Cannot execute session delete";
            throw new DaoException(message, e);
        }
    }

    public boolean IsSessionInDb(String sessionId) throws DaoException{
        String query = "SELECT * FROM sessions WHERE id =?";

        try(PreparedStatement preStatement = connection.prepareStatement(query);) {
            preStatement.setString(1, sessionId);
            ResultSet sessionStatus = preStatement.executeQuery();

            boolean hasSession;
            hasSession = sessionStatus.next();
            preStatement.close();
            return hasSession;

        } catch (SQLException e) {
            throw new DaoException("Cant connct", e);
        }
    }

    public User getUserBySession(String sessionId) throws DaoException {
        String query = "SELECT user_name, password FROM  users " +
                "JOIN sessions ON users.id = sessions.user_id " +
                "WHERE sessions.id = ?";

        try(PreparedStatement preStatement = connection.prepareStatement(query);) {

//            PreparedStatement preStatement = connection.prepareStatement(query);
            preStatement.setString(1, sessionId);
            ResultSet resultSet = preStatement.executeQuery();

            if(resultSet.next()){
                String userName = resultSet.getString("user_name");
                String password = resultSet.getString("password");
                return new User(userName, password);
            } else {
                return null;
            }


        } catch (SQLException e) {
            String message = "Cannot execute query";
            throw new DaoException(message, e);
        }
    }
}

