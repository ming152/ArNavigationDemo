package com.example.arnavigationdemo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.arnavigationdemo.BaseActivity;
import com.example.arnavigationdemo.manager.OrientationManager;
import com.example.arnavigationdemo.R;
import com.example.arnavigationdemo.bean.WayPoint;
import com.example.arnavigationdemo.opengl.OpenGLRenderer;
import com.example.arnavigationdemo.opengl.mesh.Dot;
import com.example.arnavigationdemo.ui.widget.CameraPreview;
import com.example.arnavigationdemo.ui.widget.CompassView;
import com.example.arnavigationdemo.util.LatLngUtil;
import com.example.arnavigationdemo.util.LocationManger;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends BaseActivity implements OrientationManager.OrientationChangeListener {
    private static final String TAG = NavigationActivity.class.getSimpleName();
    private static final String START_LOCATION = "startLocation";
    private static final String TARGET_LOCATION = "targetLocation";
    private FrameLayout rootView;
    private CompassView compassView;
    private TextView naviInstruction;
    private LatLng startLatLng;
    private LatLng targetLatLng;
    private RoutePlanSearch mSearch;
    private GLSurfaceView glSurfaceView;
    private OpenGLRenderer renderer;
    private OrientationManager orientationManager;
    private List<WalkingRouteLine.WalkingStep> routeSteps;
    private List<WayPoint> mStepPoints = new ArrayList<>();
    private static final int INDICATOR_NUMBER = 10;
    private LocationManger locationManger;
    private MyLocationListener myListener = new MyLocationListener();
    private boolean isLocated;
    TextView tv;

    public static void startActivity(Context context, LatLng startLocation, LatLng targetLocation){
        Intent intent = new Intent(context, NavigationActivity.class);
        intent.putExtra(START_LOCATION, startLocation);
        intent.putExtra(TARGET_LOCATION, targetLocation);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        rootView = findViewById(R.id.navigation_root_view);
        //指南针控件
        compassView = findViewById(R.id.compassView);
        //导航文字提示
        naviInstruction = findViewById(R.id.navi_instruction);
        //通过传感器获取手机旋转角度
        orientationManager = new OrientationManager(this, this);
        //初始化opengl以及相机预览
        initSurfaceView();
        //定位手机位置
        initLocation();
    }

    /**
     * 初始化opengl以及相机预览
     */
    private void initSurfaceView(){

        /**
         * 设置相机预览SurfaceView
         * */
        CameraPreview cameraPreview = new CameraPreview(this);
        SurfaceView cameraSurfaceView = cameraPreview.getPreview();

        // Create a OpenGL view.
        glSurfaceView = new GLSurfaceView(this);
        // Creating and attaching the renderer.
        renderer = new OpenGLRenderer();

        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);//这一步和下面的setFormat都是为了能够使surfaceView能够显示出来
        glSurfaceView.setZOrderMediaOverlay(true);
        glSurfaceView.setRenderer(renderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);//GLSurfaceView.RENDERMODE_CONTINUOUSLY:持续渲染(默认)
        //GLSurfaceView.RENDERMODE_WHEN_DIRTY:脏渲染,命令渲染
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);//设置glview为透明的

        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.addView(cameraSurfaceView, 0, params1);
        rootView.addView(glSurfaceView, 0, params1);
        tv = new TextView(this);
        tv.setBackgroundColor(Color.WHITE);
        rootView.addView(tv, params3);
    }

    /**
     * 百度地图定位自身位置
     */
    private void initLocation(){
        locationManger = new LocationManger();
        //两秒定位一次
        locationManger.setScanTime(2000);
        locationManger.setLocationListener(myListener);
        locationManger.start();
    }

    private void refreshSurfaceView(){
        renderer.clearMesh();
        getClosePointIndex();
        if(currentPointIndex > mStepPoints.size()-1){
            return;
        }
        int tempNumber = 0; //指示图标个数
        int tempIndex = 0;
        float lastAngle = 0;
        naviInstruction.setText(mStepPoints.get(currentPointIndex).getInstruction());
        if(mStepPoints != null){
            if((mStepPoints.size() - currentPointIndex) >= INDICATOR_NUMBER){
                tempNumber = INDICATOR_NUMBER;
            }else {
                tempNumber = INDICATOR_NUMBER - (mStepPoints.size() - currentPointIndex);
            }
            int currentDis = (int) LatLngUtil.getDistance(startLatLng, mStepPoints.get(currentPointIndex).getLatLng());
            Log.d(TAG, "currentPointIndex = " + currentPointIndex);
            Log.d(TAG, "ClosePoint dis = " + currentDis);
            if(currentDis>0) {
                //红色标记指示从当前定位位置到规划路线点
                lastAngle = (float) LatLngUtil.getAngle(startLatLng, mStepPoints.get(currentPointIndex).getLatLng());
                Log.d(TAG, "current angle : " + lastAngle);
                Dot dot1 = new Dot(0.2f, 40);
                dot1.rz = -lastAngle;
                dot1.setColor(0.8f, 0.1f, 0.1f, 1f);    //红色
                // Add the plane to the renderer.
                renderer.addMesh(dot1);
                if(currentDis > 10){
                    currentDis = 10;
                }
                for(int i=1; i<=currentDis; i++){
                    Dot dot = new Dot(0.2f, 40);
                    dot.y = 1;
                    dot.setColor(0.8f, 0.1f, 0.1f, 1f);
                    // Add the plane to the renderer.
                    renderer.addMesh(dot);
                }
            }else {
                Dot dot1 = new Dot(0.2f, 40);
                dot1.y = currentDis;
                dot1.rz = -lastAngle;
                dot1.setColor(0.1f, 0.8f, 0.1f, 1f);
                // Add the plane to the renderer.
                renderer.addMesh(dot1);
            }
            tempIndex++;

            for(; tempIndex<tempNumber; tempIndex++){
                float tempAngle = (float) LatLngUtil.getAngle(mStepPoints.get(currentPointIndex + tempIndex -1).getLatLng(), mStepPoints.get(currentPointIndex+tempIndex).getLatLng());
                Log.d(TAG, tempIndex + " tempAngle angle : " + tempAngle);
                Dot dot = new Dot(0.2f, 40);
                dot.y = 1;
                dot.rz = -tempAngle + lastAngle;
                lastAngle = tempAngle;
                dot.setColor(0.1f,0.8f,0.1f,1f);
                // Add the plane to the renderer.
                renderer.addMesh(dot);
            }
        }
    }

    //当前位置处于路径中哪个点的附近
    private int currentPointIndex;

    /**
     * 寻找当前位置已经到了路径中的哪个点
     * 算法有待优化
     * 目前算法：如果目前已经到第n点，当前定位点距离路径第n点距离为d1,距离第n+1距离为d2，
     * 如果d1<d2，则表示还在第n点附近，否则表示已经经过第n点，到达第n+1点
     * @return
     */
    private int getClosePointIndex(){
        while (mStepPoints.size() > (currentPointIndex + 1)){
            double currentDis = LatLngUtil.getDistance(startLatLng, mStepPoints.get(currentPointIndex).getLatLng());
            double nextDis = LatLngUtil.getDistance(startLatLng, mStepPoints.get(currentPointIndex + 1).getLatLng());
            if(currentDis < nextDis){
                break;
            }
            currentPointIndex++;
        }
        return currentPointIndex;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(orientationManager != null) {
            orientationManager.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(orientationManager != null) {
            orientationManager.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSearch != null){
            mSearch.destroy();
        }
    }

    /**
     * 获取手机旋转角度
     */
    @Override
    public void onOrientationChange(double degreeX, double degreeY, double degreeZ) {
        renderer.xrotate = (float) degreeX;
        renderer.zrotate = (float) degreeZ;
//        renderer.yrotate = (float) degreeY;
        glSurfaceView.requestRender();    //请求渲染，和脏渲染配合使用
        tv.invalidate();
        tv.setText("z：" + (int)degreeZ + "\nx：" + (int)degreeX + "\ny：" + (int)degreeY);
        int currentDirection = (int) degreeZ;
        if(currentDirection < 0){
            currentDirection = currentDirection + 360;
        }
        compassView.setCurrentDegree(currentDirection);
    }

    /**
     * 定位监听
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //设定中心点坐标
            startLatLng =  new LatLng(location.getLatitude(),location.getLongitude());
            Log.d(TAG, "Current location : " + startLatLng);
            if(!isLocated){
                //步行路线规划
                startRouteSearch();
                isLocated = true;
            }
            refreshSurfaceView();
        }
    }

    /**
     * 百度地图步行路线规划
     */
    private void startRouteSearch(){
        startLatLng = getIntent().getParcelableExtra(START_LOCATION);
        targetLatLng = getIntent().getParcelableExtra(TARGET_LOCATION);
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {

            public void onGetWalkingRouteResult(WalkingRouteResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(NavigationActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                List<WalkingRouteLine> routeLines = result.getRouteLines();
//                Log.d(TAG, "current LatLng : " + startLatLng);
//                if(routeLines != null) {
//                    for (int i = 0; i < routeLines.size(); i++) {
//                        Log.d(TAG, "第" + (i + 1) + "条路线>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//                        WalkingRouteLine line = routeLines.get(i);
//                        List<WalkingRouteLine.WalkingStep> steps = line.getAllStep();
//                        if (steps != null) {
//                            for (int j = 0; j < steps.size(); j++) {
//                                WalkingRouteLine.WalkingStep step = steps.get(j);
//                                RouteNode entrance = step.getEntrance();
//                                RouteNode exit = step.getExit();
//                                if (j == 0) {
//                                    Toast.makeText(NavigationActivity.this, " Direction : " + step.getDirection(), Toast.LENGTH_SHORT).show();
//                                }
//                                Log.d(TAG, "第" + (j + 1) + "步 : "
//                                        + " Name : " + step.getName()
//                                        + " Direction : " + step.getDirection()
//                                        + " Entrance : " + entrance.getLocation()
//                                        + " Instructions : " + step.getInstructions()
//                                        + " Exit : " + exit.getLocation()
//                                        + " EntranceInstruction : " + step.getEntranceInstructions()
//                                        + " ExitInstruction : " + step.getExitInstructions()
//                                        + " Distance : " + step.getDistance()
//                                        + " Duration : " + step.getDuration());
//                                List<LatLng> points = step.getWayPoints();
//                                if (points != null) {
//                                    for (LatLng latLng : points) {
//                                        Log.d(TAG, " point " + latLng);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }

                if(routeLines != null && routeLines.size() >= 1) {
                    WalkingRouteLine routeLine = routeLines.get(0);
                    List<WalkingRouteLine.WalkingStep> steps = routeLine.getAllStep();
                    if(steps != null){
                        for(WalkingRouteLine.WalkingStep step : steps){
                            List<LatLng> stepPoints = step.getWayPoints();
                            if(stepPoints != null){
                                for(LatLng latLng : stepPoints){
                                    WayPoint wayPoint = new WayPoint();
                                    wayPoint.setLatLng(latLng);
                                    wayPoint.setInstruction(step.getInstructions());
                                    mStepPoints.add(wayPoint);
                                }
                            }
                        }
                    }
                    routeSteps = steps;
                }
            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
            }
            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
            }
            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            }
            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
            }
            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
            }
        });
        PlanNode stNode = PlanNode.withLocation(startLatLng);
        PlanNode enNode = PlanNode.withLocation(targetLatLng);
        mSearch.walkingSearch((new WalkingRoutePlanOption())
                .from(stNode)
                .to(enNode));
    }
}
