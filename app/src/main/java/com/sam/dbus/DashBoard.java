package com.sam.dbus;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class DashBoard extends Activity {
    ImageButton busroute, btndriverinfo, trybtn;
    TextView route, ppoints, driverinfo;
    public static int choice;
    SharedPreferences sharedpreferences;
    int a=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        SharedPreferences.Editor editor = getSharedPreferences("LAN",MODE_PRIVATE).edit();
        editor.putString("WEB", getString(R.string.webservice1));
        editor.commit();
        Typeface thin = Typeface.createFromAsset(getAssets(), "font/Roboto-Thin.ttf");
        busroute = (ImageButton) findViewById(R.id.db_imagebtn_busroute);
        btndriverinfo = (ImageButton) findViewById(R.id.db_imagebtn_driverinfo);
        trybtn = (ImageButton) findViewById(R.id.try_imgbtn);
        route = (TextView) findViewById(R.id.db_tv_busroute);
        ppoints = (TextView) findViewById(R.id.db_tv_ppoints);
        driverinfo = (TextView) findViewById(R.id.db_tv_driverinfo);

        route.setTypeface(thin);
        ppoints.setTypeface(thin);
        driverinfo.setTypeface(thin);

        Bundle b = getIntent().getExtras();
        choice = b.getInt("choice");


        busroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent br_busroute = new Intent(DashBoard.this, BusRoute.class);
                Bundle b = new Bundle();
                b.putInt("choice",choice);
                br_busroute.putExtras(b);
                startActivity(br_busroute);
            }
        });
        trybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pp_pickuppoints = new Intent(DashBoard.this, PickUpPoints.class);
                Bundle b = new Bundle();
                b.putInt("choice",choice);
                pp_pickuppoints.putExtras(b);
                startActivity(pp_pickuppoints);
            }
        });
        btndriverinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent br_driverlist = new Intent(DashBoard.this, DriverList.class);
                Bundle b = new Bundle();
                b.putInt("choice",choice);
                br_driverlist.putExtras(b);
                startActivity(br_driverlist);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if(id ==R.id.action_settings)
        {
            if(a==0)
            {
                SharedPreferences.Editor editor = getSharedPreferences("LAN",MODE_PRIVATE).edit();
                editor.putString("WEB", getString(R.string.webservice1));
                editor.commit();
                Toast.makeText(getApplicationContext(),"Local",Toast.LENGTH_SHORT).show();
                a=1;
            }
          else if(a==1)
            {
                SharedPreferences.Editor editor = getSharedPreferences("LAN",MODE_PRIVATE).edit();
                editor.putString("WEB", getString(R.string.webservice));
                editor.commit();
                Toast.makeText(getApplicationContext(),"Online",Toast.LENGTH_SHORT).show();
                a=0;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


