package com.example.kankan.weather;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import static android.content.Context.LOCATION_SERVICE;

public class GpsFinder extends Service implements LocationListener {

    private final Context context;
    boolean isGpsEnable= false;
    boolean isNetworkEnable= false;
    boolean canGetLocation=false;


    Location location;

    protected LocationManager locationManager;


    public GpsFinder(Context context)
    {
        this.context=context;
    }

    //create a GetLocation Method


    public Location getLocation() {

        try {
            locationManager=(LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGpsEnable=locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
            isNetworkEnable=locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    ||ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if (isGpsEnable)
                {
                    if (location==null)
                    {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,10,this);
                        if(locationManager !=null)
                        {
                            location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        }
                    }
                }
                //if location is not found from GPS then it will found from network
                if(location==null)
                {
                    if (isNetworkEnable)
                    {

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,10,this);
                        if(locationManager !=null)
                        {
                            location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        }

                    }
                }
            }


        }catch (Exception e){

        }
        return location;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

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
