package com.example.konrad.chatandroid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Konrad on 04.05.2017.
 */

class Client {
    private static Client ourInstance;
    private String server = "localhost";
    //private String server = "185.5.98.242";
    private String usrName;
    private int port=1500;
    private Socket socket;
    private ObjectOutputStream obOut;
    private ObjectInputStream obIn;
    private Handler myHandler;
    public static synchronized Client getInstance() {
        if(ourInstance==null){
            ourInstance = new Client();
        }
        return ourInstance;
    }

    private Client(){}
    public boolean start(char [] password,String usrName,Handler handler){
        this.myHandler = handler;
        this.usrName=usrName;
        try{
            socket = new Socket(server,port);
        }catch(IOException ex){
            sendToHandler("Connection cannot be established"+ex);
            return false;
        }
        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        sendToHandler(msg);

        try{
            this.obOut = new ObjectOutputStream(socket.getOutputStream());
            this.obOut.flush();
            this.obIn = new ObjectInputStream(socket.getInputStream());

        }catch(IOException ex){
            sendToHandler("Cannot establish IO streams"+ex);
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
    void sendMessage(ChatMessage chatMessage){
        try{
            obOut.writeObject(chatMessage);
        }catch(IOException ex){
            System.out.println("Cannot send chatMessage");
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

                    System.out.println(msg);
                }catch(IOException ex){
                    System.out.println("Cannot read message"+ex);
                    break;
                }catch(ClassNotFoundException ex2){

                }
            }
        }
    }
    private void sendToHandler(String text){
        String message = text;
        Message msg = Message.obtain();
        msg.obj = message;
        msg.setTarget(myHandler);
        msg.sendToTarget();
    }
}
