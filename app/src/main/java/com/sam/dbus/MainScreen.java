package com.sam.dbus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;


public class MainScreen extends Activity{
    Button msdegree;
    Button msdiploma;
    public static int choice = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Typeface light = Typeface.createFromAsset(getAssets(),"font/Roboto-Light.ttf");
        msdegree = (Button) findViewById(R.id.ms_btn_degree);
        msdiploma= (Button) findViewById(R.id.ms_btn_diploma);
        msdegree.setTypeface(light);
        msdiploma.setTypeface(light);
        msdegree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 1;
                Intent msd = new Intent(MainScreen.this, DashBoard.class);
                Bundle b = new Bundle();
                b.putInt("choice", 1);
                msd.putExtras(b);
                startActivity(msd);

            }
        });
        msdiploma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 0;
                Intent msdd = new Intent(MainScreen.this, DashBoard.class);
                Bundle b = new Bundle();
                b.putInt("choice", 0);
                msdd.putExtras(b);
                startActivity(msdd);

            }
        });

    }
}
