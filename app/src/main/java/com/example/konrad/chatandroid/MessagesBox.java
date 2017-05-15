package com.example.konrad.chatandroid;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Connection;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import Chat_Message.ChatMessage;


public class MessagesBox extends AppCompatActivity {
    TextView deliveredField;
    TextView receiverField;
    TextView messageField;
    CommunicationService myService;
    boolean isBound=false;

    Handler myHandler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message newMessage) {
        ChatMessage message = (ChatMessage) newMessage.obj;

    }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_box);
        deliveredField = (EditText) findViewById(R.id.delivered);
        receiverField = (EditText) findViewById(R.id.receiver);
        messageField = (EditText) findViewById(R.id.message);
        Button sendButton = (Button) findViewById(R.id.sendButton);


        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, CommunicationService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

    }
    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(myConnection);
            isBound = false;
        }
    }


    public void write(String text){
        deliveredField.setText(text+"\n");
    }
    private ServiceConnection myConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            CommunicationService.LocalBinder binder = (CommunicationService.LocalBinder) service;
            myService = binder.getService();
            myService.passHandler(myHandler);
            isBound = true;
            getAllMessages();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };
    public void getAllMessages(){
        ArrayList<ChatMessage> newList = myService.getMessages();
        deliveredField.setText("");
        for(ChatMessage msg : newList){
            write(msg.getMessage());
        }
    }


}
