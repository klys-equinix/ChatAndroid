package com.example.konrad.chatandroid;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class MessagesBox extends AppCompatActivity {

    TextView deliveredField;
    Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message newMessage) {
            String message = (String) newMessage.obj;
            write(message);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        final String userName = intent.getStringExtra("userName");
        final String password = intent.getStringExtra("password");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_box);
        deliveredField = (EditText) findViewById(R.id.delivered);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Client.getInstance().start(password.toCharArray(),userName,myHandler);
            }
        }).start();

    }
    public void write(String text){
        deliveredField.setText(deliveredField.getText()+"\n");//this does strange stuff
    }
}
