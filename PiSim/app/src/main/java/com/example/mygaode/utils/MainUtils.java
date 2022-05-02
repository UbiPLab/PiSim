package com.example.mygaode.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.mygaode.Object.MyHandler;
import com.example.mygaode.Thread.downLoadThread;

import org.ujmp.core.collections.list.FastArrayList;

import java.io.File;
import java.util.List;
import java.util.Objects;

import parameter.IP;

import static parameter.parameter.city_name;

public class MainUtils {
    public static void getOffLineRouteData(Context context) {
        try {
            List<String> files = new FastArrayList<>();
            files.add("edges");
            files.add("geometry");
            files.add("location_index");
            files.add("nodes");
            files.add("nodes_ch_car");
            files.add("properties");
            files.add("shortcuts_car");
            files.add("string_index_keys");
            files.add("string_index_vals");
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            String Path = Objects.requireNonNull(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).getAbsolutePath();
            System.out.println("Path" + Path);
            File filetemp = new File(Path + "/graph-cache/" + city_name + "/");
            filetemp.mkdirs();
            Log.i("File", "" + filetemp + "   " + filetemp.canWrite());
            for (String str : files) {
                File file = new File(Path, "/graph-cache/" + city_name + "/" + str);
                Log.i("File", "" + file + "   " + file.canWrite());
                if (!file.exists()) {
                    Log.i("File", "" + file + "   " + file.canWrite());
//                    if (Build.VERSION.SDK_INT >= 29) {
//                        MyHandler handler = new MyHandler(context);
//                        downLoadThread downLoadThread = new downLoadThread(handler, IP.getGraph_cacheIP + city_name + "/" + str, Path + "/graph-cache/" + city_name + "/", str);
//                        downLoadThread.start();
//                    } else {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(IP.getGraph_cacheIP + city_name + "/" + str));
                        Log.i("FileDownLoad", str + "文件不存在");
                        request.setTitle("下载路线规划数据");
                        request.setDescription("正在下载路径规划数据.....");
                        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS + "/graph-cache/" + city_name + "/", str);
                        assert downloadManager != null;
                        downloadManager.enqueue(request);
//                    }
                    Log.i("FileDownLoad", str + "开始下载");
                } else {
                    Log.i("FileDownLoad", str + "文件存在");
                }
            }
            files.clear();
            //开始下载OSM数据
            files.add(city_name + "-latest.osm.pbf");
            filetemp = new File(Path + "/osm/" + city_name + "/");
            filetemp.mkdirs();
            for (String str : files) {
                File file = new File(Path, "/osm/" + city_name + "/" + str);
                if (!file.exists()) {
//                    if (Build.VERSION.SDK_INT >= 29) {
//                        MyHandler handler = new MyHandler(context);
//                        downLoadThread downLoadThread = new downLoadThread(handler, IP.getOsmIp + city_name + "/" + str, Path + "/osm/" + city_name + "/", str);
//                        downLoadThread.start();
//                    } else {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(IP.getOsmIp + city_name + "/" + str));
                        System.out.println(request);
                        request.setTitle("下载路线规划数据");
                        request.setDescription("正在下载OSM数据.....");
                        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS + "/osm/" + city_name + "/", str);
                        downloadManager.enqueue(request);
//                    }
                    Log.i("FileDownLoad", str + "开始下载");
                    Toast.makeText(context, "开始下载OSM数据", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("FileDownLoad", str + "文件存在");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
