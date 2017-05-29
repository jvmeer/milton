package com.jsvandermeer;

import com.bloomberglp.blpapi.*;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Jacob on 4/1/2017.
 */
public class DataLoader {

//    private static int correlationIDCounter = 1;
//    private enum ConversationType {CHAIN, IDENTIFICATION, OPTION_MARKET, FORWARD, FUTURE, FUTURE_MARKET}
//
//
//    static void loadOptionsFromBloomberg(ZonedDateTime startDate, ZonedDateTime endDate, String[] underliers) {
//        Session session = createSession();
//        Service service = session.getService("//blp/refdata");
//        ZonedDateTime asOf = Utils.stringToZonedDateTime("20170103");
//
//        Map<String, OptionChain> chains = new HashMap<>();
//
//
//
//        for (String underlier : underliers) {
//
//            List<String> rawTickers = new ArrayList<>();
//            executeConversation(session, service, asOf, ConversationType.CHAIN, underlier, rawTickers, null,
//                    null, null);
//
//            System.out.println(rawTickers.toString());
//
//            List<String> tickers = new ArrayList<>();
//            executeConversation(session, service, asOf, ConversationType.IDENTIFICATION, underlier, rawTickers,
//                    tickers,null, null);
//
//            System.out.println(tickers.toString());
//
//            Map<String, OptionChain.Market> markets = new HashMap<>();
//            executeConversation(session, service, asOf, ConversationType.OPTION_MARKET, underlier, null,
//                    tickers, markets, null);
//
//            System.out.println(markets.toString());
//
//            Map<ZonedDateTime, Double> forwards = new HashMap<>();
//            executeConversation(session, service, asOf, ConversationType.FORWARD, underlier, null,
//                    null, null, forwards);
//
////            chains.put(underlier, new OptionChain(underlier, asOf, forwardsData, markets));
//        }
//    }
//
//    static void loadForwardsFromBloomberg(ZonedDateTime startDate, ZonedDateTime endDate, String[] underliers) {
//        Session session = createSession();
//        Service service = session.getService("//blp/refdata");
//
//        for (ZonedDateTime dateCursor = startDate; dateCursor.isBefore(endDate); dateCursor = dateCursor.plusDays(1)) {
//
//
//            Map<ZonedDateTime, Double> forwards = new HashMap<>();
//            executeConversation(session, service, dateCursor, ConversationType.FORWARD, underlier, null,
//                    null, null, forwards);
//            for (Map.Entry<ZonedDateTime, Double> entry : forwards.entrySet()) {
//                try {
//                    PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);
//                    preparedStatement.setString(1, Utils.zonedDateTimeToString(entry.getKey()));
//                    preparedStatement.setString(2, Utils.zonedDateTimeToString(dateCursor));
//                    preparedStatement.setDouble(3, entry.getValue());
//                    preparedStatement.executeUpdate();
//                } catch (SQLException exception) {
//                    exception.printStackTrace();
//                }
//            }
//        }
//    }
//
//
//
//
//
//    private static void executeConversation(Session session, Service service, ZonedDateTime asOf,
//                                            ConversationType conversationType, String underlier, List<String> rawTickers,
//                                            List<String> tickers, Map<String, OptionChain.Market> markets,
//                                            Map<ZonedDateTime, Double> forwardsData) {
//        Request request = null;
//        switch (conversationType) {
//            case CHAIN:
//                request = createChainRequest(service, underlier, asOf);
//                break;
//            case IDENTIFICATION:
//                request = createIdentificationRequest(service, rawTickers);
//                break;
//            case OPTION_MARKET:
//                request = createMarketRequest(service, tickers, asOf);
//                break;
//            case FORWARD:
//                request = createForwardRequest(service, underlier, asOf);
//                break;
//        }
//        try {
//            session.sendRequest(request, new CorrelationID(correlationIDCounter++));
//        } catch (IOException exception) {
//            exception.printStackTrace();
//        }
//
//        switch (conversationType) {
//            case CHAIN:
//                receiveResponse(session, rawTickers, null, null, conversationType);
//                break;
//            case IDENTIFICATION:
//                receiveResponse(session, tickers, null,null, conversationType);
//                break;
//            case OPTION_MARKET:
//                receiveResponse(session, null, markets, null, conversationType);
//                break;
//            case FORWARD:
//                receiveResponse(session, null, null, forwardsData, conversationType);
//                break;
//        }
//    }
//
//    private static Session createSession() {
//        SessionOptions sessionOptions = new SessionOptions();
//        sessionOptions.setServerHost("localhost");
//        sessionOptions.setServerPort(8194);
//        Session session = new Session(sessionOptions);
//
//        try {
//            session.start();
//        } catch (IOException | InterruptedException exception) {
//            exception.printStackTrace();
//        }
//        try {
//            session.openService("//blp/refdata");
//        } catch (IOException | InterruptedException exception) {
//            exception.printStackTrace();
//        }
//        return session;
//    }
//
//    private static Request createChainRequest(Service service, String underlier, ZonedDateTime asOf) {
//        Request request = service.createRequest("ReferenceDataRequest");
//        request.getElement("securities").appendValue(underlier);
//        request.getElement("fields").appendValue("OPT_CHAIN");
//        Element asOfOverride = request.getElement("overrides").appendElement();
//        asOfOverride.setElement("fieldId","SINGLE_DATE_OVERRIDE");
//        asOfOverride.setElement("value", Utils.zonedDateTimeToString(asOf));
//        Element identifierOverride = request.getElement("overrides").appendElement();
//        identifierOverride.setElement("fieldId", "DISPLAY_ID_BB_GLOBAL_OVERRIDE");
//        identifierOverride.setElement("value", "False");
//        Element chainOverride = request.getElement("overrides").appendElement();
//        chainOverride.setElement("fieldId", "OPTION_CHAIN_OVERRIDE");
//        chainOverride.setElement("value", "A");
//
//        return request;
//    }
//
//    private static Request createIdentificationRequest(Service service, List<String> tickers) {
//        Request request = service.createRequest("ReferenceDataRequest");
//        tickers.forEach((ticker) -> {
//            request.getElement("securities").appendValue(ticker);
//        });
//        request.getElement("fields").appendValue("SECURITY_DES");
//        return request;
//    }
//
//    private static Request createMarketRequest(Service service, List<String> tickers, ZonedDateTime asOf) {
//        Request request = service.createRequest("HistoricalDataRequest");
//        tickers.forEach((ticker) -> {
//            request.getElement("securities").appendValue(ticker);
//        });
//        Element fields = request.getElement("fields");
//        fields.appendValue("PX_BID");
//        fields.appendValue("PX_ASK");
//        fields.appendValue("BID_SIZE");
//        fields.appendValue("ASK_SIZE");
//        request.set("startDate", Utils.zonedDateTimeToString(asOf));
//        request.set("endDate", Utils.zonedDateTimeToString(asOf));
//        return request;
//    }
//
//    private static Request createForwardRequest(Service service, String underlier, ZonedDateTime asOf) {
//        Request request = service.createRequest("ReferenceDataRequest");
//        request.getElement("securities").appendValue(underlier);
//        request.getElement("fields").appendValue("IMP_FORWARD_PRICE");
//        Element asOfOverride = request.getElement("overrides").appendElement();
//        asOfOverride.setElement("fieldId","REFERENCE_DATE");
//        asOfOverride.setElement("value",Utils.zonedDateTimeToString(asOf));
//        Element axisOverride = request.getElement("overrides").appendElement();
//        axisOverride.setElement("fieldId", "IVOL_SURFACE_AXIS_TYPE");
//        axisOverride.setElement("value", "Listed/Pct");
//        return request;
//    }
//
//
//
//    private static void receiveResponse(Session session, List<String> tickers, Map<String, OptionChain.Market> markets,
//                                        Map<ZonedDateTime, Double> forwardsData, ConversationType conversationType) {
//        boolean continueToLoop = true;
//        while (continueToLoop) {
//            Event event = null;
//            try {
//                event = session.nextEvent();
//            } catch (InterruptedException exception) {
//                exception.printStackTrace();
//            }
//            switch (event.eventType().intValue()) {
//                case Event.EventType.Constants.RESPONSE: //final event
//                    continueToLoop = false; //fall through
//                case Event.EventType.Constants.PARTIAL_RESPONSE:
//                    switch (conversationType) {
//                        case CHAIN:
//                            handleChainResponse(event, tickers);
//                            break;
//                        case IDENTIFICATION:
//                            handleIdentificationResponse(event, tickers);
//                            break;
//                        case OPTION_MARKET:
//                            handleMarketResponse(event, markets);
//                            break;
//                        case FORWARD:
//                            handleForwardResponse(event, forwardsData);
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//    private static void handleChainResponse(Event event, List<String> tickers) {
//        MessageIterator iter = event.messageIterator();
//        while (iter.hasNext()) {
//            Message message = iter.next();
//            System.out.println(message.toString());
//            Element responseData = message.getElement("securityData").getValueAsElement();
//            Element responseTickers = responseData.getElement("fieldData").getElement("OPT_CHAIN");
//            for (int i = 0; i < responseTickers.numValues(); ++i) {
//                Element responseTicker = responseTickers.getValueAsElement(i);
//                tickers.add(responseTicker.getElement("Security Description").getValueAsString());
//            }
//        }
//    }
//
//    private static void handleIdentificationResponse(Event event, List<String> tickers) {
//        MessageIterator iter = event.messageIterator();
//        while (iter.hasNext()) {
//            Message message = iter.next();
//            Element securityData = message.getElement("securityData");
//            for (int i = 0; i < securityData.numValues(); ++i) {
//                tickers.add(securityData.getValueAsElement(i).getElement("fieldData").getElement("SECURITY_DES").getValueAsString() + " Index");
//            }
//        }
//    }
//
//    private static void handleMarketResponse(Event event, Map<String, OptionChain.Market> markets) {
//            MessageIterator iter = event.messageIterator();
//        while (iter.hasNext()) {
//            Message message = iter.next();
//            System.out.println(message.toString());
//            Element responseData = message.getElement("securityData");
//            String ticker = responseData.getElement("security").getValueAsString();
//            Element fieldData = responseData.getElement("fieldData").getValueAsElement();
//            double bidPrice;
//            try {
//                bidPrice = fieldData.getElement("PX_BID").getValueAsFloat64();
//            } catch (NotFoundException exception) {
//                bidPrice = 0;
//            }
//            double askPrice;
//            try {
//                askPrice = fieldData.getElement("PX_ASK").getValueAsFloat64();
//            } catch (NotFoundException exception) {
//                askPrice = bidPrice;
//            }
//            int bidSize;
//            try {
//                bidSize = fieldData.getElement("PX_BID").getValueAsInt32();
//            } catch (NotFoundException exception) {
//                bidSize = 0;
//            }
//            int askSize;
//            try {
//                askSize = fieldData.getElement("PX_BID").getValueAsInt32();
//            } catch (NotFoundException exception) {
//                askSize = 0;
//            }
//            markets.put(ticker, new OptionChain.Market(bidPrice, askPrice, bidSize, askSize));
//        }
//    }
//
//    private static void handleForwardResponse(Event event, Map<ZonedDateTime, Double> forwards) {
//        MessageIterator iter = event.messageIterator();
//        while (iter.hasNext()) {
//            Message message = iter.next();
//            Element responseData = message.getElement("securityData").getValueAsElement();
//            Element forwardsData = responseData.getElement("fieldData").getElement("IMP_FORWARD_PRICE");
//            double spot = 0.0;
//            for (int i = 0; i < forwardsData.numValues(); ++i) {
//                Element forward = forwardsData.getValueAsElement(i);
//                if (i == 0) spot = forward.getElement("Implied Forward Price").getValueAsFloat64();
//                forwards.put(Utils.stringToZonedDateTime(forward.getElementAsString("Expiration Date")),
//                        forward.getElementAsFloat64("Implied Forward Price"));
//            }
//            System.out.println(message.toString());
//        }
//    }
//
//
//    private static void printResponse(Event event) {
//        MessageIterator iter = event.messageIterator();
//        while (iter.hasNext()) {
//            System.out.println(iter.next().toString());
//        }
//    }


    static void loadHolidays() {
        File file = new File(Utils.HOLIDAY_CAL_LOCAL_FILE_PATH);
        DataInterface dataInterface = DataInterface.getInstance();
        Set<LocalDate> holidays = new HashSet<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) break;
                LocalDate date = Utils.stringToLocalDate(line);
                holidays.add(date);
            }
            bufferedReader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        dataInterface.insertHolidays(holidays);
    }

    static void loadFuturesFromBloomberg(LocalDate startDate, LocalDate endDate) {
        BloombergInterface bloombergInterface = BloombergInterface.getInstance();
        Set<DataInterface.FutureLine> futureLines = bloombergInterface.retrieveFutureLines(startDate,
                endDate, Utils.Underlier.VIX);
        DataInterface dataInterface = DataInterface.getInstance();
        dataInterface.insertFutures(futureLines);
    }

    static void loadOptionsFromLocal() {
        Map<String, Utils.Underlier> underlierMap = Utils.underlierMap();
        File localDirectory = new File(Utils.LIVE_VOL_LOCAL_DIRECTORY);
        DataInterface dataInterface = DataInterface.getInstance();
        for (File file : localDirectory.listFiles()) {
            System.out.println(file.getName());
            Set<DataInterface.OptionLine> optionLines = new HashSet<>();
            try {
                ZipFile zipFile = new ZipFile(file);
                ZipEntry zipEntry = zipFile.entries().nextElement();
                InputStream inputStream = zipFile.getInputStream(zipEntry);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) break;
                    String[] cells = line.split(",");
                    String root = cells[LiveVolColumn.ROOT.columnNumber];
                    String ticker = extractSubstringFromCollection(root, underlierMap.keySet());
                    if (ticker != null) {
                        Utils.Underlier underlier = underlierMap.get(ticker);
                        String expiry = cells[LiveVolColumn.EXPIRATION.columnNumber] + "T" +
                                (root.substring(root.length() - 1).equals("W") ? underlier.alternateExpiryTime :
                                underlier.primaryExpiryTime) + "[" + underlier.timeZoneId + "]";
                        double strike = Double.parseDouble(cells[LiveVolColumn.STRIKE.columnNumber]);
                        boolean isCall = cells[LiveVolColumn.OPTION_TYPE.columnNumber].equals("C");
                        String asOf = cells[LiveVolColumn.QUOTE_DATE.columnNumber] + "T" + underlier.endOfDayTime +
                                "[" + underlier.timeZoneId + "]";
                        double bidPrice = Double.parseDouble(cells[LiveVolColumn.BID_EOD.columnNumber]);
                        double askPrice = Double.parseDouble(cells[LiveVolColumn.ASK_EOD.columnNumber]);
                        long bidSize = Integer.parseInt(cells[LiveVolColumn.BID_SIZE_EOD.columnNumber]);
                        long askSize = Integer.parseInt(cells[LiveVolColumn.ASK_SIZE_EOD.columnNumber]);
                        DataInterface.OptionLine optionLine = new DataInterface.OptionLine(ticker, expiry, strike,
                                isCall, asOf, bidPrice, askPrice, bidSize, askSize);
                        optionLines.add(optionLine);
                    }
                }
                bufferedReader.close();
                inputStream.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            dataInterface.insertOptions(optionLines);
        }
    }

    private static String extractSubstringFromCollection(String string, Collection<String> substrings) {
        for (String substring : substrings) {
            if (string.contains(substring)) return substring;
        }
        return null;
    }

    static void retrieveFilesFromLiveVol(String remoteDirectory) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(Utils.LIVE_VOL_ADDRESS);
            ftpClient.login(Utils.LIVE_VOL_USERNAME, Utils.LIVE_VOL_PASSWORD);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.changeWorkingDirectory(remoteDirectory);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.isFile()) {
                    String remoteName = ftpFile.getName();
                    String localName = Utils.LIVE_VOL_LOCAL_DIRECTORY + remoteName;
                    OutputStream outputStream = new FileOutputStream(new File(localName));
                    ftpClient.retrieveFile(remoteName, outputStream);
                    outputStream.close();
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

    private enum LiveVolColumn {
        QUOTE_DATE(1), ROOT(2), EXPIRATION(3), STRIKE(4), OPTION_TYPE(5), BID_SIZE_1545(11), BID_1545(12),
        ASK_SIZE_1545(13), ASK_1545(14), BID_SIZE_EOD(17), BID_EOD(18), ASK_SIZE_EOD(19), ASK_EOD(20);
        final int columnNumber;
        LiveVolColumn(int columnNumber) {
            this.columnNumber = columnNumber;
        }
    }

}
