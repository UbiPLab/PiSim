package Navi_utils;

import com.example.mygaode.MapActivity;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;

import java.util.List;
import java.util.Locale;

public class offline_navi {
    /**
     * 离线算路
     *
     * @param points 起点途径点终点列表
     * @return 算路结果 GPS坐标点列表
     */
    public static PointList getRoute(List<GHPoint> points) {
        if (points != null) {
            GHRequest ghRequest = new GHRequest(points).setProfile("car").setLocale(Locale.CHINA);
            GHResponse ghResponse = MapActivity.hopper.route(ghRequest);
            if (ghResponse.hasErrors()) {
                return null;
            } else {
                // use the best path, see the GHResponse class for more possibilities.
                PathWrapper path = ghResponse.getBest();
                PointList pointList = path.getPoints();
                return pointList;
            }
        } else {
            return null;
        }
    }
}
