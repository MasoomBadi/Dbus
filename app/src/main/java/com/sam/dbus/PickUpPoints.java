package com.sam.dbus;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PickUpPoints extends ListActivity implements OnItemSelectedListener {

    TextView ppoints_city, ppoints_ppoint;

    Spinner ppoints_spin_city, ppoints_spin_ppoint;

    private ArrayList<City> pcityList;
    private ArrayList<ReturnPickupPoints> PickupARRLIST;

    ProgressDialog pDialog;


    String ppselcity=null;
    String selectedppoint = null;


    JSONParser pickupparser = new JSONParser();
    JSONParser cityparser = new JSONParser();
    JSONParser selectedpointparser = new JSONParser();

    ArrayList<HashMap<String, String>> startingpointList;

    private String URL;
    private String LOADPICKUPPOINTS;
    private String SELECTEDPPOINTS = "http://192.168.23.1/dbus/db_getbusbypickup.php";



    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PICKUPARRAY = "pickupresult";
    private static final String TAG_PPNAME = "Name";
    private static final String TAG_SELPOINTSARR = "pickupresult";
    private static final String TAG_RETURNBUSNO = "BusNo";
    private static final String TAG_RETURNSP = "Name";
    public static int choice;
    JSONArray pickuppointarr;
    JSONArray startingpoints = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_points);
        SharedPreferences prefs = getSharedPreferences("LAN", MODE_PRIVATE);

        String lanSettings = prefs.getString("WEB", "");
        URL = lanSettings +"/dbus/dbus.php";
        LOADPICKUPPOINTS  = lanSettings +"/dbus/db_getpickupbycity.php";
        SELECTEDPPOINTS = getString(R.string.webservice1) +"/dbus/db_getbusbypickup.php";


        ppoints_city = (TextView) findViewById(R.id.ppoint_tv_city);
        ppoints_ppoint = (TextView) findViewById(R.id.ppoint_tv_ppoints);

        ppoints_spin_city = (Spinner) findViewById(R.id.ppoint_spinner_city);
        ppoints_spin_ppoint = (Spinner) findViewById(R.id.ppoint_spinner_ppoints);

        Typeface thin = Typeface.createFromAsset(getAssets(), "font/Roboto-Thin.ttf");

        ppoints_city.setTypeface(thin);
        ppoints_ppoint.setTypeface(thin);

        pcityList = new ArrayList<City>();
        PickupARRLIST = new ArrayList<ReturnPickupPoints>();

        ppoints_spin_city.setOnItemSelectedListener(this);
        ppoints_spin_ppoint.setOnItemSelectedListener(this);

        Bundle b = getIntent().getExtras();
        choice = b.getInt("choice");


        startingpointList = new ArrayList<HashMap<String, String>>();


        new GetCity().execute();

    }

    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();


        for (int i = 0; i < pcityList.size(); i++) {
            lables.add(pcityList.get(i).getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ppoints_spin_city.setAdapter(spinnerAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner ppointsselspinner = (Spinner) parent;
        if(ppointsselspinner.getId() == R.id.ppoint_spinner_city)
        {
            ppselcity = ppoints_spin_city.getSelectedItem().toString();
            PickupARRLIST.clear();
            startingpointList.clear();
            new LoadPickupPoints().execute();
        }
        if(ppointsselspinner.getId() == R.id.ppoint_spinner_ppoints)
        {
            selectedppoint = ppoints_spin_ppoint.getSelectedItem().toString();
            PickupARRLIST.clear();
            new DisplayStartingPoints().execute();
            startingpointList.clear();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void populatepickupnamespinner() {
        List<String> ppnamelistS = new ArrayList<String>();

        for (int k = 0; k < PickupARRLIST.size(); k++) {
            ppnamelistS.add(PickupARRLIST.get(k).getName());
        }
        ArrayAdapter<String> pickuppointspinneradapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, ppnamelistS);
        ppoints_spin_ppoint.setAdapter(pickuppointspinneradapter);

    }

    //********************************************************************************
    private class GetCity extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


                pDialog = new ProgressDialog(PickUpPoints.this);
                pDialog.setMessage("Fetching City List..");
                pDialog.setCancelable(false);
                pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(URL, ServiceHandler.GET);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray categories = jsonObj
                                .getJSONArray("bus");

                        for (int i = 0; i < categories.length(); i++) {
                            JSONObject catObj = (JSONObject) categories.get(i);
                            City pcat = new City(
                                    catObj.getString("City"), catObj.getInt("BusNo"));
                            pcityList.add(pcat);


                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateSpinner();
        }

    }

    //**********************************************************************************************************


//****************************************************************************************************
    private class LoadPickupPoints extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            List<NameValuePair> pickupparams = new ArrayList<NameValuePair>();

            pickupparams.add(new BasicNameValuePair("City", ppselcity));
            pickupparams.add(new BasicNameValuePair("control", choice +""));

            JSONObject pickupjsonob = pickupparser.makeHttpRequest(LOADPICKUPPOINTS, "GET", pickupparams);

            try {
                int success = pickupjsonob.getInt(TAG_SUCCESS);



                if (success == 1) {
                    Log.e("CALL SUCCESS", "Bellow");
                    pickuppointarr = pickupjsonob.getJSONArray(TAG_PICKUPARRAY);

                    for (int i = 0; i < pickuppointarr.length(); i++) {
                        JSONObject p = pickuppointarr.getJSONObject(i);
                        String pickupnamestr = p.getString(TAG_PPNAME);

                        ReturnPickupPoints ppname = new ReturnPickupPoints(pickupnamestr);
                        PickupARRLIST.add(ppname);


                    }
                } else {
                        
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String resultw) {


            super.onPostExecute(resultw);
            populatepickupnamespinner();


        }

    }
    private class DisplayStartingPoints extends AsyncTask<String,String ,String >
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... params) {



            List<NameValuePair> startingparams = new ArrayList<NameValuePair>();

            startingparams.add(new BasicNameValuePair("pickup",selectedppoint ));
            startingparams.add(new BasicNameValuePair("control",1+""));
            Log.e("choice", String.valueOf(choice));
            JSONObject selectedpoint = selectedpointparser.makeHttpRequest(SELECTEDPPOINTS, "GET", startingparams);

            try {
                    int selsuccess = selectedpoint.getInt(TAG_SUCCESS);

                    if(selsuccess == 1)
                    {
                        Log.e("SUCK", "CALL MADE" + selectedppoint + choice);
                        startingpoints = selectedpoint.getJSONArray(TAG_SELPOINTSARR);
                        //Log.e("DR DEBUG",""+selpointName+selbusno);
                        Log.e("DR DEBUG 2",""+startingpoints.length());
                        for(int i=0; i<startingpoints.length(); i++)
                        {
                            JSONObject s = startingpoints.getJSONObject(i);

                            String selpointName = s.getString(TAG_RETURNSP);
                            String selbusno = s.getString(TAG_RETURNBUSNO);
                            Log.e("DR DEBUG 4",""+i);
                            Log.e("BUS",selbusno);
                            Log.e("No", selpointName);
                            HashMap<String,String> returnresult = new HashMap<String,String>();

                            returnresult.put(TAG_RETURNBUSNO,selbusno);
                            returnresult.put(TAG_RETURNSP,selpointName);

                            startingpointList.add(returnresult);
                        }
                                          }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Log.e("Bellow", "Test1");
                    final ListAdapter adapter = new SimpleAdapter(
                            PickUpPoints.this, startingpointList,
                            R.layout.startingpoint_list, new String[]{TAG_RETURNBUSNO,
                            TAG_RETURNSP},
                            new int[]{R.id.starting_busno, R.id.starting_pname});

                    setListAdapter(adapter);


                }
            });
        }
    }
}
