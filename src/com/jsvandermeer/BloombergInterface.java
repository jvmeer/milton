package com.jsvandermeer;

import com.bloomberglp.blpapi.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jacob on 4/16/2017.
 */
public class BloombergInterface {
    private final String REF_DATA_SERVICE = "//blp/refdata";


    private static BloombergInterface bloombergInterface;
    private static int correlationId = 1;
    private Session session;
    private Service service;

    private BloombergInterface() {
        session = createSession(REF_DATA_SERVICE);
        service = session.getService(REF_DATA_SERVICE);
    }

    static BloombergInterface getInstance() {
        if (bloombergInterface == null) {
            bloombergInterface = new BloombergInterface();
        }
        return bloombergInterface;
    }

    void close() {
        try {
            session.stop();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    private static Session createSession(String serviceIdentifier) {
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
            session.openService(serviceIdentifier);
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
        return session;
    }

    Set<DataInterface.FutureLine> retrieveFutureLines(LocalDate startDate, LocalDate endDate,
                                                      Utils.Underlier underlier) {
        Set<DataInterface.FutureLine> futureLines = new HashSet<>();
        Request request;
        for (LocalDate dateCursor = startDate; dateCursor.isBefore(endDate); dateCursor = dateCursor.plusDays(1)) {
            request = createFutureChainRequest(underlier, dateCursor);
            Set<String> bloombergTickers = processChainResponse(request);
            request = createFutureQuoteRequest(bloombergTickers, dateCursor);
            Map<String, Chain.Market> = processQuoteResponse(request);
            try {
                session.sendRequest(request, new CorrelationID(correlationId++));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            continueToLoop = true;
            while (continueToLoop) {
                try {
                    Event event = session.nextEvent();
                }
            }


        }
        return new HashSet<>();
    }

    private Request createFutureChainRequest(Utils.Underlier underlier, LocalDate asOf) {
        Request request = service.createRequest("ReferenceDataRequest");
        request.getElement("securities").appendValue(underlier.bloombergFutureTicker);
        request.getElement("fields").appendValue("FUT_CHAIN");
        Element asOfOverride = request.getElement("overrides").appendElement();
        asOfOverride.setElement("fieldId","CHAIN_DATE");
        asOfOverride.setElement("value", dateToBloombergString(asOf));
        Element identifierOverride = request.getElement("overrides").appendElement();
        identifierOverride.setElement("fieldId", "DISPLAY_ID_BB_GLOBAL_OVERRIDE");
        identifierOverride.setElement("value", "False");

        return request;
    }

    private Request createFutureQuoteRequest(Set<String> bloombergTickers, LocalDate asOf) {
        Request request = service.createRequest("HistoricalDataRequest");
        for (String bloombergTicker : bloombergTickers) {
            request.getElement("securities").appendValue(bloombergTicker);
        }
        Element fields = request.getElement("fields");
        fields.appendValue("PX_BID");
        fields.appendValue("PX_ASK");
        fields.appendValue("BID_SIZE");
        fields.appendValue("ASK_SIZE");
        request.set("startDate", dateToBloombergString(asOf));
        request.set("endDate", dateToBloombergString(asOf));

        return request;
    }


    private Set<String> processChainResponse(Request request) {
        Set<String> bloombergTickers = new HashSet<>();
        try {
            session.sendRequest(request, new CorrelationID(correlationId++));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        boolean continueToLoop = true;
        while (continueToLoop) {
            try {
                Event event = session.nextEvent();
                switch (event.eventType().intValue()) {
                    case Event.EventType.Constants.RESPONSE:
                        continueToLoop = false; //fall through
                    case Event.EventType.Constants.PARTIAL_RESPONSE:
                        MessageIterator iter = event.messageIterator();
                        while (iter.hasNext()) {
                            Message message = iter.next();
                            Element responseData = message.getElement("securityData").getValueAsElement();
                            Element responseTickers = responseData.getElement("fieldData").getElement("FUT_CHAIN");
                            for (int i = 0; i < responseTickers.numValues(); ++i) {
                                Element responseTicker = responseTickers.getValueAsElement(i);
                                bloombergTickers.add(responseTicker.getElement("Security Description").
                                        getValueAsString());
                            }
                        }
                    default:
                        break;
                }
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
        return bloombergTickers;
    }

    private Map<String, Chain.Market> processQuoteResponse(Request request) {
        Map<String, Chain.Market> markets = new HashMap<>();
        try {
            session.sendRequest(request, new CorrelationID(correlationId++));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        boolean continueToLoop = true;
        while (continueToLoop) {
            try {
                Event event = session.nextEvent();
                switch (event.eventType().intValue()) {
                    case Event.EventType.Constants.RESPONSE:
                        continueToLoop = false; //fall through
                    case Event.EventType.Constants.PARTIAL_RESPONSE:
                        MessageIterator iter = event.messageIterator();
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
                        }
                    default:
                        break;
                }
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }

    private static String dateToBloombergString(LocalDate date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuuMMdd");
        return date.format(dateTimeFormatter);
    }
}
