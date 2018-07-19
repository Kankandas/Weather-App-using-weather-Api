package com.example.kankan.weather;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private List<Weather> weatherList;
    private TextView txtTemp,txtHighestTemp,txtLowestTemp,txtCloud,txtPresssure,txtVisibility,txtHumidity,txtSpeed,txtSunrise,txtSunset,txtSunny,txtCity,
                     txtLatitute,txtLongtitute;
    private GpsFinder gpsFinder;
    private Location location;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheTool();

        weatherList=new ArrayList<>();

        txtTemp=findViewById(R.id.txtTemp);
        txtHighestTemp=findViewById(R.id.txtHighestTemp);
        txtLowestTemp=findViewById(R.id.txtLowestTemp);
        txtCloud=findViewById(R.id.txtCloud);
        txtPresssure=findViewById(R.id.txtPresssure);
        txtVisibility=findViewById(R.id.txtVisibility);
        txtHumidity=findViewById(R.id.txtHumidity);
        txtSpeed=findViewById(R.id.txtSpeed);
        txtSunrise=findViewById(R.id.txtSunrise);
        txtSunset=findViewById(R.id.txtSunset);
        txtSunny=findViewById(R.id.txtSunny);
        txtCity=findViewById(R.id.txtCityName);
        txtLongtitute=findViewById(R.id.longtitute);
        txtLatitute=findViewById(R.id.txtLatitute);

        gpsFinder=new GpsFinder(MainActivity.this);
        location=gpsFinder.getLocation();

        swipeRefreshLayout=findViewById(R.id.swipe);

        navigationView=findViewById(R.id.nevigationMenu);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.menu_find:
                        startActivity(new Intent(MainActivity.this,MapsActivity.class));
                        break;

                    case R.id.menu_about_us:
                        Dialog dialog=new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.about_me);
                        dialog.show();
                        break;

                }
                return false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTheWeather();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        gpsFinder=new GpsFinder(MainActivity.this);
        getTheWeather();

    }

    private void getTheWeather() {
        swipeRefreshLayout.setRefreshing(true);
        final retrofit2.Call<AllWeather> weatherCall=FinalGet.getWeatherService().getTempUsingLatLng(location.getLatitude(),location.getLongitude());
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
                txtHumidity.setText(main.getHumidity()+" %");
                txtPresssure.setText(main.getPressure()+" hPa");
                txtVisibility.setText("Can't find");
                txtSpeed.setText(allWeather.getWind().getSpeed()+" meter/sec");
                txtCity.setText(allWeather.getName());

                txtLatitute.setText(allWeather.getCoord().getLat()+"");
                txtLongtitute.setText(allWeather.getCoord().getLon()+"");


                long durationInMillis=allWeather.getSys().getSunrise();
                long millis = durationInMillis % 1000;
                long second = (durationInMillis / 1000) % 60;
                long minute = (durationInMillis / (1000 * 60)) % 60;
                long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

                if(hour>12)
                {
                    hour=hour-12;
                }

                txtSunrise.setText(hour+"."+minute);

                durationInMillis=allWeather.getSys().getSunset();
                minute=(durationInMillis/(1000*60)) %60;
                hour = (durationInMillis / (1000 * 60 * 60)) % 24;
                if(hour>12)
                {
                    hour=hour-12;

                }

                txtSunset.setText(hour+"."+minute);

                swipeRefreshLayout.setRefreshing(false);


            }

            @Override
            public void onFailure(Call<AllWeather> call, Throwable t) {

            }
        });
    }


    private void setTheTool() {


        drawerLayout=findViewById(R.id.drawerLayout);
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        toggle=new ActionBarDrawerToggle(MainActivity.this,drawerLayout,toolbar,R.string.Open,R.string.Close);

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

    }
}
