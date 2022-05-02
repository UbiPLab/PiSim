package parameter;

import org.ujmp.core.Matrix;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Navi_utils.LocationBean;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

public class parameter {
    //缓存线程返回的数据
    public static String result;
    //交通拥堵情况表
    public static short[] congestionList;
    public static boolean draw_result;

    public static Map<String, String> User_KeyMap;

    public static Field G1;
    public static Field G2;
    public static Pairing pairing;

    // TA公开参数
    public static Element gpk_g;
    public static Element gpk_g2;
    public static Element gpk_A;
    public static Element gpk_B;
    public static String TA_rsa_pub;
    public static int MC;

    //RSU相关参数
    public static String RSU_rsa_pub;

    //TA给司机的clsignature
    public static BigInteger v;
    public static BigInteger e;
    public static BigInteger s;
    public static BigInteger message;

    public static String username;
    public static String password;

    //当前时间
    public static int te;
    //时间跟新频度
    public static int cycle =  1000*60*2;


    //模糊搜索相关参数
    public static Matrix HKP;
    public static Matrix SK1;
    public static Matrix SK2;
    public static Matrix V;
    public static BigInteger K;
    public static double[] threshold;
    public static double thresholdQuery;


    //driver store
    public static String driver_rsa_pub;
    public static String driver_rsa_pri;
    public static Element Ei1;
    public static Element Ei2;
    public static String si;

    //零知识证明相关参数
    public static BigInteger ZKPK_rou;
    public static BigInteger ZKPK_F;
    public static BigInteger ZKPK_g;
    public static BigInteger ZKPK_b;


    //司机之间生成报告
    public static String pidj;
    public static byte[] hsvj;
    public static List<String> pidjss;
    public static List<byte[]> hsvjss = new ArrayList<>();
    public static List<String> pidjs = new ArrayList<String>();
    public static List<byte[]> hsvjs = new ArrayList<byte[]>();
    //司机切换wifi的基准时间
    public static int frequency = 1000*5;
    public static LocationBean current_locationBean = new LocationBean(116.4033913070306,39.905315185494004);

    //请求次数
    public static int rci;


    //SK V的长度为 column k1
    public static int k1 = 35;

    // 选点间隔长度
    public static  short interval = 1;

    //司机发起导航查询时要用到的参数
    public static int r1r2_length = 256;

//    public static String graph_cache_filePath = "/graph-cache/beijing/";
//    public static String osm_filePath = "/osm/beijing/";
    public static String city_name = "beijing";


}
