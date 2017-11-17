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

    public int addUser(User user) throws DaoException{
        try {
            PreparedStatement preStatement = connection.prepareStatement(
                    "INSERT INTO users(id, user_name, password) VALUES(?,?,?)");
            preStatement.setString(1, user.getId());
            preStatement.setString(2, user.getUserName());
            preStatement.setString(3, user.getPassword());
            int results = preStatement.executeUpdate();
            return results;

        } catch (SQLException e) {
            String message = "Cannot execute insert statement";
            throw new DaoException(message, e);
        }
    }

    public User getUser(String id) throws DaoException{
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT user_name, password from users WHERE id = ?");
            preparedStatement.setString(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            User user = new User(id, rs.getString("user_name"), rs.getString("password"));
            return user;
        } catch (SQLException e) {
            String message = "Cannot execute insert statement";
            throw new DaoException(message, e);
        }
    }

    public int addSession(Session session) throws DaoException {
        try {
            PreparedStatement preStatement = connection.prepareStatement(
                    "INSERT INTO sessions(id, user_id) VALUES(?,?)");
            preStatement.setString(1, session.getId());
            preStatement.setString(2, session.getUser().getId());
            int results = preStatement.executeUpdate();
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
}

