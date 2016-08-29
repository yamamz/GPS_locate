package com.app.yamamz.gpslocate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.yamamz.gpslocate.utils.CoordinateConversion;
import com.app.yamamz.gpslocate.utils.DecimalToDMS;
import com.app.yamamz.gpslocate.utils.GPSService;
import com.app.yamamz.gpslocate.utils.LocationAddress;
import com.app.yamamz.gpslocate.utils.PRS92;
import com.bumptech.glide.Glide;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LOCATION = 2;

    private CoordinateConversion convertUtm = new CoordinateConversion();

    //private TMCoordConverter tmCoordConverter= new TMCoordConverter();

    private PRS92 prs92=new PRS92();

    @Bind(R.id.textViewLatitude)
    TextView editTextLatitude;
    @Bind(R.id.textViewLongLatLongUTM)
    TextView LatLongUTM;
    @Bind(R.id.textViewLatitudeDecimal)
    TextView LatDecimal;
    @Bind(R.id.textViewLongitude)
    TextView editTextLongitude;
    @Bind(R.id.textViewLongitudeDecimal)
    TextView LongDecimal;
    @Bind(R.id.place)
    TextView AddressTextview;
    @Bind(R.id.textViewElevation)
    TextView editTextElevation;
    @Bind(R.id.progressBar)
    ProgressBar spinner;
    @Bind(R.id.DMSlabel)
    TextView DMSlabel;
    @Bind(R.id.UTM)
    TextView UTMlabel;
    @Bind(R.id.DF)
    TextView DEClabel;
    @Bind(R.id.Elev)
    TextView Elevlabel;
    @Bind(R.id.ReverseGeo)
    TextView REVlabel;
    @Bind(R.id.AccuracyLavel)
    TextView Acurracylabel;
    @Bind(R.id.AcurracyText)
    TextView AcurracyText;
    private Timer myTimer;
    private double latitude;
    private double longitude;
    private double altitiude;
    private double accuracy;
    private static String locationAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle("Location");
        }

        spinner.setVisibility(View.GONE);


        setFontStyle();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Request missing location permission.
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE_LOCATION);

                    } else {

                        if (isGPSEnable()) {
                            myTimer = new Timer();
                            myTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    TimerMethod();
                                }

                            }, 0, 4000);
                        } else {

                            GPSService mGPSService = new GPSService(MainActivity.this);

                            mGPSService.askUserToOpenGPS();
                        }
                    }


                }
            });
        }

        fillFab();
        //load ramdom images from net
        loadBackdrop();


    }

    private boolean isGPSEnable() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            return true;
        } else {

            return false;
        }
    }

    private void TimerMethod() {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.
        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }


    private Runnable Timer_Tick = new Runnable() {
        public void run() {

            //This method runs in the same thread as the UI.
            //Do something to the UI thread here

            GPSService mGPSService = new GPSService(MainActivity.this);
            spinner.setVisibility(View.VISIBLE);
            mGPSService.getLocation();
            //get lat longitude altitude and accuracy
            latitude = mGPSService.getLatitude();
    longitude = mGPSService.getLongitude();
    altitiude = mGPSService.getAltitude();
    accuracy = mGPSService.getAcuraccy();
         //get actual address name from coordinates given
    LocationAddress.getAddressFromLocation(latitude, longitude, getApplicationContext(), new GeocoderHandler());
    //convert WGS84 to UTM


           // double Radianlat = latitude;
           // double RadianLong = longitude;

            // convert them in radians
        //   Radianlat = Math.toRadians(Radianlat);
         //   RadianLong = Math.toRadians(RadianLong);

            String UTM = convertUtm.latLon2UTM(latitude, longitude);






    //convert degress minutes seconds from WGS84
    DecimalToDMS ConverterDMS=new DecimalToDMS();
    String lat = ConverterDMS.decimalToDMS(latitude);
    String lon = ConverterDMS.decimalToDMS(longitude);
    //display progress loading
    if (mGPSService.getLatitude() == 0 && mGPSService.getLongitude() == 0) {
        spinner.setVisibility(View.VISIBLE);
    } else {
        spinner.setVisibility(View.GONE);
        //display result to Textviews
        LatDecimal.setText(String.format("%s%s%s", latitude, getString(R.string.space), longitude));
        String formatLat = lat.replaceAll(getString(R.string.characterToReplace), "");
        String formatLon = lon.replaceAll(getString(R.string.characterToReplace), "");
        editTextLatitude.setText(String.format("%s%s%s", formatLat, getString(R.string.space), formatLon));
        editTextElevation.setText(String.format("%s", altitiude));
        AcurracyText.setText(String.format("%s", accuracy));
        LatLongUTM.setText(UTM);

        AddressTextview.setText(locationAddress);
    }

}
};



        private static class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

            Intent k = new Intent(this, Activity_Geo_Converter.class);
            startActivity(k);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadBackdrop() {
        this.runOnUiThread(loadImage);
    }

    private Runnable loadImage = new Runnable() {

        @Override
        public void run() {
            final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
            Glide.with(MainActivity.this).load("https://unsplash.it/600/300/?random").centerCrop().into(imageView);
        }
    };

    private void fillFab() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        if (fab != null) {
            fab.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_my_location).actionBar().color(Color.WHITE));
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putDouble("lat", latitude);
        outState.putDouble("lon", longitude);
        outState.putDouble("ele", altitiude);
        outState.putDouble("acc", accuracy);
        outState.putString("add", locationAddress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        latitude = savedInstanceState.getDouble("lat");
        longitude = savedInstanceState.getDouble("lon");
        altitiude = savedInstanceState.getDouble("ele");
        accuracy = savedInstanceState.getDouble("acc");
        locationAddress = savedInstanceState.getString("add");


        setFontStyle();


        String UTM = convertUtm.latLon2UTM(latitude, longitude);
        DecimalToDMS ConverterDMS=new DecimalToDMS();
        String lat = ConverterDMS.decimalToDMS(latitude);
        String lon = ConverterDMS.decimalToDMS(longitude);
        //display result to Textviews
        LatDecimal.setText(String.format("%s%s%s", latitude, getString(R.string.space), longitude));
        String formatLat = lat.replaceAll(getString(R.string.characterToReplace), "");
        String formatLon = lon.replaceAll(getString(R.string.characterToReplace), "");
        editTextLatitude.setText(String.format("%s%s%s", formatLat, getString(R.string.space), formatLon));
        editTextElevation.setText(String.format("%s", altitiude));
        AcurracyText.setText(String.format("%s", accuracy));
        LatLongUTM.setText(UTM);

        if (isGPSEnable()) {
            myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    TimerMethod();
                }

            }, 0, 4000);
        }


    }

    public void setFontStyle() {
        Typeface roboto = Typeface.createFromAsset(getBaseContext().getAssets(),
                "font/RobotoCondensed-Regular.ttf");

        Typeface robotoBold = Typeface.createFromAsset(getBaseContext().getAssets(),
                "font/RobotoCondensed-Regular.ttf");
        DMSlabel.setTypeface(robotoBold);
        UTMlabel.setTypeface(robotoBold);
        DEClabel.setTypeface(robotoBold);
        Elevlabel.setTypeface(robotoBold);
        REVlabel.setTypeface(robotoBold);
        Acurracylabel.setTypeface(robotoBold);
        AcurracyText.setTypeface(roboto);
        editTextLatitude.setTypeface(roboto);
        editTextLongitude.setTypeface(roboto);
        editTextElevation.setTypeface(roboto);
        LatLongUTM.setTypeface(roboto);
        LatDecimal.setTypeface(roboto);
        LongDecimal.setTypeface(roboto);
        AddressTextview.setTypeface(roboto);

        DMSlabel.setText(R.string.DegreesMinutesSeconds);
        UTMlabel.setText(R.string.UTM);
        DEClabel.setText(R.string.DecimalFormat);
        Elevlabel.setText(R.string.Elevation);
        REVlabel.setText(R.string.Reverse_geo);
        Acurracylabel.setText(R.string.Accuracy);
    }


}
