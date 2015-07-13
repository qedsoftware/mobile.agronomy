package com.afsis.yieldestimator.views;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.africasoils.gssid.GSSID;
import com.afsis.yieldestimator.R;
import com.afsis.yieldestimator.crops.Maize;
import com.afsis.yieldestimator.crops.MaizeGrowthStage;
import com.afsis.yieldestimator.util.ErrorManager;
import com.afsis.yieldestimator.util.Notifier;
import com.parse.Parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static String MAIZE_DATA = "maizeData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();
        initParse();
    }

    private void initParse() {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "insert app id", "insert client key");
    }

    private void initLayout() {
        renderSpinner();
        setClickHandlers();
    }

    private void setClickHandlers() {
        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Extract user input
                EditText txtCobsPerUnitArea = (EditText) findViewById(R.id.txtCobsPerUnitArea);
                EditText txtRowsPerCob = (EditText) findViewById(R.id.txtRowsPerCob);
                EditText txtKernelsPerRow = (EditText) findViewById(R.id.txtKernelsPerRow);
                Spinner spinGrowthStage = (Spinner) findViewById(R.id.spinGrowthStage);
                Maize maize = new Maize();
                String cobs = txtCobsPerUnitArea.getText().toString().trim();
                String rows = txtRowsPerCob.getText().toString().trim();
                String kernels = txtKernelsPerRow.getText().toString().trim();
                MaizeGrowthStage stage = (MaizeGrowthStage) spinGrowthStage.getSelectedItem();
                if (cobs.isEmpty() || rows.isEmpty() || kernels.isEmpty() || stage == null) {
                    Notifier.showToastMessage(getApplicationContext(), ErrorManager.errFieldsEmpty);
                } else {
                    // Estimate the yield
                    maize.setCobsPerUnitArea(Integer.parseInt(cobs));
                    maize.setRowsPerCob(Integer.parseInt(rows));
                    maize.setKernelsPerRow(Integer.parseInt(kernels));
                    maize.setGrowthStage(stage);
                    renderResult(maize);
                }
            }
        });
    }

    private void renderResult(Maize maize) {
        // Switch activity
        Intent i = new Intent(getApplicationContext(), ResultsActivity.class);
        i.putExtra(MAIZE_DATA, maize);
        startActivity(i);
    }

    private void renderSpinner() {
        List<MaizeGrowthStage> lstSpinner = new ArrayList<>();
        lstSpinner.addAll(Arrays.asList(MaizeGrowthStage.values()));
        // Create spinner adapter
        ArrayAdapter<MaizeGrowthStage> adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, lstSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinGrowthStage);
        sItems.setAdapter(adapter);
        // TODO: default?
        // TODO: show drop down downwards
        // Set a default value
        sItems.setSelection(adapter.getPosition(MaizeGrowthStage.R1));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}



