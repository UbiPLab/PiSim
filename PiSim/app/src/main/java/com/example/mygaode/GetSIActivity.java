package com.example.mygaode;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GetSIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ip);
    }
    @SuppressLint({"ShowToast", "SetTextI18n"})
    public void getSI(View view){
        final TextView SI = findViewById(R.id.id_si);
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("KeyMap", Context.MODE_PRIVATE);
        String si = sharedPreferences.getString("si", "");
        if (si.equals("")){
            SI.setText("设备无秘密SI");
            Toast.makeText(this,"获取SI失败",Toast.LENGTH_SHORT).show();

        }else {
            SI.setText("您的秘密SI:\n"+si+"\n请妥善保管哦");
            Toast.makeText(this,"获取SI成功",Toast.LENGTH_SHORT).show();

        }
    }
}
