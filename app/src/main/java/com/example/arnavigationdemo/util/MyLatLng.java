package com.example.arnavigationdemo.util;

/**
 * Created by ming on 2018/8/22.
 */

public class MyLatLng {
    final static double Rc=6378137;
    final static double Rj=6356725;
    double m_LoDeg,m_LoMin,m_LoSec;
    double m_LaDeg,m_LaMin,m_LaSec;
    double m_Longitude,m_Latitude;
    double m_RadLo,m_RadLa;
    double Ec;
    double Ed;
    public MyLatLng(double latitude,double longitude){
        m_LoDeg=(int)longitude;
        m_LoMin=(int)((longitude-m_LoDeg)*60);
        m_LoSec=(longitude-m_LoDeg-m_LoMin/60.)*3600;

        m_LaDeg=(int)latitude;
        m_LaMin=(int)((latitude-m_LaDeg)*60);
        m_LaSec=(latitude-m_LaDeg-m_LaMin/60.)*3600;

        m_Longitude=longitude;
        m_Latitude=latitude;
        m_RadLo=longitude*Math.PI/180.;
        m_RadLa=latitude*Math.PI/180.;
        Ec=Rj+(Rc-Rj)*(90.-m_Latitude)/90.;
        Ed=Ec*Math.cos(m_RadLa);
    }
}
