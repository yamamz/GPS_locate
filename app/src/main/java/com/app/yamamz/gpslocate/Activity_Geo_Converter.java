package com.app.yamamz.gpslocate;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.yamamz.gpslocate.utils.CoordinateConversion;
import com.app.yamamz.gpslocate.utils.DecimalToDMS;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Activity_Geo_Converter extends AppCompatActivity {
    private CoordinateConversion convertUtm = new CoordinateConversion();
    @Bind(R.id.textViewLatitude)
    TextView editTextLatitude;
    @Bind(R.id.textViewLongLatLongUTM)
    TextView LatLongUTM;



    @Bind(R.id.DMSlabel)
    TextView DMSlabel;
    @Bind(R.id.UTM)
    TextView UTMlabel;



    @Bind(R.id.ETlatitude)
    EditText ETlatitude;
    @Bind(R.id.ETlongitude)
    EditText ETlongitude;

    private double latitude;
    private double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity__geo__converter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFontStyle();
                latitude =Double.parseDouble(ETlatitude.getText().toString());
                longitude =Double.parseDouble(ETlongitude.getText().toString());


                if(ETlongitude.getText().toString().equals("") || ETlatitude.getText().toString().equals("")){


                    Toast.makeText(Activity_Geo_Converter.this,"textbox must have value",Toast.LENGTH_LONG).show();

                }

                else {


                    //convert WGS84 to UTM
                    String UTM = convertUtm.latLon2UTM(latitude, longitude);
                    //convert degress minutes seconds from WGS84
                    DecimalToDMS ConverterDMS = new DecimalToDMS();
                    String lat = ConverterDMS.decimalToDMS(latitude);
                    String lon = ConverterDMS.decimalToDMS(longitude);
                    //display progress loading


                    //display result to Textviews

                    String formatLat = lat.replaceAll(getString(R.string.characterToReplace), "");
                    String formatLon = lon.replaceAll(getString(R.string.characterToReplace), "");
                    editTextLatitude.setText(String.format("%s%s%s", formatLat, getString(R.string.space), formatLon));

                    LatLongUTM.setText(UTM);

                }

                }



        });
    }

    public void setFontStyle() {
        Typeface roboto = Typeface.createFromAsset(getBaseContext().getAssets(),
                "font/RobotoCondensed-Regular.ttf");

        Typeface robotoBold = Typeface.createFromAsset(getBaseContext().getAssets(),
                "font/RobotoCondensed-Regular.ttf");
        DMSlabel.setTypeface(robotoBold);
        UTMlabel.setTypeface(robotoBold);


        DMSlabel.setTypeface(roboto);
        UTMlabel.setTypeface(roboto);





        LatLongUTM.setTypeface(roboto);



        DMSlabel.setText(R.string.DegreesMinutesSeconds);
        UTMlabel.setText(R.string.UTM);



    }

}
