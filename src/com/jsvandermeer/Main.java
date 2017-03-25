package com.jsvandermeer;


import com.bloomberglp.blpapi.*;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    private static int correlationIDCounter = 1;

    static final String VIX_TICKER = "VIX Index";
    static final String SPX_TICKER = "SPX Index";
    private static final String EXPIRY_TIME = "131500";
    private static final String TIME_ZONE = "America/Chicago";

    public static void main(String[] args) {
        Session session = createSession();
        Service service = session.getService("//blp/refdata");
        Request request = createChainRequest(service, SPX_TICKER, stringToDate("20170103"));
        try {
            session.sendRequest(request, new CorrelationID(correlationIDCounter++));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        ArrayList<String> tickers = new ArrayList<>();
        receiveResponse(session, tickers, null);

        System.out.println(tickers.toString());

        request = createMarketRequest(service, tickers, stringToDate("20170103"));

        try {
            session.sendRequest(request, new CorrelationID(correlationIDCounter++));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        HashMap<String, Chain.Market> markets = new HashMap<>();
        receiveResponse(session, null, markets);

        System.out.println(markets.toString());

        Chain chain = new Chain();
        System.out.println("Hello world!");
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
        asOfOverride.setElement("value", dateToString(asOf));

        return request;
    }

    private static Request createMarketRequest(Service service, ArrayList<String> tickers, ZonedDateTime asOf) {
        Request request = service.createRequest("HistoricalDataRequest");
        tickers.forEach((ticker) -> {
            request.getElement("securities").appendValue(ticker);
        });
        Element fields = request.getElement("fields");
        fields.appendValue("PX_BID");
        fields.appendValue("PX_ASK");
        request.set("startDate", dateToString(asOf));
        request.set("endDate", dateToString(asOf));
        return request;
    }

    private static String dateToString(ZonedDateTime date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        return year + (month < 10 ? "0" : "") + month + (day < 10 ? "0" : "") + day;
    }

    private static ZonedDateTime stringToDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuuMMdd HHmmss VV");
        return ZonedDateTime.parse(date + " " + EXPIRY_TIME + " " + TIME_ZONE, formatter);
    }

    private static void receiveResponse(Session session, ArrayList<String> tickers, HashMap<String, Chain.Market> markets) {
        boolean continueToLoop = true;
        while (continueToLoop) {
            Event event = null;
            try {
                event = session.nextEvent();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            System.out.println(event.toString());
            System.out.println(event.eventType().toString());
            switch (event.eventType().intValue()) {
                case Event.EventType.Constants.RESPONSE: //final event
                    continueToLoop = false; //fall through
                case Event.EventType.Constants.PARTIAL_RESPONSE:
                    if (markets == null) {
                        handleChainResponse(event, tickers);
                    } else {
                        handleMarketResponse(event, markets);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static void handleChainResponse(Event event, ArrayList<String> tickers) {
        MessageIterator iter = event.messageIterator();
        while (iter.hasNext()) {
            Message message = iter.next();
            Element responseData = message.getElement("securityData").getValueAsElement();
            Element responseTickers = responseData.getElement("fieldData").getElement("OPT_CHAIN");
            for (int i = 0; i < responseTickers.numValues(); ++i) {
                Element responseTicker = responseTickers.getValueAsElement(i);
                tickers.add(responseTicker.getElement("Security Description").getValueAsString());
            }
        }
    }

    private static void handleMarketResponse(Event event, HashMap<String, Chain.Market> markets) {
        MessageIterator iter = event.messageIterator();
        int counter = 1;
        while (iter.hasNext()) {
            Message message = iter.next();
            Element responseData = message.getElement("securityData");
            String ticker = responseData.getElement("security").getValueAsString();
            if (ticker.equals("BBG00F90Q524 Index")) {
                System.out.println("Hey There");
            }
            Element prices = responseData.getElement("fieldData").getValueAsElement();
            double bidPrice;
            try {
                bidPrice = prices.getElement("PX_BID").getValueAsFloat64();
            } catch (NotFoundException exception) {
                bidPrice = 0;
            }
            double askPrice = prices.getElement("PX_ASK").getValueAsFloat64();
            markets.put(ticker, new Chain.Market(bidPrice, askPrice));
            System.out.println(counter++);
            System.out.println(message.toString());
        }
    }




    private static void printResponse(Event event) {
        MessageIterator iter = event.messageIterator();
        while (iter.hasNext()) {
            System.out.println(iter.next().toString());
        }
    }
}
