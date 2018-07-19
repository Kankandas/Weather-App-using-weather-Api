package com.example.kankan.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lati,longti;
    private Location mLocation;
    private GpsFinder gpsFinder;
    private TextView txtCityName,txtTemp,txtHighestTemp,txtLowestTemp,txtSunny,txtCloud,txtPressure,txtVisibility,txtHumadity,txtSpeed,txtSunrise,txtSunset;
    public static final int LOCATION_REQUEST = 10020;
    private List<Weather> weatherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        weatherList=new ArrayList<>();

        txtCityName=findViewById(R.id.txtCityNameForMap);
        txtTemp=findViewById(R.id.txtTempForMap);
        txtHighestTemp=findViewById(R.id.txtHighestTempForMap);
        txtLowestTemp=findViewById(R.id.txtLowestTempForMap);
        txtSunny=findViewById(R.id.txtSunnyForMap);
        txtCloud=findViewById(R.id.txtCloudForMap);
        txtPressure=findViewById(R.id.txtPresssureForMap);
        txtVisibility=findViewById(R.id.txtVisibilityForMap);
        txtHumadity=findViewById(R.id.txtHumidityForMap);
        txtSpeed=findViewById(R.id.txtSpeedForMap);
        txtSunrise=findViewById(R.id.txtSunriseForMap);
        txtSunset=findViewById(R.id.txtSunsetForMap);



        gpsFinder=new GpsFinder(MapsActivity.this);
        mLocation=gpsFinder.getLocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sydney).title("I'm Here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                mMap.clear();

                double lati=latLng.latitude;
                double longti=latLng.longitude;

                getTheWeatherForThis(latLng);


                Toast.makeText(MapsActivity.this,"LAti:"+lati+"\nLongi:"+longti,Toast.LENGTH_LONG).show();

                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                markerOptions.title("Lati:"+lati+"\nLongti:"+longti);
                mMap.addMarker(markerOptions);
            }
        });
    }
    private void getTheWeatherForThis(LatLng latLng)
    {
        retrofit2.Call<AllWeather> weatherCall=FinalGet.getWeatherService().getTempUsingLatLng(latLng.latitude,latLng.longitude);

        weatherCall.enqueue(new Callback<AllWeather>() {
            @Override
            public void onResponse(Call<AllWeather> call, Response<AllWeather> response) {

                AllWeather allWeather=response.body();

                weatherList=allWeather.getWeather();

                Main main=allWeather.getMain();
                String temp=main.getTemp().toString();
                txtTemp.setText(temp+"°");
                txtHighestTemp.setText(main.getTempMax().toString()+"°");
                txtLowestTemp.setText(main.getTempMin().toString()+"°");

                if(weatherList.get(0).getDescription().toString().equals("clear sky"))
                {
                    txtCloud.setVisibility(View.INVISIBLE);
                    txtSunny.setVisibility(View.VISIBLE);
                    txtSunny.setText("  "+weatherList.get(0).getDescription().toString());
                }
                else
                {
                    txtSunny.setVisibility(View.INVISIBLE);
                    txtCloud.setVisibility(View.VISIBLE);

                    txtCloud.setText("  "+weatherList.get(0).getDescription().toString());
                }
                txtHumadity.setText(main.getHumidity()+" %");
                txtPressure.setText(main.getPressure()+" hPa");
                txtVisibility.setText("Can't find");
                txtSpeed.setText(allWeather.getWind().getSpeed()+" meter/sec");
                txtCityName.setText(allWeather.getName());
                long durationInMillis=allWeather.getSys().getSunrise();
                long millis = durationInMillis % 1000;
                long second = (durationInMillis / 1000) % 60;
                long minute = (durationInMillis / (1000 * 60)) % 60;
                long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

                if(hour>12)
                {
                    hour=hour-12;
                }

                txtSunrise.setText(hour+"."+minute+" AM");

                durationInMillis=allWeather.getSys().getSunset();
                minute=(durationInMillis/(1000*60)) %60;
                hour = (durationInMillis / (1000 * 60 * 60)) % 24;
                if(hour>12)
                {
                    hour=hour-12;

                }

                txtSunset.setText(hour+"."+minute+" PM");


            }

            @Override
            public void onFailure(Call<AllWeather> call, Throwable t) {

            }
        });
    }
}
