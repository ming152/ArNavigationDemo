package com.example.arnavigationdemo.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.arnavigationdemo.BaseActivity;
import com.example.arnavigationdemo.R;
import com.example.arnavigationdemo.ui.adpter.PoiSearchAdapter;
import com.example.arnavigationdemo.util.LocationManger;
import com.example.arnavigationdemo.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements TextWatcher {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private EditText mPlaceSearchView;
    private RecyclerView mPoiRecyclerView;
    private PoiSearchAdapter poiSearchAdapter;
    private LocationManger locationManger;
    private MyLocationListener myListener = new MyLocationListener();
    private String city;
    private LatLng currentlatLng;
    private List<PoiInfo>  poiInfos;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mPlaceSearchView = findViewById(R.id.place_search);
        mPlaceSearchView.addTextChangedListener(this);
        mPoiRecyclerView = findViewById(R.id.rv_poi_hint);
        mPoiRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        poiSearchAdapter = new PoiSearchAdapter(this);
        poiSearchAdapter.setOnItemClickListener(new PoiSearchAdapter.OnPoiClickListener() {
            @Override
            public void onClick(int position, LatLng location) {
                mPoiRecyclerView.setVisibility(View.GONE);
                NavigationActivity.startActivity(MainActivity.this, currentlatLng, location);
            }
        });
        mPoiRecyclerView.setAdapter(poiSearchAdapter);

        //动态权限获取
        List<String> permissions = PermissionUtil.checkPermisson(this, PERMISSIONS);
        if(permissions == null || permissions.isEmpty()){
            initLocation();
        }else {
            PermissionUtil.requestPermissions(this, PERMISSION_REQUEST_CODE, permissions.toArray(new String[permissions.size()]));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    //存放没授权的权限
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        //说明都授权了
                        initLocation();
                    } else {
                        MainActivity.this.finish();
                    }
                }
                break;
            default:
                break;
        }
    }
    /**
     * 定位当前位置
     */
    private void initLocation(){
        locationManger = new LocationManger();
        locationManger.setLocationListener(myListener);
        locationManger.start();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0 || "".equals(s.toString())) {
            mPoiRecyclerView.setVisibility(View.GONE);
        } else {
            mPoiRecyclerView.setVisibility(View.VISIBLE);

            // 创建PoiSearch实例
            PoiSearch poiSearch = PoiSearch.newInstance();
            // 城市内检索
            PoiCitySearchOption poiCitySearchOption = new PoiCitySearchOption();
            // 关键字
            poiCitySearchOption.keyword(s.toString());
            // 城市
            poiCitySearchOption.city(city);
            // 设置每页容量，默认为每页10条
            poiCitySearchOption.pageCapacity(30);
            // 分页编号
            poiCitySearchOption.pageNum(0);
            // 设置poi检索监听者
            poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
                // poi 查询结果回调
                @Override
                public void onGetPoiResult(PoiResult poiResult) {
                    poiInfos = poiResult.getAllPoi();
                    if (poiInfos != null) {
                        poiSearchAdapter.setDataList(poiInfos);
                        poiSearchAdapter.notifyDataSetChanged();
                    }
                }

                // poi 详情查询结果回调
                @Override
                public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                }

                @Override
                public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

                }
            });
            poiSearch.searchInCity(poiCitySearchOption);

        }
    }

    /**
     * 获取当前位置信息并显示在地图上
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);

            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            //设定中心点坐标
            currentlatLng =  new LatLng(location.getLatitude(),location.getLongitude());
            //定义地图状态
            MapStatus mMapStatus = new MapStatus.Builder()
                    //要移动的点
                    .target(currentlatLng)
                    //放大地图到20倍
                    .zoom(15)
                    .build();

            //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

            //改变地图状态
            mBaiduMap.setMapStatus(mMapStatusUpdate);

            city = location.getCity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
