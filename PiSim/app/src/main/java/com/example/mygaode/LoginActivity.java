package com.example.mygaode;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygaode.Object.MyHandler;
import com.example.mygaode.Object.User;

public class LoginActivity extends AppCompatActivity {
    MyHandler handler = new MyHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
        setContentView(R.layout.activity_login);
        Button btn_login = findViewById(R.id.login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        final TextView username = findViewById(R.id.log_username);
        final TextView password = findViewById(R.id.log_password);


        User user = new User(this, handler, username.getText().toString(), password.getText().toString());
        try {
            user.login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void toRegister(View view){
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
