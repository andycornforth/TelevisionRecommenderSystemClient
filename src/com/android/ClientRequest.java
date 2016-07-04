/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.android;

/**
 *
 * @author Andy
 */
public class ClientRequest {

    private final static String SOAP_START = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\" >"
            + "<env:Body><Request><RequestType>";
    private final static String SOAP_MIDDLE = "</RequestType>";
    private final static String SOAP_END = "</Request></env:Body></env:Envelope>";
    String username, requestType, channel, date, show, password, age, gender, day, start;

    public ClientRequest(String user, String request) {
        username = user;
        requestType = request;
    }

    public ClientRequest(String user, String request, String channelName, String dateWanted) {
        username = user;
        requestType = request;
        channel = channelName;
        date = dateWanted;
    }

    public ClientRequest(String user, String request, String showName) {
        username = user;
        requestType = request;
        show = showName;
    }

    public ClientRequest(String user, String request, String pass, String a, String g) {
        username = user;
        requestType = request;
        password = pass;
        age = a;
        gender = g;
    }
    
    public ClientRequest(String user, String request, String showName, String channelName, String date, String startTime) {
        username = user;
        requestType = request;
        show = showName;
        channel = channelName;
        day = date;
        start = startTime;
    }

    public String createRecommendationRequest() {
        return SOAP_START + requestType + SOAP_MIDDLE + "<Username>"
                + username + "</Username>" + SOAP_END;
    }

    public String createListingsRequest() {
        return SOAP_START + requestType + SOAP_MIDDLE + "<Username>"
                + username + "</Username><Channel>" + channel
                + "</Channel><Date>" + date + "</Date>" + SOAP_END;
    }

    public String createShowRequest() {
        return SOAP_START + requestType + SOAP_MIDDLE + "<Username>"
                + username + "</Username><Show>" + show
                + "</Show>" + SOAP_END;
    }

    public String createSignUpRequest() {
        return SOAP_START + requestType + SOAP_MIDDLE + "<Username>"
                + username + "</Username><Password>" + password + "</Password>"
                + "<Age>" + age + "</Age><Gender>" + gender + "</Gender>" + SOAP_END;
    }
    
    public String createLogInRequest(){
        return SOAP_START + requestType + SOAP_MIDDLE + "<Username>"
                + username + "</Username><Password>" + show
                + "</Password>" + SOAP_END;
    }
    
    public String createRateRequest(){
        return SOAP_START + requestType + SOAP_MIDDLE + "<Username>"
                + username + "</Username><Show>" + channel
                + "</Show><Rating>" + date + "</Rating>" + SOAP_END;
    }
    
    public String createUpdateProfileRequest() {
        return SOAP_START + requestType + SOAP_MIDDLE + "<Username>"
                + username + "</Username><Password>" + password + "</Password>"
                + "<Age>" + age + "</Age><Gender>" + gender + "</Gender>" + SOAP_END;
    }
    
    public String createEpisodeRequest(){
          return SOAP_START + requestType + SOAP_MIDDLE + "<Username>"
                + username + "</Username><Channel>" + channel + "</Channel><Show>" + show + "</Show"
                + "<Date>" + day + "</Date><Start>" + start + "</Start>" + SOAP_END;
    }
}
