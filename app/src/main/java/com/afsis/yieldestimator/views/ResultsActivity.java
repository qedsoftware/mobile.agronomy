package com.afsis.yieldestimator.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afsis.yieldestimator.R;
import com.afsis.yieldestimator.util.ErrorManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = ResultsActivity.class.getName();

    private static int AUTO_REFRESH_TIME = 1000;
    private double lat = 0;
    private double lon = 0;
    private long timestamp = 0;
    private float accuracy = 0;

    private boolean isAutoRefreshSelected = false;
    private boolean isLocationDataValid = false;

    private ProgressBar refreshProgress;
    private ToggleButton autoRefreshToggle;
    private TextView lblLatlon;
    private TextView lblAccuracy;
    private TextView lblTime;
    private RadioGroup radioGroupSoilSample;
    private Button btnSave;

    private MyLocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent i = getIntent();
        String yieldEstimate = i.getStringExtra(MainActivity.MAIZE_YIELD_ESTIMATE);
        TextView txtResult = (TextView) findViewById(R.id.txtYield);
        txtResult.setText(yieldEstimate);
        getViewsForInteraction();
        // Restore instance state from last run of activity
        restoreInstanceState(savedInstanceState);
    }

    private void getViewsForInteraction() {
        // Get the views we want to interact with.
        refreshProgress = (ProgressBar) findViewById(R.id.refreshProgress);
        autoRefreshToggle = (ToggleButton) findViewById(R.id.autoRefresh);
        lblLatlon = (TextView) findViewById(R.id.txtLatlon);
        lblAccuracy = (TextView) findViewById(R.id.txtAccuracy);
        lblTime = (TextView) findViewById(R.id.txtTime);
        radioGroupSoilSample = (RadioGroup) findViewById(R.id.radGrpSampleDepth);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setEnabled(isLocationDataValid);
        isAutoRefreshSelected = autoRefreshToggle.isChecked();
        locationManager = new MyLocationManager();
        locationListener = new MyLocationListener();

    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            lat = savedInstanceState.getDouble(getString(R.string.LAT));
            lon = savedInstanceState.getDouble(getString(R.string.LON));
            timestamp = savedInstanceState.getLong(getString(R.string.TIMESTAMP));
            accuracy = savedInstanceState.getFloat(getString(R.string.ACCURACY));
            isLocationDataValid = savedInstanceState.getBoolean(getString(R.string.VALIDDATA));
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save instance state for retrieval upon activity recreation
        savedInstanceState.putDouble(getString(R.string.LAT), lat);
        savedInstanceState.putDouble(getString(R.string.LON), lon);
        savedInstanceState.putLong(getString(R.string.TIMESTAMP), timestamp);
        savedInstanceState.putFloat(getString(R.string.ACCURACY), accuracy);
        savedInstanceState.putBoolean(getString(R.string.VALIDDATA), isLocationDataValid);
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
            autoRefreshToggle.setChecked(true);
        }
        // Start location updates when activity starts the first time
        // and or resumes after interruption
        if (isAutoRefreshSelected) {
            startRefreshSpin();
            locationManager.startLocationUpdates();
        }
    }

    private void handleGPSDisabled() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(ErrorManager.errGpsDisabled);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Navigate the user to location settings
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        AlertDialog alert = dialog.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            lblLatlon.setText(String.format("%01.5f, %01.5f", lat, lon));
            lblLatlon.setTypeface(null, Typeface.NORMAL);
            lblAccuracy.setText(String.format("%01.1f meters", accuracy));
            lblAccuracy.setTypeface(null, Typeface.NORMAL);
            Date date = new Date(timestamp);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            lblTime.setText(format.format(date));
            lblTime.setTypeface(null, Typeface.NORMAL);
        } else {
            lblLatlon.setText("n/a");
            lblAccuracy.setText("n/a");
            lblTime.setText("n/a");
        }
    }

    public void onSaveClicked(View view) {

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
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
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
