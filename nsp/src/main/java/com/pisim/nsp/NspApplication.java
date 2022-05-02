package com.pisim.nsp;

import com.pisim.nsp.parameterUtil.IP;
import com.pisim.nsp.service.CongestionInfoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.pisim.nsp.parameterUtil.parameter;
import com.pisim.nsp.encryption.Initialize;


import static com.pisim.nsp.parameterUtil.parameter.*;

@SpringBootApplication
public class NspApplication {

    public static void main(String[] args) {
        //初始化阶段
        IP.taIp = args[0];
        System.out.println("TA的IP地址为:" + IP.taIp);
        Initialize initialize = new Initialize();
        initialize.start();
        try {
            initialize.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SpringApplication.run(NspApplication.class, args);

        //运行阶段
        ApplicationContext context = SpringBootUtil.getApplicationContext();
        TimeThread timeThread = new TimeThread(context);
        timeThread.start();

//旧版本方案
//        Calendar calendar = Calendar.getInstance();
//        Date firstTime = calendar.getTime();
//        Timer anotherTimer = new Timer();
//        anotherTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    //定期删除过期的交通拥堵数据
//                    CongestionInfoService congestionInfoService = context.getBean(CongestionInfoService.class);
//                    congestionInfoService.deleteOverdueCongestionInfo(parameter.validTime);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, firstTime, cycle);
    }

    //数据更新线程
    @SuppressWarnings("InfiniteLoopStatement")
    private static class TimeThread extends Thread {
        ApplicationContext context;
        TimeThread(ApplicationContext context) {
            this.context = context;
        }

        @Override
        public void run() {
            super.run();
            try {
                update();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        private void update() throws InterruptedException {
            //获取当前时间历元
            te = (int) (System.currentTimeMillis() / (cycle));
            System.out.println("当前时间历元是:" + te);
            while (true) {
                //判断时间历元是否已更新
                if ((int) (System.currentTimeMillis() / (cycle)) != te) {
                    te = (int) (System.currentTimeMillis() / (cycle));
                    System.out.println("当前时间历元是:" + te);
                    NSPtrafficInfoCount_Last = NSPtrafficInfoCount_temp;
                    NSPNaviCount_Last = NSPNaviCount_temp;
                    NSPtrafficInfoCount_temp = 0;
                    NSPNaviCount_temp = 0;
                    //定期删除过期的交通拥堵数据
                    CongestionInfoService congestionInfoService = context.getBean(CongestionInfoService.class);
                    congestionInfoService.deleteOverdueCongestionInfo(parameter.validTime);
                }else {
                    sleep(cycle);
                }
            }
        }
    }

}

