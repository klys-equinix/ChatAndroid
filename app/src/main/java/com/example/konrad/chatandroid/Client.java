package com.example.konrad.chatandroid;

/**
 * Created by Konrad on 14.05.2017.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import Chat_Message.ChatMessage;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

class Client extends Thread{
    private String server = "10.0.2.2";
    //private String server = "localhost";
    public String usrName;
    private int port=1500;
    private byte[] password;
    private Socket socket;
    private ObjectOutputStream obOut;
    private ObjectInputStream obIn;

    private Handler myHandler;

    public Client(byte[] password, String usrName,Handler myHandler){
        this.myHandler = myHandler;
        this.usrName=usrName;
        this.password = password;
    }
    public void run(){
        try{
            socket = new Socket(server,port);
        }catch(IOException ex){


            sendToHandler(new ChatMessage(ChatMessage.ERROR,"Connection cannot be established"+ex,this.usrName));
            return ;
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
            return;
        }
        new Client.ListenFromServer().start();
        try{
            ChatMessage register = new ChatMessage(ChatMessage.REGISTER,password,this.usrName);
            obOut.writeObject(register);
        }catch (IOException ex){
            System.out.println("Error logging in"+ex);
            disconnect();
            return ;
        }
        return;
    }
    private class broadcastTask extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                String messageText = params[0];
                String receiver = params[1];
                ChatMessage message=null;
                if (messageText.equalsIgnoreCase("LOGOUT")) {
                    message = new ChatMessage(ChatMessage.LOGOUT, "",usrName);
                } else if (messageText.equalsIgnoreCase("WHOISIN")) {
                    message = new ChatMessage(ChatMessage.WHOISIN, "",usrName);
                } else if(messageText!="") {
                    message = new ChatMessage(ChatMessage.MESSAGE,messageText,usrName,receiver);
                }
                obOut.writeObject(message);
            } catch (IOException ex) {
                System.out.println("Cannot send message");
            }
            return null;
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

    public Handler getClientHandler() {
        return ClientHandler;
    }

    private void sendToHandler(ChatMessage newMsg){
        Message msg = Message.obtain();
        msg.obj = newMsg;
        msg.setTarget(myHandler);
        msg.sendToTarget();
    }
    private Handler ClientHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message newMessage) {
            String[] out = (String[]) newMessage.obj;
            new broadcastTask().execute(out[0],out[1]);
        }
    };

}

