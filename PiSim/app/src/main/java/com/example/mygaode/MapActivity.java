package com.example.mygaode;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.example.mygaode.Object.MyHandler;
import com.example.mygaode.Thread.RelyThread;
import com.example.mygaode.Thread.ReportThread;
import com.example.mygaode.Thread.TimeThread;
import com.example.mygaode.offlinemap.GetOfflineMapActivity;
import com.example.mygaode.utils.MapUtils;
import com.example.mygaode.Thread.queryCongestionThread;
import com.graphhopper.GraphHopper;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Navi_utils.LocationBean;
import overlay.DrivingRouteOverlay;
import parameter.parameter;

import com.example.mygaode.utils.AMapUtil;
import com.example.mygaode.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;

import static Navi_utils.offline_navi.getRoute;
import static com.amap.api.services.route.RouteSearch.DRIVING_SINGLE_DEFAULT;
import static com.example.mygaode.utils.Util.convertPointList;
import static parameter.parameter.city_name;
import static parameter.parameter.draw_result;

public class MapActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener, AdapterView.OnItemSelectedListener, AMap.OnMapLongClickListener, AMap.OnMapClickListener, RouteSearch.OnRouteSearchListener {
    private AMap aMap;
    private MapView mapView;
    private String[] itemLocationTypes = {"??????", "????????????1", "????????????2", "????????????3"};
    //??????????????????
    private Context mContext;
    private RouteSearch routeSearch;
    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;
    private List<LatLonPoint> wayToPoint;
    //????????????
    public static GraphHopper hopper;
    private MyLocationStyle myLocationStyle;
    //??????????????????????????????????????????
    private boolean flag = true;

    //????????????
    MyHandler handler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// ???????????????????????????
        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        this.mContext = this;
        init();
        //??????GPS???????????????????????????
        openGPS(this);
        //????????????????????????
        TimeThread timeThread = new TimeThread(handler, aMap, this);
        timeThread.start();
        //??????????????????
        ReportThread reportThread = new ReportThread(this, handler);
        reportThread.start();

    }

    /**
     * ???????????????
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        //???????????????????????????
        hopper = new GraphHopperOSM().forMobile();
        hopper.setDataReaderFile(Objects.requireNonNull(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).getAbsolutePath() + "/osm/" + city_name + "/" + city_name + "-latest.osm.pbf");
        hopper.setGraphHopperLocation(Objects.requireNonNull(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).getAbsolutePath() + "/graph-cache/" + city_name);
        hopper.setEncodingManager(EncodingManager.create("car"));
        hopper.setProfiles(new Profile("car").setVehicle("car").setWeighting("fastest"));
        hopper.getCHPreparationHandler().setCHProfiles(new CHProfile("car"));
        hopper.importOrLoad();

//        ???????????????
        Spinner spinnerGps = findViewById(R.id.spinner_gps);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, itemLocationTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGps.setAdapter(adapter);
        spinnerGps.setOnItemSelectedListener(this);
        Log.i("spinnerGps", "?????????????????????");
        //????????????
        // aMap.setOnMyLocationChangeListener(mAMapLocationListener);

        //????????????????????????
        if (wayToPoint == null) {
            wayToPoint = new ArrayList<>();
            System.out.println(wayToPoint.size());
        }
        aMap.setOnMapClickListener(MapActivity.this);
        aMap.setOnMapLongClickListener(MapActivity.this);
        //?????????????????????
//        aMap.setMapLanguage(AMap.ENGLISH);
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
        Log.i("Map", "?????????????????????");

        //????????????
        Button get_fresh = findViewById(R.id.get_refresh);
        get_fresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        //??????????????????
        final Button get_congestion = findViewById(R.id.paint_line);
        get_congestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCongistion getCongistion = new GetCongistion(mStartPoint, mEndPoint, wayToPoint);
                getCongistion.start();
            }
        });

        //????????????????????????
        Button submit_report = findViewById(R.id.submit_report);
        submit_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //??????????????????
                LocationBean locationBean = new LocationBean(parameter.current_locationBean.getLng(), parameter.current_locationBean.getLat());
                //????????????
                System.out.println("???????????????" + parameter.current_locationBean);
                LatLonPoint tempPoint = new LatLonPoint(parameter.current_locationBean.getLat(), parameter.current_locationBean.getLng());
//                aMap.addMarker(new MarkerOptions()
//                        .position(AMapUtil.convertToLatLng(tempPoint))
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.report_point)));
                RelyThread relyThread = new RelyThread(handler, locationBean);
                relyThread.start();
            }
        });
        LatLng latLng = new LatLng(39.905315185494004, 116.4033913070306);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
    }

    /**
     * ?????????????????????
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_cat_malicious:
                startActivity(new Intent(mContext, MaliciousActivity.class));
                break;
            case R.id.id_to_offLineMap:
                startActivity(new Intent(mContext, GetOfflineMapActivity.class));
                break;
            case R.id.id_getPath_gaoDe:
                RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
                RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, DRIVING_SINGLE_DEFAULT, wayToPoint, null, "");
                routeSearch.calculateDriveRouteAsyn(query);
                break;
            case R.id.id_getPath:
                if (mStartPoint != null && mEndPoint != null) {
                    PointList pointList = null;
                    pointList = getRoute(GaodeConvertToGps(mStartPoint, wayToPoint, mEndPoint));
//                SharedPreferences sharedPreferences = this.getSharedPreferences("Routes", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                if (mStartPoint != null && mEndPoint != null) {
//                    PointList pointList = null;
//                    float temp_a = 0;
//                    float temp_b = 0;
//                    for (int k = 0; k < 10; k++) {
//                        long startTime = System.currentTimeMillis();
//                        pointList = getRoute(GaodeConvertToGps());
//                        long endTime = System.currentTimeMillis();
//                        System.out.println("??????????????????????????????" + (endTime - startTime));
//                        temp_a = temp_a + (endTime - startTime);
//                        assert pointList != null;
//                        editor.putString("6-" + i, pointList.toString());
//                        editor.apply();
//                        startTime = System.currentTimeMillis();
//                        PointList pointList1 = convertPointList(sharedPreferences.getString("6-" + i, ""));
//                        endTime = System.currentTimeMillis();
//                        System.out.println("????????????????????????????????????2???" + (endTime - startTime));
//                        temp_b = temp_b + (endTime - startTime);
//                        i++;
//                        try {
//                            sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    System.out.println(temp_a / (float) 10);
//                    System.out.println(temp_b / (float) 10);
                    //????????????????????????
                    if (pointList != null) {
                        double[][] temp2 = new double[0][];
                        try {
                            temp2 = gpsConvertToGaode(pointList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        short[] congestionList = new short[temp2.length / parameter.interval + 1];
                        addPolylineInPlayGround(temp2, congestionList);
//                    Toast.makeText(MapActivity.this, "Offline path planning was successful", Toast.LENGTH_SHORT).show();
                        Message message = handler.obtainMessage(32);
                        handler.sendMessage(message);
                    }
                } else {
//                    Toast.makeText(MapActivity.this, "Please select the starting and ending point", Toast.LENGTH_SHORT).show();
                    Message message = handler.obtainMessage(29);
                    handler.sendMessage(message);
                }
                break;
            //   case R.id.id_getHistoryPath:

            default:
        }
        return true;
    }

    /**
     * ????????????
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu); //??????getMenuInflater()????????????MenuInflater????????????????????????inflate()??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????Menu???????????????
        return true; // true???????????????????????????????????????false????????????????????????????????????
    }


    int i = 0;

    //??????????????????
    class GetCongistion extends Thread {
        private PointList pointList;
        private boolean flag;
        private LatLonPoint mStartPoint;
        private LatLonPoint mEndPoint;
        private List<LatLonPoint> wayToPoint;

        GetCongistion(PointList pointList) {
            this.pointList = pointList;
            this.flag = false;
        }

        GetCongistion(LatLonPoint mStartPoint, LatLonPoint mEndPoint, List<LatLonPoint> wayToPoint) {
            this.flag = true;
            this.mStartPoint = mStartPoint;
            this.mEndPoint = mEndPoint;
            this.wayToPoint = wayToPoint;
        }

        @Override
        public void run() {
            if (flag) {
                //??????????????????????????????????????????
                if (mStartPoint != null && mEndPoint != null) {
                    //??????????????????????????????????????????
                    SharedPreferences sharedPreferences = MapActivity.this.getSharedPreferences("Routes", Context.MODE_PRIVATE);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                    //???????????????????????????
                    PointList pointList = getRoute(GaodeConvertToGps(mStartPoint, wayToPoint, mEndPoint));
                    //???????????????????????????
                    if (pointList != null) {
                        editor.putString("" + i, pointList.toString());
                        editor.apply();
                        i++;
                        double[][] query_point = new double[0][]; //???????????????
                        try {
                            //????????????
                            query_point = gpsConvertToGaode(pointList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //??????????????????
                        queryCongestionThread queryCongestionThread = new queryCongestionThread(handler, query_point, parameter.interval);
                        queryCongestionThread.start();
                        try {
                            queryCongestionThread.join();
                            //?????????????????????????????????
                            //??????????????????????????????
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (draw_result) {
                            //refresh();
                            addPolylineInPlayGround(query_point, parameter.congestionList);
                        }
                    } else {
                        Message message = handler.obtainMessage(28);
                        handler.sendMessage(message);
                    }
                } else {
                    Message message = handler.obtainMessage(29);
                    handler.sendMessage(message);
                }
            } else {
                //??????????????????????????????
                double[][] query_point; //???????????????
                try {
                    query_point = gpsConvertToGaode(pointList);

                    //???????????????????????????????????????????????????
                    LatLonPoint mStartPoint = new LatLonPoint(query_point[0][1], query_point[0][0]);
                    aMap.addMarker(new MarkerOptions()
                            .position(AMapUtil.convertToLatLng(mStartPoint))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_start)));
                    LatLonPoint mEndPoint = new LatLonPoint(query_point[query_point.length - 1][3], query_point[query_point.length - 1][2]);
                    aMap.addMarker(new MarkerOptions()
                            .position(AMapUtil.convertToLatLng(mEndPoint))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_end)));

                    //???????????????????????????????????????
                    queryCongestionThread queryCongestionThread = new queryCongestionThread(handler, query_point, parameter.interval);
                    queryCongestionThread.start();
                    queryCongestionThread.join();
                    //?????????????????????????????????
                    //??????????????????????????????
                    if (draw_result) {
                        //  refresh();
                        addPolylineInPlayGround(query_point, parameter.congestionList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * ??????????????????
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
//                queryFromHistory(0);
                // ?????????????????????????????????
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW));
                break;
            case 1:
                queryFromHistory(0);
                // ???????????????????????? ????????????
//                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW));
                break;
            case 2:
                queryFromHistory(1);
                // ???????????????????????????????????????????????????????????????????????????????????? ????????????????????????????????
//                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE));
                break;
            case 3:
                queryFromHistory(2);
                break;
        }
    }


    /**
     * ??????GPS ?????????????????????
     */
    public void openGPS(Context context) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Message message = handler.obtainMessage(23);
            handler.sendMessage(message);
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(settingsIntent);
        }
    }

    /**
     * ???????????????gps??????
     *
     * @return ????????????????????????????????????PointList
     */
    public List<GHPoint> GaodeConvertToGps(LatLonPoint mStartPoint, List<LatLonPoint> wayToPoint, LatLonPoint mEndPoint) {
        if (mStartPoint != null && mEndPoint != null) {
            List<GHPoint> pointList = new ArrayList<>();
            LatLonPoint temp = MapUtils.toWGS84Point(mStartPoint.getLatitude(), mStartPoint.getLongitude());
            GHPoint temp_GHPoint = new GHPoint(temp.getLatitude(), temp.getLongitude());
            pointList.add(temp_GHPoint);
            if (wayToPoint.size() != 0) {
                for (LatLonPoint latLonPoint : wayToPoint) {
                    temp = MapUtils.toWGS84Point(latLonPoint.getLatitude(), latLonPoint.getLongitude());
                    temp_GHPoint = new GHPoint(temp.getLatitude(), temp.getLongitude());
                    pointList.add(temp_GHPoint);
                }
            }
            temp = MapUtils.toWGS84Point(mEndPoint.getLatitude(), mEndPoint.getLongitude());
            temp_GHPoint = new GHPoint(temp.getLatitude(), temp.getLongitude());
            pointList.add(temp_GHPoint);
            return pointList;
        } else {
            return null;
        }
    }

    /**
     * ???????????? gps?????????????????????
     *
     * @param pointListTemp ??????????????????
     * @return ????????????gps?????????????????????
     */
    public double[][] gpsConvertToGaode(PointList pointListTemp) throws Exception {
        // ??????double???????????????????????????????????????????????????????????????????????????????????????????????????
        double[][] result = new double[pointListTemp.size() - 1][4];
        CoordinateConverter converter = new CoordinateConverter(this);
        // CoordType.GPS ?????????????????????
        converter.from(CoordinateConverter.CoordType.GPS);
        DPoint temp = new DPoint();
        DPoint desLatLng;
        for (int i = 0; i < pointListTemp.size() - 1; i++) {
            //?????????????????????
            temp.setLatitude(pointListTemp.get(i).lat);
            temp.setLongitude(pointListTemp.get(i).lon);
            converter.coord(temp);
            desLatLng = converter.convert();
            result[i][0] = desLatLng.getLongitude();
            result[i][1] = desLatLng.getLatitude();
            //?????????????????????
            temp.setLatitude(pointListTemp.get(i + 1).lat);
            temp.setLongitude(pointListTemp.get(i + 1).lon);
            converter.coord(temp);
            desLatLng = converter.convert();
            result[i][2] = desLatLng.getLongitude();
            result[i][3] = desLatLng.getLatitude();
        }
        return result;
    }

    private void refresh() {
        mStartPoint = null;
        mEndPoint = null;
        wayToPoint = null;
        wayToPoint = new ArrayList<>();
        aMap.clear();
    }

    /**
     * ????????????amap?????????
     */
    private void setUpMap() {
        // ??????????????????????????????????????????????????????????????????
        myLocationStyle = new MyLocationStyle();
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// ????????????????????????????????????
        aMap.setMyLocationEnabled(false);// ?????????true??????????????????????????????????????????false??????????????????????????????????????????????????????false
    }


    private void queryFromHistory(int i) {
        refresh();
        Message message = handler.obtainMessage(27);
        handler.sendMessage(message);
        SharedPreferences sharedPreferences = MapActivity.this.getSharedPreferences("Routes", Context.MODE_PRIVATE);
        PointList pointList = convertPointList(sharedPreferences.getString("" + i, ""));
        GetCongistion getCongistion = new GetCongistion(pointList);
        getCongistion.start();
    }

//    //???????????????????????????
//    AMap.OnMyLocationChangeListener mAMapLocationListener = new AMap.OnMyLocationChangeListener() {
//        @Override
//        public void onMyLocationChange(Location location) {
//            parameter.current_locationBean.setLng(aMap.getMyLocation().getLongitude());
//            parameter.current_locationBean.setLat(aMap.getMyLocation().getLatitude());
//        }
//    };
    //??????????????????
//    AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
//        @Override
//        public void onLocationChanged(AMapLocation amapLocation) {
//            parameter.current_locationBean.setLng(amapLocation.getLongitude());
//            parameter.current_locationBean.setLat(amapLocation.getLatitude());
//        }
//    };

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * ??????????????????
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * ??????????????????
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * ??????????????????
     */
    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * ??????????????????
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMyLocationChange(Location location) {
        // ??????????????????
        if (location != null) {
            Log.e("amap", "onMyLocationChange ??????????????? lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            Bundle bundle = location.getExtras();
            if (bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // ????????????????????????GPS WIFI???????????????????????????????????????SDK??????
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
                Log.e("amap", "??????????????? code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType);
            } else {
                Log.e("amap", "??????????????? bundle is null ");

            }
        } else {
            Log.e("amap", "????????????");
        }
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void onMapLongClick(LatLng latLng) {
        if (mStartPoint == null) {
            mStartPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
            aMap.addMarker(new MarkerOptions()
                    .position(AMapUtil.convertToLatLng(mStartPoint))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_start)));
        } else if (mEndPoint == null) {
            mEndPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
            aMap.addMarker(new MarkerOptions()
                    .position(AMapUtil.convertToLatLng(mEndPoint))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_end)));
        }
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void onMapClick(LatLng latLng) {
        // TODO Auto-generated method stub
        LatLonPoint newPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        if (wayToPoint == null) {
            wayToPoint = new ArrayList<>();
        }
//        LocationBean locationBean = new LocationBean(newPoint.getLongitude(), newPoint.getLatitude());
//        RelyThread relyThread = new RelyThread(handler, locationBean);
//        relyThread.start();
        wayToPoint.add(newPoint);
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(newPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_through)));
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
        //????????????????????????
    }

    /**
     * ??????????????????
     *
     * @param driveRouteResult ??????????????????
     * @param errorCode        ???????????????
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int errorCode) {
        //aMap.clear();// ?????????????????????????????????
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (driveRouteResult != null && driveRouteResult.getPaths() != null) {
                if (driveRouteResult.getPaths().size() > 0) {
                    final DrivePath drivePath = driveRouteResult.getPaths()
                            .get(0);
                    if (drivePath == null) {
                        return;
                    }
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            mContext, aMap, drivePath,
                            driveRouteResult.getStartPos(),
                            driveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(true);//????????????marker????????????
                    drivingRouteOverlay.setIsColorfulline(true);//????????????????????????????????????????????????true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    RelativeLayout mBottomLayout = new RelativeLayout(this);
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                    TextView mRotueTimeDes = new TextView(this);
                    mRotueTimeDes.setText(des);
                    TextView mRouteDetailDes = new TextView(this);
                    mRouteDetailDes.setVisibility(View.VISIBLE);
                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                } else if (driveRouteResult.getPaths() == null) {
                    Message message = handler.obtainMessage(30);
                    handler.sendMessage(message);
                }
            } else {
                Message message = handler.obtainMessage(30);
                handler.sendMessage(message);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        //??????????????????
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
        //??????????????????
    }

    /**
     * ????????????????????????
     */
    private void addPolylineInPlayGround(double[][] route, short[] congestionList) {
        short[][] type = {{1, 255, 1}, {26, 189, 230}, {255, 255, 1}, {255, 1, 1}};
        for (int i = 0; i < route.length; i++) {
            List<LatLng> latLngs = MapUtils.readLatLngs(route[i]);
            //    System.out.println(level[i] * 50);
            aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(20).color(
                    Color.argb(255, type[congestionList[(i + 1) / parameter.interval]][0],
                            type[congestionList[(i + 1) / parameter.interval]][1],
                            type[congestionList[(i + 1) / parameter.interval]][2])));
        }
    }


//    private void dissmissProgressDialog() {
//        if (progDialog != null) {
//            progDialog.dismiss();
//        }
//    }
}
