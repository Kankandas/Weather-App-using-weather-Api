package com.example.kankan.weather;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class FinalGet {

    public static final String API_KEY=/*Enter your Api Key*/;
    public static final String URL="http://api.openweathermap.org/data/2.5/";


    public static WeatherService weatherService=null;

    public static WeatherService getWeatherService()
    {
        if(weatherService==null)
        {
            Retrofit retrofit=new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).build();

            weatherService=retrofit.create(WeatherService.class);
        }

        return weatherService;

    }


    public  interface WeatherService
    {
        @GET("weather?q=barddhaman&appid="+API_KEY+"&units=metric")
        Call<AllWeather> getAllWeatherDetails();
        @GET("weather?&&appid="+API_KEY+"&units=metric")
        Call<AllWeather> getTempUsingLatLng(@Query("lat")Double lat, @Query("lon")Double lng );

    }


}
