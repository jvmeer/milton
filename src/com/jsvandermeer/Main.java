package com.jsvandermeer;


import com.bloomberglp.blpapi.*;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static int correlationIDCounter = 1;



    private enum RequestType {CHAIN, IDENTIFICATION, MARKET, FORWARD}

    public static void main(String[] args) {
        Session session = createSession();
        Service service = session.getService("//blp/refdata");
        Request request = createChainRequest(service, Utils.SPX_TICKER, Utils.stringToDate("20170103"));
        try {
            session.sendRequest(request, new CorrelationID(correlationIDCounter++));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        ArrayList<String> rawTickers = new ArrayList<>();
        receiveResponse(session, rawTickers, null, RequestType.CHAIN);

        System.out.println(rawTickers.toString());

        request = createIdentificationRequest(service, rawTickers);
        try {
            session.sendRequest(request, new CorrelationID(correlationIDCounter++));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        ArrayList<String> tickers = new ArrayList<>();
        receiveResponse(session, tickers, null, RequestType.IDENTIFICATION);

        System.out.println(tickers.toString());

        request = createMarketRequest(service, tickers, Utils.stringToDate("20170103"));

        try {
            session.sendRequest(request, new CorrelationID(correlationIDCounter++));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        HashMap<String, Chain.Market> markets = new HashMap<>();
        receiveResponse(session, null, markets, RequestType.MARKET);

        System.out.println(markets.toString());

        Chain chain = null;
        System.out.println("Hello world!");
    }


    private static void executeRequest(Session session, Service service, List<String> tickers, Map<String, Chain.Market> markets, Map<ZonedDateTime, Double> forwards, RequestType requestType, String underlier) {

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

    private static Request createIdentificationRequest(Service service, ArrayList<String> tickers) {
        Request request = service.createRequest("ReferenceDataRequest");
        tickers.forEach((ticker) -> {
            request.getElement("securities").appendValue(ticker);
        });
        request.getElement("fields").appendValue("SECURITY_DES");
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
        request.set("startDate", Utils.dateToString(asOf));
        request.set("endDate", Utils.dateToString(asOf));
        return request;
    }



    private static void receiveResponse(Session session, ArrayList<String> tickers, HashMap<String, Chain.Market> markets, RequestType requestType) {
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
                    switch (requestType) {
                        case CHAIN:
                            handleChainResponse(event, tickers);
                            break;
                        case IDENTIFICATION:
                            handleIdentificationResponse(event, tickers);
                            break;
                        case MARKET:
                            handleMarketResponse(event, markets);
                            break;
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

    private static void handleIdentificationResponse(Event event, ArrayList<String> tickers) {
        MessageIterator iter = event.messageIterator();
        while (iter.hasNext()) {
            Message message = iter.next();
            Element securityData = message.getElement("securityData");
            for (int i = 0; i < securityData.numValues(); ++i) {
                tickers.add(securityData.getValueAsElement(i).getElement("fieldData").getElement("SECURITY_DES").getValueAsString() + " Index");
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
            markets.put(ticker, new Chain.Market(bidPrice, askPrice));
        }
    }




    private static void printResponse(Event event) {
        MessageIterator iter = event.messageIterator();
        while (iter.hasNext()) {
            System.out.println(iter.next().toString());
        }
    }
}
