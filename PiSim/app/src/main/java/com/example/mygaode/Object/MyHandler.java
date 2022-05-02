package com.example.mygaode.Object;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.mygaode.MapActivity;

import org.jetbrains.annotations.NotNull;
import org.ujmp.core.collections.list.FastArrayList;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static com.example.mygaode.utils.MainUtils.getOffLineRouteData;
import static parameter.parameter.rci;

public class MyHandler extends Handler {
    public Context context;

    public MyHandler(Context context) {
        this.context = context;
    }

    public void handleMessage(@NotNull Message message) {
        System.out.println("handle--id-->" + Thread.currentThread().getId());
        System.out.println("handle--name-->" + Thread.currentThread().getName());//显示为main主线程
        super.handleMessage(message);
        switch (message.what) {
            // 在收到消息时，对界面进行更新 ，Handler处于主线程，可以对UI进行更新
            case 1://参数初始化完成
                printToast("请求公共参数完成", 0);
                break;
            case 2://下载配置文件完成
                printToast("请求公共参数失败", 0);
                break;
            case 3://下载路线规划相关数据完成
                printToast("下载路线规划相关数据完成", 0);
                break;
            case 4://注册成功
                printToast("向TA注册成功", 1);
                if (checkFile()) {
                    context.startActivity(new Intent(context, MapActivity.class));
                } else {
                    Toast.makeText(context, "你还没有下载离线算路数据，正在开始下载", Toast.LENGTH_SHORT).show();
                    getOffLineRouteData(context);
                }
                break;
            case 5://注册失败
                printToast("向TA注册失败\n用户身份无效或用户名重复使用", 1);
                break;
            case 6://登录成功
                printToast("向TA登录成功", 1);
                if (checkFile()) {
                    context.startActivity(new Intent(context, MapActivity.class));
                } else {
                    Toast.makeText(context, "你还没有下载离线算路数据，正在开始下载", Toast.LENGTH_SHORT).show();
                    getOffLineRouteData(context);
                }
                break;
            case 7://登录失败
                printToast("向TA登录失败", 0);
                break;
            case 8:
                printToast("RSU匿名认证身份通过", 0);
                break;
            case 10:
                printToast("匿名认证成功\n第"+(rci+1)+"次向RSU请求拥堵信息成功", 0);
                break;
            case 11:
                printToast("身份无效\n第"+(rci+1)+"次向RSU请求拥堵信息失败", 0);
                break;
            case 12:
                printToast("向RSU提交路况报告成功", 0);
                break;
            case 13:
                printToast("身份无效\n向RSU提交路况报告失败", 0);
                break;
            case 18:
                printToast("请求ClSignature成功", 0);
                break;
            case 19:
                printToast("请求ClSignature失败", 0);
                break;
            case 20:
                printToast("网络存在延迟，请稍等", 0);
                break;
            case 21:
                printToast("恶意请求！\n第"+(rci+1)+"次请求无效", 1);
                break;
            case 22:
                printToast("与服务器密钥交换失败，请检查网络", 1);
                break;
            case 23:
                printToast("需要打开GPS以扫描周围用户WiFi握手信息", 1);
                break;
            case 24:
                printToast("文件下载出错", 0);
                break;
            case 25:
                printToast("请重新选点查询", 0);
                break;
            case 26:
                printToast("请求异常，请刷新公共参数", 0);
                break;
            case 27:
                printToast("选择历史出行路线", 0);
                break;
            case 28:
                printToast("您的离线路径规划数据不完整，请重新下载", 0);
                break;
            case 29:
                printToast("请先选择起点终点", 0);
                break;
            case 31:
                printToast("请先选择起点终点", 0);
                break;
            case 32:
                printToast("离线路径规划成功", 0);
                break;

            default:
                printToast("消息错误", 0);
        }
    }

    public void handleMessage_en(@NotNull Message message) {
        System.out.println("handle--id-->" + Thread.currentThread().getId());
        System.out.println("handle--name-->" + Thread.currentThread().getName());//显示为main主线程
        super.handleMessage(message);
        switch (message.what) {
            // 在收到消息时，对界面进行更新 ，Handler处于主线程，可以对UI进行更新
            case 1://参数初始化完成
                printToast("Request public parameters complete", 0);
                break;
            case 2://下载配置文件完成
                printToast("The request for a public parameter failed", 0);
                break;
            case 3://下载路线规划相关数据完成
                printToast("Download route planning related data completed", 0);
                break;
            case 4://注册成功
                printToast("Registered with TA successfully", 1);

                if (checkFile()) {
                    context.startActivity(new Intent(context, MapActivity.class));
                } else {
                    Toast.makeText(context, "You have not downloaded the offline routing data, you are starting to download", Toast.LENGTH_SHORT).show();
                    getOffLineRouteData(context);
                }
                break;
            case 5://注册失败
                printToast("Failed to register with TA", 0);
                break;
            case 6://登录成功
                printToast("The signature and SK were successfully obtained from TA", 1);
                if (checkFile()) {
                    context.startActivity(new Intent(context, MapActivity.class));
                } else {
                    Toast.makeText(context, "You have not downloaded the offline routing data, you are starting to download", Toast.LENGTH_SHORT).show();
                    getOffLineRouteData(context);
                }
                break;
            case 7://登录失败
                printToast("Failed to get information from TA", 0);
                break;
            case 8:
                printToast("RSU anonymous authentication identity passed", 0);
                break;
            case 10:
                printToast("Anonymous authentication successful\n" +
                        "The request for congestion information from the RSU was successful", 0);
                break;
            case 11:
                printToast("Identity is invalid\n" +
                        "The request for congestion information to the RSU failed", 0);
                break;
            case 12:
                printToast("Successful submission of a road traffic report to RSU", 0);
                break;
            case 13:
                printToast("Identity is invalid\n" +
                        "Failure to submit road traffic report to RSU", 0);
                break;
            case 18:
                printToast("Requested ClSignature successfully", 0);
                break;
            case 19:
                printToast("Request ClSignature failed", 0);
                break;
            case 20:
                printToast("There is a delay in the network. Please wait", 0);
                break;
            case 21:
                printToast("Malicious request!", 1);
                break;
            case 22:
                printToast("Failed to exchange key with server. Check network", 1);
                break;
            case 23:
                printToast("You need to turn on THE GPS \n to scan the WiFi handshake information of nearby users", 1);
                break;
            case 24:
                printToast("File download error", 0);
                break;
            case 25:
                printToast("Please re-select the query", 0);
                break;
            case 26:
                printToast("Request exception. Refresh public parameters", 0);
                break;

            default:
                printToast("This is a Error message ", 0);
        }
    }


    private void printToast(String message, int toastType) {
        Toast.makeText(this.context, message, toastType).show();
    }

    /**
     * 判断算路数据是否齐全
     */
    public boolean checkFile() {
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
        String Path = Objects.requireNonNull(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).getAbsolutePath();
        for (String str : files) {
            File file = new File(Path, "/graph-cache/beijing/" + str);
            if (!file.exists()) {
                return false;
            }
        }
        files.clear();
        for (String str : files) {
            File file = new File(Path, "/osm/beijing/" + str);
            if (!file.exists()) {
                return false;
            }
        }
        return true;
    }
}