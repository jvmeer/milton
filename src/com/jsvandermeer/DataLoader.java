package com.jsvandermeer;

import com.bloomberglp.blpapi.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * Created by Jacob on 4/1/2017.
 */
public class DataLoader {

    private static int correlationIDCounter = 1;
    private enum ConversationType {CHAIN, IDENTIFICATION, OPTION_MARKET, FORWARD, FUTURE, FUTURE_MARKET}


    static void loadOptionsFromBloomberg(ZonedDateTime startDate, ZonedDateTime endDate, String[] underliers) {
        Session session = createSession();
        Service service = session.getService("//blp/refdata");
        ZonedDateTime asOf = Utils.stringToDate("20170103");

        Map<String, OptionChain> chains = new HashMap<>();



        for (String underlier : underliers) {

            List<String> rawTickers = new ArrayList<>();
            executeConversation(session, service, asOf, ConversationType.CHAIN, underlier, rawTickers, null,
                    null, null);

            System.out.println(rawTickers.toString());

            List<String> tickers = new ArrayList<>();
            executeConversation(session, service, asOf, ConversationType.IDENTIFICATION, underlier, rawTickers,
                    tickers,null, null);

            System.out.println(tickers.toString());

            Map<String, OptionChain.Market> markets = new HashMap<>();
            executeConversation(session, service, asOf, ConversationType.OPTION_MARKET, underlier, null,
                    tickers, markets, null);

            System.out.println(markets.toString());

            Map<ZonedDateTime, Double> forwards = new HashMap<>();
            executeConversation(session, service, asOf, ConversationType.FORWARD, underlier, null,
                    null, null, forwards);

//            chains.put(underlier, new OptionChain(underlier, asOf, forwardsData, markets));
        }
    }

    static void loadForwards(ZonedDateTime startDate, ZonedDateTime endDate, String databasePath) {
        Session session = createSession();
        Service service = session.getService("//blp/refdata");

        String newDatabasePath = "jdbc:sqlite:C:\\Users\\Jacob\\Dropbox\\Code\\milton\\history"
                + ZonedDateTime.now().toString() + ".db";
        Connection connection = null;
        try {
            if (databasePath == null) {
                connection = DriverManager.getConnection(newDatabasePath);
            } else {
                connection = DriverManager.getConnection(databasePath);
            }
            connection.createStatement().execute("create table if not exists spx_forwards (expiry text," +
                    "as_of text, forward real, primary key (expiry, as_of))");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }


        String insertStatement = "insert into spx_forwards(expiry, as_of, forward) values(?, ?, ?)";
        String underlier = Utils.SPX_TICKER;

        for (ZonedDateTime dateCursor = startDate; dateCursor.isBefore(endDate); dateCursor = dateCursor.plusDays(1)) {


            Map<ZonedDateTime, Double> forwards = new HashMap<>();
            executeConversation(session, service, dateCursor, ConversationType.FORWARD, underlier, null,
                    null, null, forwards);
            for (Map.Entry<ZonedDateTime, Double> entry : forwards.entrySet()) {
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);
                    preparedStatement.setString(1, Utils.dateToString(entry.getKey()));
                    preparedStatement.setString(2, Utils.dateToString(dateCursor));
                    preparedStatement.setDouble(3, entry.getValue());
                    preparedStatement.executeUpdate();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }





    private static void executeConversation(Session session, Service service, ZonedDateTime asOf,
                                            ConversationType conversationType, String underlier, List<String> rawTickers,
                                            List<String> tickers, Map<String, OptionChain.Market> markets,
                                            Map<ZonedDateTime, Double> forwardsData) {
        Request request = null;
        switch (conversationType) {
            case CHAIN:
                request = createChainRequest(service, underlier, asOf);
                break;
            case IDENTIFICATION:
                request = createIdentificationRequest(service, rawTickers);
                break;
            case OPTION_MARKET:
                request = createMarketRequest(service, tickers, asOf);
                break;
            case FORWARD:
                request = createForwardRequest(service, underlier, asOf);
                break;
        }
        try {
            session.sendRequest(request, new CorrelationID(correlationIDCounter++));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        switch (conversationType) {
            case CHAIN:
                receiveResponse(session, rawTickers, null, null, conversationType);
                break;
            case IDENTIFICATION:
                receiveResponse(session, tickers, null,null, conversationType);
                break;
            case OPTION_MARKET:
                receiveResponse(session, null, markets, null, conversationType);
                break;
            case FORWARD:
                receiveResponse(session, null, null, forwardsData, conversationType);
                break;
        }
    }

    private static Session createSession() {
        SessionOptions sessionOptions = new SessionOptions();
        sessionOptions.setServerHost("localhost");
        sessionOptions.setServerPort(8194);
        Session session = new Session(sessionOptions);

        try {
            session.start();
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
        try {
            session.openService("//blp/refdata");
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
        return session;
    }

    private static Request createChainRequest(Service service, String underlier, ZonedDateTime asOf) {
        Request request = service.createRequest("ReferenceDataRequest");
        request.getElement("securities").appendValue(underlier);
        request.getElement("fields").appendValue("OPT_CHAIN");
        Element asOfOverride = request.getElement("overrides").appendElement();
        asOfOverride.setElement("fieldId","SINGLE_DATE_OVERRIDE");
        asOfOverride.setElement("value", Utils.dateToString(asOf));
        Element identifierOverride = request.getElement("overrides").appendElement();
        identifierOverride.setElement("fieldId", "DISPLAY_ID_BB_GLOBAL_OVERRIDE");
        identifierOverride.setElement("value", "False");
        Element chainOverride = request.getElement("overrides").appendElement();
        chainOverride.setElement("fieldId", "OPTION_CHAIN_OVERRIDE");
        chainOverride.setElement("value", "A");

        return request;
    }

    private static Request createIdentificationRequest(Service service, List<String> tickers) {
        Request request = service.createRequest("ReferenceDataRequest");
        tickers.forEach((ticker) -> {
            request.getElement("securities").appendValue(ticker);
        });
        request.getElement("fields").appendValue("SECURITY_DES");
        return request;
    }

    private static Request createMarketRequest(Service service, List<String> tickers, ZonedDateTime asOf) {
        Request request = service.createRequest("HistoricalDataRequest");
        tickers.forEach((ticker) -> {
            request.getElement("securities").appendValue(ticker);
        });
        Element fields = request.getElement("fields");
        fields.appendValue("PX_BID");
        fields.appendValue("PX_ASK");
        fields.appendValue("BID_SIZE");
        fields.appendValue("ASK_SIZE");
        request.set("startDate", Utils.dateToString(asOf));
        request.set("endDate", Utils.dateToString(asOf));
        return request;
    }

    private static Request createForwardRequest(Service service, String underlier, ZonedDateTime asOf) {
        Request request = service.createRequest("ReferenceDataRequest");
        request.getElement("securities").appendValue(underlier);
        request.getElement("fields").appendValue("IMP_FORWARD_PRICE");
        Element asOfOverride = request.getElement("overrides").appendElement();
        asOfOverride.setElement("fieldId","REFERENCE_DATE");
        asOfOverride.setElement("value",Utils.dateToString(asOf));
        Element axisOverride = request.getElement("overrides").appendElement();
        axisOverride.setElement("fieldId", "IVOL_SURFACE_AXIS_TYPE");
        axisOverride.setElement("value", "Listed/Pct");
        return request;
    }



    private static void receiveResponse(Session session, List<String> tickers, Map<String, OptionChain.Market> markets,
                                        Map<ZonedDateTime, Double> forwardsData, ConversationType conversationType) {
        boolean continueToLoop = true;
        while (continueToLoop) {
            Event event = null;
            try {
                event = session.nextEvent();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            switch (event.eventType().intValue()) {
                case Event.EventType.Constants.RESPONSE: //final event
                    continueToLoop = false; //fall through
                case Event.EventType.Constants.PARTIAL_RESPONSE:
                    switch (conversationType) {
                        case CHAIN:
                            handleChainResponse(event, tickers);
                            break;
                        case IDENTIFICATION:
                            handleIdentificationResponse(event, tickers);
                            break;
                        case OPTION_MARKET:
                            handleMarketResponse(event, markets);
                            break;
                        case FORWARD:
                            handleForwardResponse(event, forwardsData);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static void handleChainResponse(Event event, List<String> tickers) {
        MessageIterator iter = event.messageIterator();
        while (iter.hasNext()) {
            Message message = iter.next();
            System.out.println(message.toString());
            Element responseData = message.getElement("securityData").getValueAsElement();
            Element responseTickers = responseData.getElement("fieldData").getElement("OPT_CHAIN");
            for (int i = 0; i < responseTickers.numValues(); ++i) {
                Element responseTicker = responseTickers.getValueAsElement(i);
                tickers.add(responseTicker.getElement("Security Description").getValueAsString());
            }
        }
    }

    private static void handleIdentificationResponse(Event event, List<String> tickers) {
        MessageIterator iter = event.messageIterator();
        while (iter.hasNext()) {
            Message message = iter.next();
            Element securityData = message.getElement("securityData");
            for (int i = 0; i < securityData.numValues(); ++i) {
                tickers.add(securityData.getValueAsElement(i).getElement("fieldData").getElement("SECURITY_DES").getValueAsString() + " Index");
            }
        }
    }

    private static void handleMarketResponse(Event event, Map<String, OptionChain.Market> markets) {
        MessageIterator iter = event.messageIterator();
        while (iter.hasNext()) {
            Message message = iter.next();
            System.out.println(message.toString());
            Element responseData = message.getElement("securityData");
            String ticker = responseData.getElement("security").getValueAsString();
            Element fieldData = responseData.getElement("fieldData").getValueAsElement();
            double bidPrice;
            try {
                bidPrice = fieldData.getElement("PX_BID").getValueAsFloat64();
            } catch (NotFoundException exception) {
                bidPrice = 0;
            }
            double askPrice;
            try {
                askPrice = fieldData.getElement("PX_ASK").getValueAsFloat64();
            } catch (NotFoundException exception) {
                askPrice = bidPrice;
            }
            int bidSize;
            try {
                bidSize = fieldData.getElement("PX_BID").getValueAsInt32();
            } catch (NotFoundException exception) {
                bidSize = 0;
            }
            int askSize;
            try {
                askSize = fieldData.getElement("PX_BID").getValueAsInt32();
            } catch (NotFoundException exception) {
                askSize = 0;
            }
            markets.put(ticker, new OptionChain.Market(bidPrice, askPrice, bidSize, askSize));
        }
    }

    private static void handleForwardResponse(Event event, Map<ZonedDateTime, Double> forwards) {
        MessageIterator iter = event.messageIterator();
        while (iter.hasNext()) {
            Message message = iter.next();
            Element responseData = message.getElement("securityData").getValueAsElement();
            Element forwardsData = responseData.getElement("fieldData").getElement("IMP_FORWARD_PRICE");
            double spot = 0.0;
            for (int i = 0; i < forwardsData.numValues(); ++i) {
                Element forward = forwardsData.getValueAsElement(i);
                if (i == 0) spot = forward.getElement("Implied Forward Price").getValueAsFloat64();
                forwards.put(Utils.stringToDate(forward.getElementAsString("Expiration Date")),
                        forward.getElementAsFloat64("Implied Forward Price"));
            }
            System.out.println(message.toString());
        }
    }


    private static void printResponse(Event event) {
        MessageIterator iter = event.messageIterator();
        while (iter.hasNext()) {
            System.out.println(iter.next().toString());
        }
    }




    static void loadOptionsFromLiveVol(String directory) {
        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect("ftp.datashop.livevol.com");
            ftpClient.login("jsvmeer@gmail.com", "courageandhonor");
            System.out.println(ftpClient.isConnected());
            ftpClient.changeWorkingDirectory(directory);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.isFile()) {
                    String name = ftpFile.getName();
                    InputStream inputStream = ftpClient.retrieveFileStream(name);
                    ftpClient.completePendingCommand();
                    ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                    System.out.println(zipInputStream.getNextEntry().getName());
                    zipInputStream.close();
                    inputStream.close();
                    //                ZipInputStream zipInputStream = new ZipInputStream(ftpClient.retrieveFileStream(name));
                    //                System.out.println(zipInputStream.getNextEntry().toString());
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();

        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

}
