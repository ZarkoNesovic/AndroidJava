package com.example.sanjagps2;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ShowSavedLocationsList extends AppCompatActivity {

    ListView lv_wayPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_locations_list);

        lv_wayPoints=findViewById(R.id.lv_wayPoints);



        MyAplication myAplication=(MyAplication)getApplicationContext();
        List<Location> savedLocations=myAplication.getMyLocations();

        lv_wayPoints.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_expandable_list_item_1,savedLocations));
    }
}