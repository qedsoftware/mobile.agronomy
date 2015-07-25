package com.afsis.yieldestimator.db;


import com.africasoils.gssid.GSSID;
import com.afsis.yieldestimator.crops.Maize;

interface DBAccessor {

    void saveMaizeYieldData(GSSID gssid, double latitude, double longitude, long timestamp,
                                   Maize maize, ServerAccessorCallback cb);
    void saveSoilSampleData(GSSID gssid,double latitude, double longitude, int sampleDepth,
                                   int sampleExtension, long timestamp, ServerAccessorCallback cb);

}
