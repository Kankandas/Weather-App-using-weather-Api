package com.example.kankan.weather;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    private TextView txtCreater,txtAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        txtAppName=findViewById(R.id.txtAppname);
        txtCreater=findViewById(R.id.txtCreater);

        Typeface font1=Typeface.createFromAsset(getAssets(),"actionis.ttf");
        Typeface font2=Typeface.createFromAsset(getAssets(),"Beyond Wonderland.ttf");

        txtAppName.setTypeface(font1);
        txtCreater.setTypeface(font2);

        Handler handler=new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        },4000);


    }
}
