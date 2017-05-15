package com.example.konrad.chatandroid;

/**
 * Created by Konrad on 14.05.2017.
 */

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import Chat_Message.ChatMessage;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

class Client {
    private static final Client ourInstance = new Client();
    private String server = "10.0.2.2";
    //private String server = "185.5.98.242";
    private String usrName;
    private int port=1500;
    private Socket socket;
    private ObjectOutputStream obOut;
    private ObjectInputStream obIn;

    private Handler myHandler;
    static Client getInstance() {
        return ourInstance;
    }

    private Client(){}
    public boolean start(byte[] password, String usrName,Handler myHandler){
        this.myHandler = myHandler;
        this.usrName=usrName;
        try{
            socket = new Socket(server,port);
        }catch(IOException ex){


            sendToHandler(new ChatMessage(ChatMessage.ERROR,"Connection cannot be established"+ex,this.usrName));
            return false;
        }
        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        sendToHandler(new ChatMessage(ChatMessage.REGISTER,msg,this.usrName));
        System.out.print(msg);
        try{
            this.obOut = new ObjectOutputStream(socket.getOutputStream());
            this.obOut.flush();
            this.obIn = new ObjectInputStream(socket.getInputStream());

        }catch(IOException ex){
            System.out.println("Cannot establish IO streams"+ex);
            return false;
        }
        new Client.ListenFromServer().start();
        try{
            ChatMessage register = new ChatMessage(ChatMessage.REGISTER,password,this.usrName);
            obOut.writeObject(register);
        }catch (IOException ex){
            System.out.println("Error logging in"+ex);
            disconnect();
            return false;
        }
        return true;
    }
    void sendMessage(ChatMessage message){
        try{
            obOut.writeObject(message);
        }catch(IOException ex){
            System.out.println("Cannot send message");
        }
    }
    public void disconnect(){
        try{
            if(obIn!=null) obIn.close();
        }catch (IOException ex){

        }
        try{
            if(obOut!=null) obOut.close();
        }catch (IOException ex){

        }
        try{
            if(socket!= null) socket.close();
        }catch (IOException ex){

        }
    }
    class ListenFromServer extends Thread{
        public void run(){
            while(true){
                try{
                    ChatMessage msg = (ChatMessage) obIn.readObject();
                    sendToHandler(msg);
                    System.out.println(msg);
                }catch(IOException ex){
                    System.out.println("Cannot read message"+ex);
                    break;
                }catch(ClassNotFoundException ex2){

                }
            }
        }
    }
    private void sendToHandler(ChatMessage newMsg){
        Message msg = Message.obtain();
        msg.obj = newMsg;
        msg.setTarget(myHandler);
        msg.sendToTarget();
    }

}

