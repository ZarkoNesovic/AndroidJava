package com.example.sanjagps2;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class MyAplication extends Application {
    private static MyAplication singleton;

    private List<Location>myLocations;

    public List<Location> getMyLocations() {
        return myLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }

    public MyAplication getInstance(){
        return singleton;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        singleton=this;
        myLocations=new ArrayList<>();
    }
}
