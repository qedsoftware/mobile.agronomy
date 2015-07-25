package com.afsis.yieldestimator.db;


import com.africasoils.gssid.GSSID;
import com.afsis.yieldestimator.crops.Maize;

public class ServerAccessor {

    DBAccessor dbAccessor = new ParseUtils();

    public void saveMaizeYieldData(GSSID gssid, double latitude, double longitude, long timestamp, Maize maize, ServerAccessorCallback cb) {
        dbAccessor.saveMaizeYieldData(gssid, latitude, longitude, timestamp, maize, cb);
    }

    public void saveSoilSampleData(GSSID gssid,double latitude, double longitude, int sampleDepth, int sampleExtension, long timestamp, ServerAccessorCallback cb) {
        dbAccessor.saveSoilSampleData(gssid,latitude,longitude, sampleDepth, sampleExtension, timestamp, cb);
    }
}
