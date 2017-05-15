package com.example.konrad.chatandroid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.app.IntentService;
import android.content.Intent;

import Chat_Message.ChatMessage;

/**
 * Created by Konrad on 08.05.2017.
 * System.out are desktop leftovers, will be purged soon
 */

public class CommunicationIntentService extends IntentService {
    private String server = "10.0.2.2";
    private String usrName;
    private int port=1500;
    private Socket socket=null;
    private ObjectOutputStream obOut;
    private ObjectInputStream obIn;
    public static final String NOTIFICATION = "com.example.konrad.chatandroid.receiver";


    public CommunicationIntentService() {
        super("Comms service");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        if(socket==null) {
            usrName = intent.getStringExtra("userName");
            byte[] hashedPassword =null;
            try{
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.update(intent.getStringExtra("password").getBytes(StandardCharsets.UTF_8));
                hashedPassword = digest.digest();
            }catch (NoSuchAlgorithmException ex){}

            try {
                socket = new Socket(server, port);
            } catch (IOException ex) {
                sendToGUI("Connection cannot be established" + ex);
                return;
            }
            String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
            sendToGUI(msg);

            try {
                this.obOut = new ObjectOutputStream(socket.getOutputStream());
                this.obOut.flush();
                this.obIn = new ObjectInputStream(socket.getInputStream());

            } catch (IOException ex) {
                sendToGUI("Cannot establish IO streams" + ex);
                return;
            }
            new CommunicationIntentService.ListenFromServer().start();
            try {
                ChatMessage register = new ChatMessage(ChatMessage.REGISTER, hashedPassword, this.usrName);
                obOut.writeObject(register);
            } catch (IOException ex) {
                sendToGUI("Error logging in" + ex);
                disconnect();
                return;
            }
        }else {
            sendMessage(intent.getStringExtra("Message"), intent.getStringExtra("Receiver"));
        }
    }
    void sendMessage(String message,String receiver){
        ChatMessage chatMessage=null;
        if (message.equalsIgnoreCase("LOGOUT")) {
            chatMessage=new ChatMessage(ChatMessage.LOGOUT, "",usrName);
        } else if (message.equalsIgnoreCase("WHOISIN")) {
            chatMessage=new ChatMessage(ChatMessage.WHOISIN, "",usrName);
        } else if(message!="") {
            chatMessage=new ChatMessage(ChatMessage.MESSAGE,message,usrName,receiver);
        }
        try{
            obOut.writeObject(chatMessage);
        }catch(IOException ex){
            sendToGUI("Cannot send chatMessage");
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
        stopSelf();
    }
    class ListenFromServer extends Thread{
        public void run(){
            while(true){
                try{
                    ChatMessage msg = (ChatMessage) obIn.readObject();
                    sendToGUI(msg.getMessage());
                    System.out.println(msg);
                }catch(IOException ex){
                    System.out.println("Cannot read message"+ex);
                    break;
                }catch(ClassNotFoundException ex2){

                }
            }
        }
    }
    private void sendToGUI(String outputPath) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("Communication", outputPath);
        sendBroadcast(intent);
    }

}
