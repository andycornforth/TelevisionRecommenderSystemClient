/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.android;

/**
 *
 * @author Andy
 */
public class ServerResponse {

    String[] response;

    public ServerResponse(String responseString) {
        response = responseString.split("<");
    }

    public String getResponseBody() {
        if (response[5].contains("recommendation")) {
            return getRecommendationBody();
        } else if (response[5].contains("listing")) {
            return getListingBody();
        } else if (response[5].contains("show")) {
            return getShowBody();
        } else if (response[5].contains("profile")) {
            return getProfileBody();
        } else if (response[5].contains("rate")){
            return getRateResponse();
        } else if (response[5].contains("episode")){
            return getEpisodeResponse();
        }
        return null;
    }

    private String getRecommendationBody() {
        String result = "";
        if (response[8].contains("Message")) {
            result = response[8].replace("Message>", "");
        } else {
            result += response[8].replace(">", ": ") + "\n";
            result += response[10].replace(">", ": ") + "\n\n";
            result += response[12].replace("message>", ": ");
        }
        return result;
    }

    private String getListingBody() {
        String result = "";
        for (int i = 9; i < response.length-7; i = i+8) {
            result += response[i].replace("Show>", "") + "/";
            result += response[i+2].replace("Start>", "") + "/";
            result += response[i+4].replace("End>", "") + "/";
        }
        return result;
    }

    private String getShowBody() {
        return response[6] + "\n" + response[8] + "\n" + response[10];
    }

    public String getSignUpValue() {
        if (response[8].contains("true")) {
            return "You have successfully signed up, please log in.";
        }
        return "Unable to create an account, please try again.";
    }

    public String getLogInValue() {
        if (response[8].contains("true")) {
            return "You have successfully Logged In.";
        }
        return "Unable to log in, please try again";
    }

    public String getRateResponse() {
        if (response[7].contains("true")) {
            return "You have successfully RATED the show.";
        }
        return "Unable to rate show.";
    }

    public String getProfileBody() {
        return response[8] + "\n" + response[10] + "\n" + response[12];
    }

    public String getProfileResponse(){
        if(response[5].contains("update")){
            return getUpdateResponse();
        }else{
            return getAllRatingsResponse();
        }
    }
    
    public String getUpdateResponse() {
        if (response[8].contains("true")) {
            return "You have successfully updated your profile.";
        }
        return "Unable to update profile.";
    }
    
    public String getAllRatingsResponse(){
        String result = "";
        for (int i = 0; i < response.length; i++) {
            if(response[i].contains("title>")){
                response[i] = response[i].replace("title>", "");
                result += response[i] + "/";
            }else if(response[i].contains("rating>")){
                response[i] = response[i].replace("rating>", "");
                result += response[i] + "/";
            }
        }
        return result;
    }
    
    public String getEpisodeResponse(){
        return "" + response[11];
    }
}
