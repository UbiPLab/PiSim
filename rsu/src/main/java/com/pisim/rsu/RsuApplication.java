package com.pisim.rsu;

import com.pisim.rsu.service.CongestionInfoService;
import com.pisim.rsu.service.DrivingReportService;
import com.pisim.rsu.service.Impl.CongestionInfoServiceImpl;
import com.pisim.rsu.service.Impl.DrivingReportServiceImpl;
import com.pisim.rsu.service.Impl.NaviQueryInfoServiceImpl;
import com.pisim.rsu.service.NaviQueryInfoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import com.pisim.rsu.parameterUtil.IP;
import com.pisim.rsu.parameterUtil.parameter;
import com.pisim.rsu.utils.CongestionUtil;
import com.pisim.rsu.encryption.Initialize;

import java.math.BigInteger;

import static com.pisim.rsu.parameterUtil.parameter.*;

@SpringBootApplication
public class RsuApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
//        初始化阶段
        String pairingPath = args[0];
        IP.taIp = args[1];
        IP.nspIp = args[2];
//        String pairingPath = "./config/pairing.properties";
        System.out.println("TA的IP地址为:" + IP.taIp);
        System.out.println("NSP的IP地址为:" + IP.nspIp);
        Initialize initialize = new Initialize(pairingPath);
        initialize.start();
        origin_te = (int) (System.currentTimeMillis() / (cycle));
        try {
            initialize.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("RSU初始化失败");
        }
        SpringApplication.run(RsuApplication.class, args);

        //运行阶段
        ApplicationContext context = SpringBootUtil.getApplicationContext();
        DrivingReportService drivingReportService = context.getBean(DrivingReportServiceImpl.class);
        NaviQueryInfoService naviQueryInfoService = context.getBean(NaviQueryInfoServiceImpl.class);
        TimeThread timeThread = new TimeThread(context, drivingReportService, naviQueryInfoService);
        timeThread.start();

//旧版本方案
//        Calendar calendar = Calendar.getInstance();
//        Date firstTime = calendar.getTime();
//        ApplicationContext context = SpringBootUtil.getApplicationContext();
//        DrivingReportService drivingReportService = context.getBean(DrivingReportServiceImpl.class);
//        //按时间单位 过滤虚假路况信息
//        Timer anotherTimer = new Timer();
//        anotherTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                //从数据库中删除过时的驾驶报告
//                drivingReportService.deleteOverdueDrivingReport(parameter.reportValidTime);
//                //过滤虚假交通报告
//                drivingReportService.filterFalseDrivingReport();
//                CongestionUtil congestionUtil = new CongestionUtil();
//                CongestionInfoService congestionInfoService = context.getBean(CongestionInfoServiceImpl.class);
//                congestionUtil.generateCongestion(congestionInfoService, drivingReportService);
//                //删除过时的交通拥堵信息
//                congestionInfoService.deleteOverdueCongestionInfo(parameter.congestionInfoValidTime);
//            }
//        }, firstTime, cycle);
    }

    //运行阶段
    @SuppressWarnings("InfiniteLoopStatement")
    private static class TimeThread extends Thread {
        ApplicationContext context;
        DrivingReportService drivingReportService;
        NaviQueryInfoService naviQueryInfoService;

        TimeThread(ApplicationContext context, DrivingReportService drivingReportService, NaviQueryInfoService naviQueryInfoService) {
            this.context = context;
            this.drivingReportService = drivingReportService;
            this.naviQueryInfoService = naviQueryInfoService;
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
            parameter.grlpis.clear();
            parameter.rlpis.clear();
            for (int i = 0; i < parameter.MC; i++) {
                String temp = "" + te + i;
                BigInteger grlpi_temp = parameter.pairing.getZr().newElement().setFromHash(temp.getBytes(), 0, temp.getBytes().length).toBigInteger();
                BigInteger grlpi = grlpi_temp.modPow(((parameter.ZKPK_F.subtract(BigInteger.ONE)).divide(parameter.ZKPK_rou)), parameter.ZKPK_F);
                parameter.grlpis.add(grlpi);
            }

            while (true) {
                //判断时间历元是否已更新
                if ((int) (System.currentTimeMillis() / (cycle)) != parameter.te) {
                    te = (int) (System.currentTimeMillis() / (cycle));
                    System.out.println("当前时间历元是:" + te);
                    parameter.grlpis.clear();
                    parameter.rlpis.clear();
                    for (int i = 0; i < parameter.MC; i++) {
                        String temp = "" + te + i;
                        BigInteger grlpi_temp = parameter.pairing.getZr().newElement().setFromHash(temp.getBytes(), 0, temp.getBytes().length).toBigInteger();
                        BigInteger grlpi = grlpi_temp.modPow(((parameter.ZKPK_F.subtract(BigInteger.ONE)).divide(parameter.ZKPK_rou)), parameter.ZKPK_F);
                        parameter.grlpis.add(grlpi);
                    }
                    for (int i = 0; i < parameter.MC; i++) {
                        String temp = "" + (te - 1) + i;
                        BigInteger grlpi_temp = parameter.pairing.getZr().newElement().setFromHash(temp.getBytes(), 0, temp.getBytes().length).toBigInteger();
                        BigInteger grlpi = grlpi_temp.modPow(((parameter.ZKPK_F.subtract(BigInteger.ONE)).divide(parameter.ZKPK_rou)), parameter.ZKPK_F);
                        parameter.grlpis.add(grlpi);
                    }

                    //从数据库中删除过时的驾驶报告
                    drivingReportService.deleteOverdueDrivingReport(parameter.reportValidTime, true);
                    drivingReportService.deleteOverdueDrivingReport(parameter.reportValidTime, false);
                    //过滤虚假交通报告
                    drivingReportService.filterFalseDrivingReport();
                    CongestionUtil congestionUtil = new CongestionUtil();
                    CongestionInfoService congestionInfoService = context.getBean(CongestionInfoServiceImpl.class);
                    congestionUtil.generateCongestion(congestionInfoService, drivingReportService);
                    //删除过时的交通拥堵信息
                    congestionInfoService.deleteOverdueCongestionInfo(parameter.congestionInfoValidTime);
                    //删除过期的历史导航查询报告
                    naviQueryInfoService.deleteNaviQueryInfo((double) reportValidTime);


                    RSUNaviCount_Last = RSUNaviCount_temp;
                    RSUNaviValidCount_Last = RSUNaviValidCount_temp;
                    RSUReportRequestCount_Last = RSUReportRequestCount_temp;
                    RSUReportRequestValidCount_Last = RSUReportRequestValidCount_temp;
                    RSUReportValidCount_Last = RSUReportValidCount_temp;
                    RSUNaviCount_temp = 0;
                    RSUNaviValidCount_temp = 0;
                    RSUReportRequestCount_temp = 0;
                    RSUReportRequestValidCount_temp = 0;
                    RSUReportValidCount_temp = 0;
                } else {
                    long time_temp;
//                    System.out.println("当前"+System.currentTimeMillis());
//                    System.out.println((long)te*(long)cycle);
                    time_temp = System.currentTimeMillis() - (long) te * (long) cycle;
//                    System.out.println("已经过时间"+time_temp);
//                    System.out.println("要休眠的时间"+(cycle-time_temp));
                    System.out.println(cycle - time_temp);
                    if (cycle - time_temp > 1000) {
                        System.out.println("**"+(cycle - time_temp));
                        sleep(cycle - time_temp);
                    }
                }
            }
        }
    }

}
