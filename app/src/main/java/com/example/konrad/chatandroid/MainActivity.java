package com.example.konrad.chatandroid;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import Chat_Message.ChatMessage;

public class MainActivity extends AppCompatActivity {
    CommunicationService myService;
    boolean isBound=false;
    Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message newMessage) {
            ChatMessage message = (ChatMessage) newMessage.obj;
            if(message.getType()==ChatMessage.ERROR||message.getType()==ChatMessage.REGISTER){
                Toast.makeText(getApplicationContext(), message.getMessage(),
                        Toast.LENGTH_LONG).show();
            }else if(message.getType()==ChatMessage.LOGIN){
                MainActivity.this.startActivity(new Intent(MainActivity.this,MessagesBox.class));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button loginButton = (Button) findViewById(R.id.loginButton);
        final TextView loginField = (EditText) findViewById(R.id.login);
        final TextView passwordField = (EditText) findViewById(R.id.password);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(isBound){
                    try {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        digest.update(passwordField.getText().toString().getBytes(StandardCharsets.UTF_8));
                        byte[] hashedPasswd = digest.digest();
                        String usrName = loginField.getText().toString();
                        myService.initializeCommunication(hashedPasswd,usrName);
                        myService.passHandler(myHandler);
                    }catch (NoSuchAlgorithmException ex){

                    }
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, CommunicationService.class);
        startService(intent);
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
    private ServiceConnection myConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            CommunicationService.LocalBinder binder = (CommunicationService.LocalBinder) service;
            myService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

}
