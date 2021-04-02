package com.example.sanjagps2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;

Switch sw_gps,sw_locationupdates;
TextView tv_lat,tv_lon,tv_altitude,tv_accuracy,tv_speed,tv_sensor,tv_updates,tv_adress,tv_wayPointCount;
Button btn_newWayPoint,btn_showWayPointList;

//current location
    Location currenLocation;

    //Saved locations
List<Location> savedLocations;


public static final int PERMISION_FINE_LOCATION=99;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sw_gps=findViewById(R.id.sw_gps);
        sw_locationupdates=findViewById(R.id.sw_locationsupdates);
        tv_lat=findViewById(R.id.tv_lat);
        tv_lon=findViewById(R.id.tv_lon);
        tv_altitude=findViewById(R.id.tv_altitude);
        tv_accuracy=findViewById(R.id.tv_accuracy);
        tv_speed=findViewById(R.id.tv_speed);
        tv_sensor=findViewById(R.id.tv_sensor);
        tv_updates=findViewById(R.id.tv_updates);
        tv_adress=findViewById(R.id.tv_address);
        tv_wayPointCount=findViewById(R.id.tv_wayPointCount);

        btn_newWayPoint=findViewById(R.id.btn_newWayPoint);
        btn_showWayPointList=findViewById(R.id.btn_showWayPointList);

//btn_update=findViewById(R.id.btn_update);
        locationRequest=new LocationRequest();
        locationRequest.setInterval(30000);//Moze i manje
        locationRequest.setFastestInterval(5000);//Moze i manje
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallBack=new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location=locationResult.getLastLocation();
                updateUIValues(location);
            };
        };

        btn_showWayPointList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,ShowSavedLocationsList.class);
                startActivity(i);
            }
        });

        btn_newWayPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get gps location

                //add new location to global location;

                MyAplication myAplication=(MyAplication)getApplicationContext();
                savedLocations=myAplication.getMyLocations();
                if(currenLocation!=null)
                savedLocations.add(currenLocation);
                updateWayPointCounter();
            }
        });

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()){
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    updateGPS();
                }else{
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    updateGPS();
                }
            }
        });

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sw_locationupdates.isChecked()){
                    startLocationUpdates();
                    tv_updates.setText("On");
                }else{
                    stopLocationUpdates();
                    tv_updates.setText("Off");

                }
            }
        });
        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISION_FINE_LOCATION:
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                updateGPS();
            }else{
                Toast.makeText(this,"Permisions needed",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void updateGPS(){
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            //user provided permision
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //updateUIValues(location);

                    if(location!=null)
                    {
                        updateUIValues(location);
                        currenLocation=location;
                    }
                    else{tv_lat.setText("Nema lokacije");}


                }
            });
        }
        else{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISION_FINE_LOCATION);
            }
        }
    }
    private void updateUIValues(Location location){
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if(location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }else{
            tv_altitude.setText("Not available");
        }
        if(location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }else{
            tv_speed.setText("Not available");
        }
        Geocoder geocoder=new Geocoder(MainActivity.this);

        try{
            List<Address> adresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            tv_adress.setText(adresses.get(0).getAddressLine(0));// Moze i ime zemlje ima dosta opcija
        }
        catch(Exception e) {
            tv_adress.setText("No adress");
        }

        updateWayPointCounter();

    }

    private void updateWayPointCounter(){
        MyAplication myAplication=(MyAplication)getApplicationContext();
        savedLocations=myAplication.getMyLocations();
        tv_wayPointCount.setText(Integer.toString(savedLocations.size()));
    }

    private void stopLocationUpdates(){
        setTextOff();
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }
    private void startLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallBack,null);
        updateGPS();

    }
    private void setTextOff(){
        String message="Not tracking";
        tv_adress.setText(message);
        tv_sensor.setText(message);
        tv_accuracy.setText(message);
        tv_lon.setText(message);
        tv_lat.setText(message);
        tv_altitude.setText(message);
        tv_speed.setText(message);

    }
}