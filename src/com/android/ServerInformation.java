/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.android;

/**
 *
 * @author Andy
 */
public class ServerInformation {
    
    private static String SERVER_IP;
    private static int PORT;
    
    public ServerInformation(){
        SERVER_IP = "10.167.118.243";
        PORT = 21111;
    }

    public String getSERVER_IP() {
        return SERVER_IP;
    }

    public int getPORT() {
        return PORT;
    }
    
    
}
