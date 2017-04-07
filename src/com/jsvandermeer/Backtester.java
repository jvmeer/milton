package com.jsvandermeer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 4/4/2017.
 */
public class Backtester {

    private Connection connection = null;

    Backtester() {
        try {
            connection = DriverManager.getConnection(Utils.DATABASE_PATH);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    void populateReplicationHistoryTable() {
        List<ZonedDateTime> asOfs = new ArrayList<>();
        String asOfsStatement = "select distinct as_of from vix_futures";
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(asOfsStatement);
            while (resultSet.next()) {
                asOfs.add(Utils.stringToDate(resultSet.getString("as_of")));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        for (ZonedDateTime asOf : asOfs) {
            List<>
            String vixFuturesStatement = "select * from vix_futures where as_of=" + Utils.dateToString(asOf);
            String spxForwardsStatement = "select * from spx_forwards where as_of=" + Utils.dateToString(asOf);
            String optionsStatement = "select * from options where as_of=" + Utils.dateToString(asOf);
        }
    }



}
