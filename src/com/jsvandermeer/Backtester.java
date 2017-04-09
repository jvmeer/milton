package com.jsvandermeer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.jsvandermeer.Utils.dateToString;

/**
 * Created by Jacob on 4/4/2017.
 */
public class Backtester {
    ZonedDateTime startDate;
    ZonedDateTime endDate;
    Connection connection;


    Backtester(ZonedDateTime startDate, ZonedDateTime endDate, Connection connection) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.connection = connection;
    }

    void populateStrikeHistory() {
        List<ZonedDateTime> asOfs = new ArrayList<>();
        String asOfsStatement = "select distinct as_of from vix_futures where as_of>=" + dateToString(startDate) +
                " and as_of <" + dateToString(endDate);
        try {
            ResultSet asOfsSet = connection.createStatement().executeQuery(asOfsStatement);
            while (asOfsSet.next()) {
                asOfs.add(Utils.stringToDate(asOfsSet.getString("as_of")));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        for (ZonedDateTime asOf : asOfs) {
            FutureChain futureChain = new FutureChain(Utils.VIX_TICKER, asOf, connection);
            ForwardChain forwardChain = new ForwardChain(Utils.SPX_TICKER, asOf, connection);
            OptionChain futureOptionChain = new OptionChain(Utils.VIX_TICKER, asOf, connection);
            OptionChain forwardOptionChain = new OptionChain(Utils.SPX_TICKER, asOf, connection);
            for (ZonedDateTime expiry : futureChain.expiries) {
                
            }
        }
    }



}
