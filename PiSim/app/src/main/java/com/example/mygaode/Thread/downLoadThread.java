package com.example.mygaode.Thread;

import android.os.Message;
import android.util.Log;


import com.example.mygaode.Object.MyHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.example.mygaode.utils.FileUtils;

public class downLoadThread extends Thread{
    private String path;
    private String filePath;
    private String FileName;
    private MyHandler handler;
    public downLoadThread(MyHandler handler, String path, String filePath, String FileName){
        this.path = path;
        this.filePath = filePath;
        this.FileName = FileName;
        this.handler = handler;
    }
    public void run() {
        try {
            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(50000);
            con.setConnectTimeout(50000);
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestMethod("GET");
            System.out.println(con.getResponseCode());
            if (con.getResponseCode() == 200) {
                InputStream inputStream = con.getInputStream();
                FileOutputStream fileOutputStream = null;
                if (inputStream != null) {
                    FileUtils fileUtils = new FileUtils(filePath);
                    fileOutputStream = new FileOutputStream(fileUtils.createFile(FileName));
                    byte[] buf = new byte[1024];
                    int ch;
                    while ((ch = inputStream.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, ch);
                    }
                }
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Message message = handler.obtainMessage(24);
            handler.sendMessage(message);
            Log.e("DownLoadFile",FileName+"下载失败");
        }

    }
}
