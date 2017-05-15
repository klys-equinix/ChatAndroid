package com.example.konrad.chatandroid;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import Chat_Message.ChatMessage;

/**
 * Created by Konrad on 14.05.2017.
 */

public class CommunicationService extends Service {
    private final IBinder mBinder = new LocalBinder();
    ArrayList<ChatMessage> received = new ArrayList<>();
    private Handler sendingHandler;
    Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message newMessage) {
            ChatMessage message = (ChatMessage) newMessage.obj;
            received.add(message);
            passOn(message);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                Client.getInstance().start(password,usrName,myHandler);
            }
        }).start();

    }
    private void passOn(ChatMessage message){
        Message msg = Message.obtain();
        msg.obj = message;
        msg.setTarget(sendingHandler);
        msg.sendToTarget();
    }
    public void passHandler(Handler newHandler){
        this.sendingHandler = newHandler;
    }
    public ArrayList<ChatMessage> getMessages(){
        return this.received;
    }

}
