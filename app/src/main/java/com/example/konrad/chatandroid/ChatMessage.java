/**
 * Created by Konrad on 18.04.2017.
 */
package com.example.konrad.chatandroid;

import java.io.*;
public class ChatMessage implements Serializable{
    protected static final long serialVersionUID = 1112122200L;
    static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2,REGISTER = 3,LOGIN =4 ,ERROR = 5;
    private int type;
    private String message;
    private char[] password;
    private String receiver=null;
    private String sender;
    private String time;
    public ChatMessage(int type, String message, String sender){
        this.type = type;
        this.message = message;
        if(sender!="") {
            this.sender=sender;
        }
    }
    public ChatMessage(int type, char [] password, String sender){
        this.type = type;
        this.password = password;
        if(sender!="") {
            this.sender=sender;
        }
    }
    public ChatMessage(int type, String message, String sender, String receiver){
        this(type,message,sender);
        this.receiver=receiver;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;

    }

    public char[] getPassword() {
        return password;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }
}
