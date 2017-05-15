package com.example.konrad.chatandroid;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;

import Chat_Message.ChatMessage;

/**
 * Created by Konrad on 14.05.2017.
 */

public class CommunicationService extends Service {
    private final IBinder mBinder = new LocalBinder();
    ArrayList<ChatMessage> received = new ArrayList<>();
    Client currClient;
    private Handler ActivitySendHandler;
    Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message newMessage) {
            ChatMessage message = (ChatMessage) newMessage.obj;
            received.add(message);
            passToActivity(message);
        }
    };
    public class LocalBinder extends Binder {
        CommunicationService getService() {
            return CommunicationService.this;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void initializeCommunication(byte[] password,String usrName){
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                Client.getInstance().start(password,usrName,myHandler);
            }
        }).start();*/
        currClient = new Client(password,usrName,myHandler);
        currClient.start();
    }

    private void passToActivity(ChatMessage message){
        Message msg = Message.obtain();
        msg.obj = message;
        msg.setTarget(ActivitySendHandler);
        msg.sendToTarget();
    }
    public void passToClient(ChatMessage message){
        Message msg = Message.obtain();
        msg.obj = message;
        msg.setTarget(currClient.getClientHandler());
        msg.sendToTarget();
    }
    public void passHandler(Handler newHandler){
        this.ActivitySendHandler = newHandler;
    }
    public ArrayList<ChatMessage> getMessages(){
        return this.received;
    }

}
