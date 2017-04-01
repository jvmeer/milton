package com.jsvandermeer;


import com.bloomberglp.blpapi.*;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static int correlationIDCounter = 1;



    private enum ConversationType {CHAIN, IDENTIFICATION, MARKET, FORWARD}

    public static void main(String[] args) {
        Session session = createSession();
        Service service = session.getService("//blp/refdata");
        ZonedDateTime asOf = Utils.stringToDate("20170103");



        String[] underliers = {Utils.SPX_TICKER, Utils.VIX_TICKER};

        Map<String, Strip> chains = new HashMap<>();



        for (String underlier : underliers) {

            List<String> rawTickers = new ArrayList<>();
            executeConversation(session, service, asOf, ConversationType.CHAIN, underlier, rawTickers, null,
                    null, null);

            System.out.println(rawTickers.toString());

            List<String> tickers = new ArrayList<>();
            executeConversation(session, service, asOf, ConversationType.IDENTIFICATION, underlier, rawTickers,
                    tickers,null, null);

            System.out.println(tickers.toString());

            Map<String, Strip.Market> markets = new HashMap<>();
            executeConversation(session, service, asOf, ConversationType.MARKET, underlier, null,
                    tickers, markets, null);

            System.out.println(markets.toString());

            Map<ZonedDateTime, Double> forwards = new HashMap<>();
            executeConversation(session, service, asOf, ConversationType.FORWARD, underlier, null,
                    null, null, forwards);

            chains.put(underlier, new Strip(underlier, asOf, forwards, markets));
        }




    }


    private static void executeConversation(Session session, Service service, ZonedDateTime asOf,
                                       ConversationType conversationType, String underlier, List<String> rawTickers,
                                       List<String> tickers, Map<String, Strip.Market> markets,
                                       Map<ZonedDateTime, Double> forwards) {
        Request request = null;
        switch (conversationType) {
            case CHAIN:
                request = createChainRequest(service, underlier, asOf);
                break;
            case IDENTIFICATION:
                request = createIdentificationRequest(service, rawTickers);
                break;
            case MARKET:
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
            case MARKET:
                receiveResponse(session, null, markets, null, conversationType);
                break;
            case FORWARD:
                receiveResponse(session, null, null, forwards, conversationType);
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



    private static void receiveResponse(Session session, List<String> tickers, Map<String, Strip.Market> markets,
                                        Map<ZonedDateTime, Double> forwards, ConversationType conversationType) {
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

    private static void handleChainResponse(Event event, List<String> tickers) {
        MessageIterator iter = event.messageIterator();
        while (iter.hasNext()) {
            Message message = iter.next();
            System.out.println(message.toString());
//            Element responseData = message.getElement("securityData").getValueAsElement();
//            Element responseTickers = responseData.getElement("fieldData").getElement("OPT_CHAIN");
//            for (int i = 0; i < responseTickers.numValues(); ++i) {
//                Element responseTicker = responseTickers.getValueAsElement(i);
//                tickers.add(responseTicker.getElement("Security Description").getValueAsString());
//            }
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

    private static void handleMarketResponse(Event event, Map<String, Strip.Market> markets) {
        MessageIterator iter = event.messageIterator();
        while (iter.hasNext()) {
            Message message = iter.next();
            System.out.println(message.toString());
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
//            markets.put(ticker, new Strip.Market(bidPrice, askPrice));
        }
    }

    private static void handleForwardResponse(Event event, Map<ZonedDateTime, Double> forwards) {
        MessageIterator iter = event.messageIterator();
        while (iter.hasNext()) {
            Message message = iter.next();
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
