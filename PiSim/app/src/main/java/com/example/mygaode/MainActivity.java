package com.example.mygaode;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.mygaode.Object.MyHandler;
import com.example.mygaode.Thread.initializeThread;

import org.jetbrains.annotations.NotNull;
import org.ujmp.core.collections.list.FastArrayList;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import static com.example.mygaode.utils.MainUtils.getOffLineRouteData;
import static parameter.parameter.city_name;


public class MainActivity extends AppCompatActivity {
    private int updateIp = 0;
    //如果设置了target > 28，需要增加这个权限，否则不会弹出"始终允许"这个选择框
    private static String BACK_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
        setContentView(R.layout.activity_main);
        MyHandler myHandlder = new MyHandler(this);
        initializeThread initializeThread = new initializeThread(this, myHandlder);
        initializeThread.start();
    }

    public void to_Login(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }


    public void getRouteData(View view) {
        if (checkFile()) {
            Toast.makeText(this, "您已下载离线算路数据", Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "You have downloaded the offline routing data", Toast.LENGTH_SHORT).show();
        } else {
            getOffLineRouteData(this);
        }
    }


    /**
     * 软件分享
     * 默认选取手机所有可以分享的APP
     */
    public void allShare(View view) {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("text/plain");//设置分享内容的类型
        share_intent.putExtra(Intent.EXTRA_SUBJECT, "分享");//添加分享内容标题
//        share_intent.putExtra(Intent.EXTRA_TEXT, "PiSim,一款隐私保护导航软件。。" + "http://114.55.33.26/PiSim.apk");//添加分享内容
        share_intent.putExtra(Intent.EXTRA_TEXT, "PiSim,A privacy protection navigation software.。" + "http://114.55.33.26/PiSim.apk");//添加分享内容
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, "PiSim");
        startActivity(share_intent);
    }

    public void refershParam(View view) {
        MyHandler myHandlder = new MyHandler(this);
        initializeThread initializeThread = new initializeThread(this, myHandlder);
        initializeThread.start();

    }

    public void togetSI(View view) {
        if (updateIp > 2) {
            updateIp = 0;
            //进入获取SI界面
            startActivity(new Intent(this, GetSIActivity.class));
        }
        updateIp++;

    }


    /*************************************** 权限检查及一些方法******************************************************/

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_CONFIGURATION,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            BACK_LOCATION_PERMISSION
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    @Override
    protected void onResume() {
        try {
            super.onResume();
            if (Build.VERSION.SDK_INT >= 23) {
                if (isNeedCheck) {
                    checkPermissions(needPermissions);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @since 2.5.0
     */
    @TargetApi(23)
    private void checkPermissions(String... permissions) {
        try {
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    try {
                        String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                        Method method = getClass().getMethod("requestPermissions", String[].class, int.class);
                        method.invoke(this, array, 0);
                    } catch (Throwable ignored) {
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @since 2.5.0
     */
    @TargetApi(23)
    private List<String> findDeniedPermissions(String[] permissions) {
        try {
            List<String> needRequestPermissonList = new ArrayList<String>();
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                for (String perm : permissions) {
                    if (checkMySelfPermission(perm) != PackageManager.PERMISSION_GRANTED
                            || shouldShowMyRequestPermissionRationale(perm)) {
                        //是否需要检测后台定位权限，设置为true时，如果用户没有给予后台定位权限会弹窗提示
                        boolean needCheckBackLocation = false;
                        if (BACK_LOCATION_PERMISSION.equals(perm)) {
                            continue;
                        }
                        needRequestPermissonList.add(perm);
                    }
                }
            }
            return needRequestPermissonList;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private int checkMySelfPermission(String perm) {
        try {
            Method method = getClass().getMethod("checkSelfPermission", new Class[]{String.class});
            Integer permissionInt = (Integer) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return -1;
    }

    private boolean shouldShowMyRequestPermissionRationale(String perm) {
        try {
            Method method = getClass().getMethod("shouldShowRequestPermissionRationale", String.class);
            return (Boolean) method.invoke(this, perm);
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 检测是否所有的权限都已经授权
     *
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        try {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] paramArrayOfInt) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (requestCode == PERMISSON_REQUESTCODE) {
                    if (!verifyPermissions(paramArrayOfInt)) {
                        //showMissingPermissionDialog();
                        isNeedCheck = false;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
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
        String Path = Objects.requireNonNull(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).getAbsolutePath();
        for (String str : files) {
            File file = new File(Path, "/graph-cache/" + city_name + "/" + str);
            if (!file.exists()) {
                return false;
            }
        }
        files.clear();
        for (String str : files) {
            File file = new File(Path, "/osm/" + city_name + "/" + str);
            if (!file.exists()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("当前应用缺少必要权限。\\n\\n请点击\\\"设置\\\"-\\\"权限\\\"-打开所需权限");

            // 拒绝, 退出应用
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                finish();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setPositiveButton("设置",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                startAppSettings();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setCancelable(false);

            builder.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        try {
            Intent intent = new Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
