package com.jsvandermeer;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Jacob on 4/1/2017.
 */
public class History {

    public SortedSet<Replication> replications;
    Connection connection;

    public History(ZonedDateTime startDate, ZonedDateTime endDate, String databasePath) {
        replications = new TreeSet<>();
        connection = null;
        try {
            connection = DriverManager.getConnection(databasePath);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    void test() {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM options LIMIT 5");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("identifier"));
            }
        } catch (SQLException exception){
            exception.printStackTrace();
        }
    }


}
