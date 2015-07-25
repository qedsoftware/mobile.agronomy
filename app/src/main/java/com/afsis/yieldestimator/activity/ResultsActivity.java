package com.afsis.yieldestimator.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.africasoils.gssid.GSSID;
import com.afsis.yieldestimator.R;
import com.afsis.yieldestimator.crops.Maize;
import com.afsis.yieldestimator.db.ServerAccessor;
import com.afsis.yieldestimator.db.ServerAccessorCallback;
import com.afsis.yieldestimator.util.Notifier;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ResultsActivity extends SuperActivity {

    private static final String TAG = ResultsActivity.class.getName();

    private static int AUTO_REFRESH_TIME = 1000;
    private double lat = 0;
    private double lon = 0;
    private long timestamp = 0;
    private float accuracy = 0;

    private boolean isAutoRefreshSelected = false;
    private boolean isLocationDataValid = false;

    private ProgressBar refreshProgress;
    private ToggleButton btnToggleAutoRefresh;
    private TextView lblLatLon;
    private TextView lblAccuracy;
    private TextView lblTime;
    private RadioGroup radioGroupSoilSample;
    private Button btnSave;

    private MyLocationManager locationManager;
    private LocationListener locationListener;
    private Maize maize;

    private ServerAccessor serverAccessor = new ServerAccessor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Retrieve extras, maize yield object from previous acitivy
        Intent i = getIntent();
        maize = (Maize) i.getExtras().getSerializable(MainActivity.MAIZE_DATA);
        double yieldEstimate = maize.estimateYield();
        TextView txtResult = (TextView) findViewById(R.id.txtYield);
        txtResult.setText(String.valueOf(yieldEstimate));

        // Get handles to views from UI
        getViewsForInteraction();

        // Restore instance state from last run of activity
        restoreInstanceState(savedInstanceState);
    }

    private void getViewsForInteraction() {
        refreshProgress = (ProgressBar) findViewById(R.id.refreshProgress);
        btnToggleAutoRefresh = (ToggleButton) findViewById(R.id.autoRefresh);
        lblLatLon = (TextView) findViewById(R.id.txtLatlon);
        lblAccuracy = (TextView) findViewById(R.id.txtAccuracy);
        lblTime = (TextView) findViewById(R.id.txtTime);
        radioGroupSoilSample = (RadioGroup) findViewById(R.id.radGrpSampleDepth);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setEnabled(isLocationDataValid);
        isAutoRefreshSelected = btnToggleAutoRefresh.isChecked();

        locationManager = new MyLocationManager();
        locationListener = new MyLocationListener();

    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            lat = savedInstanceState.getDouble(getString(R.string.lat));
            lon = savedInstanceState.getDouble(getString(R.string.lon));
            timestamp = savedInstanceState.getLong(getString(R.string.timestamp));
            accuracy = savedInstanceState.getFloat(getString(R.string.accuracy));
            isLocationDataValid = savedInstanceState.getBoolean(getString(R.string.valid_data));
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save instance state for retrieval upon activity recreation
        savedInstanceState.putDouble(getString(R.string.lat), lat);
        savedInstanceState.putDouble(getString(R.string.lon), lon);
        savedInstanceState.putLong(getString(R.string.timestamp), timestamp);
        savedInstanceState.putFloat(getString(R.string.accuracy), accuracy);
        savedInstanceState.putBoolean(getString(R.string.valid_data), isLocationDataValid);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();

        locationManager.cancelLocationUpdates();
        stopRefreshSpin();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!locationManager.isGPSEnabled()) {
            handleGPSDisabled();
            return;
        }

        updateTextViews();

        if (!isLocationDataValid) {
            isAutoRefreshSelected = true;
            btnToggleAutoRefresh.setChecked(true);
        }
        // Start location updates when activity starts the first time
        // and or resumes after interruption
        if (isAutoRefreshSelected) {
            startRefreshSpin();
            locationManager.startLocationUpdates();
        }
    }

    private void handleGPSDisabled() {
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setMessage(getString(R.string.err_gps_disabled))
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Navigate the user to location settings
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            })
            .create();
        dialog.show();
    }

    public void onAutoRefreshClicked(View view) {
        isAutoRefreshSelected = ((ToggleButton) view).isChecked();
        // Turn ON / OFF location updates
        if (isAutoRefreshSelected) {
            startRefreshSpin();
            locationManager.startLocationUpdates();
        } else {
            locationManager.cancelLocationUpdates();
            stopRefreshSpin();
        }
    }

    private void startRefreshSpin() {
        refreshProgress.setVisibility(View.VISIBLE);
    }

    private void stopRefreshSpin() {
        refreshProgress.setVisibility(View.INVISIBLE);
    }

    private void updateTextViews() {
        if (isLocationDataValid) {
            lblLatLon.setText(String.format("%01.5f, %01.5f", lat, lon));
            lblLatLon.setTypeface(null, Typeface.NORMAL);
            lblAccuracy.setText(String.format("%01.1f meters", accuracy));
            lblAccuracy.setTypeface(null, Typeface.NORMAL);
            Date date = new Date(timestamp);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            lblTime.setText(format.format(date));
            lblTime.setTypeface(null, Typeface.NORMAL);
        } else {
            lblLatLon.setText(R.string.n_a);
            lblAccuracy.setText(R.string.n_a);
            lblTime.setText(R.string.n_a);
        }
    }

    public void onSaveClicked(View view) {
        int sampleDepth = 0;
        int sampleExtension = 0;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (radioGroupSoilSample.getCheckedRadioButtonId() == R.id.topSoil) {
            sampleDepth = Integer.valueOf(sharedPref.getString(SettingsActivity.TOP_SOIL_DEPTH, "0"));
            sampleExtension =
                    Integer.valueOf(sharedPref.getString(SettingsActivity.TOP_SOIL_EXTENSION, "25"));
        } else if (radioGroupSoilSample.getCheckedRadioButtonId() == R.id.subSoil) {
            sampleDepth = Integer.valueOf(sharedPref.getString(SettingsActivity.SUB_SOIL_DEPTH, "25"));
            sampleExtension =
                    Integer.valueOf(sharedPref.getString(SettingsActivity.SUB_SOIL_EXTENSION, "25"));
        }
        GSSID gssid = new GSSID(lat, lon, timestamp, sampleDepth, sampleExtension);
        saveMaizeYieldEstimate(gssid, maize);
        saveSoilSampleData(gssid,lat,lon,timestamp,sampleDepth, sampleExtension);
        // TODO: Show GSSID on new screen? Show Tag History?
    }

    private void saveSoilSampleData(GSSID gssid,double latitude, double longitude, long timestamp, int sampleDepth, int sampleExtension) {
        serverAccessor.saveSoilSampleData(gssid, latitude, longitude, sampleDepth, sampleExtension, timestamp, new ServerAccessorCallback() {
            @Override
            public void onSuccess() {
                Notifier.showToastMessage(getApplicationContext(), getString(R.string.soil_update_success));
            }

            @Override
            public void onFailure() {
                Notifier.showToastMessage(getApplicationContext(), getString(R.string.err_soil_sample_update_failed));
            }
        });
    }

    private void saveMaizeYieldEstimate(GSSID gssid, Maize maize) {
        serverAccessor.saveMaizeYieldData(gssid, lat, lon, timestamp, maize, new ServerAccessorCallback() {
            @Override
            public void onSuccess() {
                Notifier.showToastMessage(ResultsActivity.this, getString(R.string.yield_update_success));
            }

            @Override
            public void onFailure() {
                Notifier.showToastMessage(ResultsActivity.this, getString(R.string.err_yield_update_failed));
            }
        });
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            Log.d(TAG, String.format("got location update: %01.5f, %01.5f", lat, lon));
            accuracy = location.getAccuracy();
            timestamp = System.currentTimeMillis();
            isLocationDataValid = true;
            btnSave.setEnabled(true);
            // Update views when location update received
            updateTextViews();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    }

    private class MyLocationManager {

        private LocationManager locationManager;

        public MyLocationManager(){
            this.locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        }

        private void startLocationUpdates() {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, AUTO_REFRESH_TIME, 0,
                    locationListener);
        }

        private void cancelLocationUpdates() {
            locationManager.removeUpdates(locationListener);
        }

        private boolean isGPSEnabled() {
            try {
                return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                return false;
            }
        }
    }
}
