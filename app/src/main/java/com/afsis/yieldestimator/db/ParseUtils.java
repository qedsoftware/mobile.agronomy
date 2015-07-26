package com.afsis.yieldestimator.db;


import android.util.Log;

import com.africasoils.gssid.GSSID;
import com.afsis.yieldestimator.crops.Maize;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.parse.ParseException;

public class ParseUtils implements DBAccessor {


    private static final String TAG = ParseUtils.class.getName();

    private String OBJ_MAIZE_YIELD = "MaizeYield";
    private String OBJ_SOIL_SAMPLE = "SoilSample";

    private String MAIZE_YIELD_ESTIMATE = "MaizeYieldEstimate";
    private String MAIZE_ROWS_PER_COB = "MaizeRowsPerCob";
    private String MAIZE_KERNELS_PER_ROW = "MaizeKernelsPerRow";
    private String MAIZE_COBS_PER_UNIT_AREA = "MaizecobsPerUnitArea";
    private String MAIZE_GROWTH_STAGE = "MaizeGrowthStage";
    private String LATITUDE = "Latitude";
    private String LONGITUDE = "Longitude";
    private String GEOPOINT = "GeoPoint";
    private String TIMESTAMP = "Timestamp";
    private String GSSID = "GSSID";
    private String SAMPLE_DEPTH = "SampleDepth";
    private String SAMPLE_EXTENSION = "SampleExtension";


    @Override
    public void saveMaizeYieldData(GSSID gssid, double latitude, double longitude, long timestamp,
                                   Maize maize, final ServerAccessorCallback cb)  {


        // TODO: if old, retrieve and update
        final ParseObject po = new ParseObject(OBJ_MAIZE_YIELD);
        po.put(GSSID, gssid.bytes());
        ParseGeoPoint parseGeoPoint = new ParseGeoPoint(latitude, longitude);
        po.put(GEOPOINT, parseGeoPoint);
        po.put(LATITUDE, latitude);
        po.put(LONGITUDE, longitude);
        po.put(TIMESTAMP, timestamp);
        po.put(MAIZE_COBS_PER_UNIT_AREA, maize.getCobsPerUnitArea());
        po.put(MAIZE_ROWS_PER_COB, maize.getRowsPerCob());
        po.put(MAIZE_KERNELS_PER_ROW, maize.getKernelsPerRow());
        po.put(MAIZE_GROWTH_STAGE, maize.getGrowthStage().toString());
        po.put(MAIZE_YIELD_ESTIMATE, maize.estimateYield());

        po.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                    Log.d(TAG, "Update: Maize yield saved successfully!");
                    String id = po.getObjectId();
                    Log.d(TAG, "The object id is: " + id);
                    cb.onSuccess();
                } else {
                    // The save failed.
                    Log.d(TAG, "Error: Maize yield update error: " + e);
                    cb.onFailure();
                }
            }
        });

    }

    @Override
    public void saveSoilSampleData(GSSID gssid,double latitude, double longitude, int sampleDepth,
                                   int sampleExtension, long timestamp, final ServerAccessorCallback cb) {
        // TODO: if old, retrieve and update
        final ParseObject po = new ParseObject(OBJ_SOIL_SAMPLE);
        po.put(GSSID, gssid.bytes());
        ParseGeoPoint parseGeoPoint = new ParseGeoPoint(latitude, longitude);
        po.put(GEOPOINT, parseGeoPoint);
        po.put(LATITUDE,latitude);
        po.put(LONGITUDE, longitude);
        po.put(TIMESTAMP, timestamp);
        po.put(SAMPLE_DEPTH, sampleDepth);
        po.put(SAMPLE_EXTENSION, sampleExtension);

        po.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                    Log.d(TAG, "Update: Soil sample saved successfully!");
                    String id = po.getObjectId();
                    Log.d(TAG, "The object id is: " + id);
                    cb.onSuccess();
                } else {
                    // The save failed.
                    Log.d(TAG, "Error: Soil sample update error: " + e);
                    cb.onFailure();
                }
            }
        });

    }
}

