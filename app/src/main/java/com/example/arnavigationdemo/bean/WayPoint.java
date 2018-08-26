package com.example.arnavigationdemo.bean;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by ming on 2018/8/24.
 */

public class WayPoint {
    private LatLng latLng;
    private String instruction;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}
