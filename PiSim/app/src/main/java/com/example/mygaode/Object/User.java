package com.example.mygaode.Object;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mygaode.Thread.register_login_Thread;

import java.math.BigInteger;
import java.security.SecureRandom;

import encryption.RSA;
import parameter.parameter;

import static parameter.parameter.User_KeyMap;


public class User {
    private String username;//
    private String password;//
    private String driverPrivateKey;//
    private String driverPubKey;//
    private String idNumber;
    private String idCar;
    private String si;
    private Context context;
    private MyHandler handler;
    private SharedPreferences.Editor editor;

    public User(final Context context, MyHandler handler, String username, String passwod, String idCar, String idNumber) {
        this.username = username;
        this.password = passwod;
        this.idNumber = idNumber;
        this.idCar = idCar;
        this.context = context;
        this.handler = handler;
        //从文件读取RSU公私钥和si
        SharedPreferences sharedPreferences = context.getSharedPreferences("KeyMap", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String privateKey = sharedPreferences.getString("privateKey", "");
        String publicKey = sharedPreferences.getString("publicKey", "");
//        判断是否存在公私钥
        if (!privateKey.equals("") || !publicKey.equals("")) {
            //有公私钥 通知注册的过程会覆盖之前的公私钥和秘密
            alert();
        }else {
            SecureRandom secureRandom = new SecureRandom();
            genUserKeyPair();
            si = parameter.si = new BigInteger(256, secureRandom).toString();
            editor.putString("privateKey", driverPrivateKey);
            editor.putString("publicKey", driverPubKey);
            editor.putString("si", si);
            editor.apply();
            register();
        }
        // TODO Auto-generated constructor stub

    }

    public User(Context context, MyHandler handler, String username, String password) {
        this.username = username;
        this.password = password;
        this.context = context;
        this.handler = handler;
        //从文件读取公私钥及si
        getKeyFromFile();
        // TODO Auto-generated constructor stub
    }

    public void register() {
        register_login_Thread register_login_thread = new register_login_Thread(this.handler, username, password, idNumber, idCar, si, driverPrivateKey, driverPubKey);
        register_login_thread.start();
    }

    public void login() {
        register_login_Thread register_login_thread = new register_login_Thread(this.handler, username, password, driverPrivateKey, driverPubKey);
        register_login_thread.start();
    }

    private void genUserKeyPair() {
        try {
            User_KeyMap = RSA.generateRsaKeyPair();
            parameter.driver_rsa_pub = driverPubKey = User_KeyMap.get("publicKey");
            parameter.driver_rsa_pri = driverPrivateKey = User_KeyMap.get("privateKey");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getKeyFromFile() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("KeyMap", Context.MODE_PRIVATE);
        this.driverPrivateKey = parameter.driver_rsa_pri = sharedPreferences.getString("privateKey", "");
        this.driverPubKey = parameter.driver_rsa_pub = sharedPreferences.getString("publicKey", "");
        this.si = parameter.si = sharedPreferences.getString("si", "");
        if (this.driverPrivateKey.equals("")) {
            final EditText inputServer = new EditText(this.context);
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setTitle("设备上没有找到您的私钥,请您手动输入").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                    .setNegativeButton("取消", null);
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println(inputServer.getText().toString());
                    parameter.driver_rsa_pri = inputServer.getText().toString();
                }
            });
            builder.show();
        }
        if (this.driverPubKey.equals("")) {
            final EditText inputServer = new EditText(this.context);
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setTitle("设备上没有找到您的公钥,请您手动输入").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                    .setNegativeButton("取消", null);
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println(inputServer.getText().toString());
                    parameter.driver_rsa_pub = inputServer.getText().toString();
                }
            });
            builder.show();
        }
        if (this.si.equals("")) {
            final EditText inputServer = new EditText(this.context);
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setTitle("设备上没有找到您的秘密si,请您手动输入").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                    .setNegativeButton("取消", null);
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println(inputServer.getText().toString());
                    parameter.si = inputServer.getText().toString();
                }
            });
            builder.show();
        }
    }

    //弹框提示覆盖风险
    private void alert() {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("秘密覆盖")
                .setMessage("您的设备上已存在认证信息，是否覆盖并重新注册")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //生成公私钥及si并存储到文件中
                        SecureRandom secureRandom = new SecureRandom();
                        genUserKeyPair();
                        si = parameter.si = new BigInteger(256, secureRandom).toString();
                        editor.putString("privateKey", driverPrivateKey);
                        editor.putString("publicKey", driverPubKey);
                        editor.putString("si", si);
                        editor.apply();
                        register();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "您可以多次点击主界面版本号\n来查看您设备当前保存的SI", Toast.LENGTH_LONG).show();
                    }
                }).create();
        alertDialog.show();
    }
}