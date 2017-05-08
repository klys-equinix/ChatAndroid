package com.example.konrad.chatandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button loginButton = (Button) findViewById(R.id.loginButton);
        final TextView loginField = (EditText) findViewById(R.id.login);
        final EditText passwordField = (EditText) findViewById(R.id.password);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String usrName = loginField.getText().toString();
                String passWd=passwordField.getText().toString();
                Intent goToMessages = new Intent(MainActivity.this,MessagesBox.class);
                goToMessages.putExtra("userName",usrName);
                goToMessages.putExtra("password",passWd);
                MainActivity.this.startActivity(goToMessages);
            }
        });
    }

}
