package com.example.mygaode;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygaode.Object.MyHandler;
import com.example.mygaode.Object.User;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
        setContentView(R.layout.activity_register);
    }


    @SuppressLint("ShowToast")
    public void get_register(View view) {
        final TextView username = findViewById(R.id.re_username);
        final TextView password1 = findViewById(R.id.re_password1);
        final TextView password2 = findViewById(R.id.re_password2);
        final TextView idNumber = findViewById(R.id.IdNumber);
        final TextView idCar = findViewById(R.id.IdCar);
        if (idNumber.getText().toString().equals("") ||
                idNumber.getText().toString().length()<18||
                idCar.getText().toString().equals("") ||
                idCar.getText().toString().length()<7||
                username.getText().toString().equals("") ||
                password1.getText().toString().equals("")
        ) {
            Toast.makeText(this, "您输入信息有误，请重新输入", Toast.LENGTH_SHORT).show();
        } else {
            if (!password1.getText().toString().equals(password2.getText().toString())) {
                Toast.makeText(this, "您两次输入密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
            } else {
                MyHandler handler = new MyHandler(this);
                new User(this, handler,
                        username.getText().toString(),
                        password1.getText().toString(),
                        idCar.getText().toString(),
                        idNumber.getText().toString());
            }
        }
    }
}
