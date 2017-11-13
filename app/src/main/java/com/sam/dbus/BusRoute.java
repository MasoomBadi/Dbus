package com.sam.dbus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;



public class BusRoute extends ListActivity implements OnItemSelectedListener {

    private Spinner cityname, recbusno;
    TextView brcity, brbusno, radioselect;
    RadioButton fromcollege, tocollege;
    RadioGroup selectfromto;
    public static int ordercontrol;

    Button mapdisplay;


    private ArrayList<City> cityList, busnoList;
    ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    JSONParser jpickupParser = new JSONParser();

    JSONArray products = null;
    JSONArray pickuppoints = null;

    ArrayList<String> coordinatearrlat;
    ArrayList<String> coordinatearrlon;
    ArrayList<String> pickupnamesmap;

    ArrayList<HashMap<String, String>> pickuppointList;
    HashMap<String, String> map = new HashMap<String, String>();

    ListAdapter adapter;

    private String URL;
    private String getbusno;
    private String pickuplist;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "busnoarray";
    private static final String TAG_BUSNO = "BusNo";
    private static final String TAG_PICKUP = "pickupresult";
    private static final String TAG_PICKUPPOINTNO = "PickupPointNo";
    private static final String TAG_PICKUPPOINTNAME = "Name";
    private static final String TAG_LAT = "LAT";
    private static final String TAG_LON = "LON";

    ListView list1;
    String selcity = null;
    String valsel = null;
    public static int choice;

    public static int counter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_route);
        SharedPreferences prefs = getSharedPreferences("LAN", MODE_PRIVATE);

        String lanSettings = prefs.getString("WEB", "");
        URL = lanSettings + "/dbus/dbus.php";
        getbusno = lanSettings + "/dbus/db_getbuslist.php";
        pickuplist = lanSettings + "/dbus/db_getpickup.php";


        cityname = (Spinner) findViewById(R.id.br_spinner_city);
        recbusno = (Spinner) findViewById(R.id.br_spinner_busno);

        cityList = new ArrayList<City>();
        busnoList = new ArrayList<City>();

        cityname.setOnItemSelectedListener(this);
        recbusno.setOnItemSelectedListener(this);

        brcity = (TextView) findViewById(R.id.br_tv_city);
        brbusno = (TextView) findViewById(R.id.br_tv_busno);
        radioselect = (TextView) findViewById(R.id.br_tv_select_radio);

        mapdisplay = (Button) findViewById(R.id.br_btn_showonmap);

        Typeface thin = Typeface.createFromAsset(getAssets(), "font/Roboto-Thin.ttf");
        Typeface light = Typeface.createFromAsset(getAssets(), "font/Roboto-Light.ttf");

        brbusno.setTypeface(thin);
        brcity.setTypeface(thin);
        radioselect.setTypeface(thin);
        mapdisplay.setTypeface(light);

        fromcollege = (RadioButton) findViewById(R.id.br_radio_fromcollege);
        tocollege = (RadioButton) findViewById(R.id.br_radio_tocollege);

        fromcollege.setTypeface(light);
        tocollege.setTypeface(light);

        selectfromto = (RadioGroup) findViewById(R.id.br_radiogroup_select);
        new GetCategories().execute();

        pickuppointList = new ArrayList<HashMap<String, String>>();
        coordinatearrlat = new ArrayList<String>();
        coordinatearrlon = new ArrayList<String>();
        pickupnamesmap = new ArrayList<String>();

        Bundle b = getIntent().getExtras();
        choice = b.getInt("choice");
        mapdisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("lenofarr", coordinatearrlat.size() + "");

                Intent ss = new Intent(BusRoute.this, MapsActivity.class);
                ss.putStringArrayListExtra("coordinatearrlat", coordinatearrlat);
                ss.putStringArrayListExtra("coordinatearrlon", coordinatearrlon);
                ss.putStringArrayListExtra("pickupnamesmap", pickupnamesmap);
                finish();
                startActivity(ss);
            }
        });


    }


    public void onRadiobuttonClicked(View rbtn) {


        busnoList.clear();
        valsel = recbusno.getSelectedItem().toString();
        pickuppointList.clear();
        new DisplayPickupPoints().execute();
        if (fromcollege.isChecked()) {
            ordercontrol = 1;
            busnoList.clear();
            pickuppointList.clear();

        }
        if (tocollege.isChecked()) {
            ordercontrol = 0;
            busnoList.clear();
            pickuppointList.clear();
        }

    }


    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();


        for (int i = 0; i < cityList.size(); i++) {
            lables.add(cityList.get(i).getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cityname.setAdapter(spinnerAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner getSpinnersel = (Spinner) parent;
        if (getSpinnersel.getId() == R.id.br_spinner_city) {
            selcity = cityname.getSelectedItem().toString();
            new LoadAllProducts().execute();
            selectfromto.clearCheck();

            busnoList.clear();
            setListAdapter(null);
        }
        if (getSpinnersel.getId() == R.id.br_spinner_busno) {
            selectfromto.clearCheck();
            busnoList.clear();
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    //#####################################################################################################################################################3
    private class GetCategories extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {


            super.onPreExecute();
            pDialog = new ProgressDialog(BusRoute.this);
            pDialog.setMessage("Fetching Bus List..");
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
                            City cat = new City(
                                    catObj.getString("City"), catObj.getInt("BusNo"));
                            cityList.add(cat);


                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
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
    // GET BUS NO

    //8******************************************************************************************************************************************************
    private class LoadAllProducts extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();


            params.add(new BasicNameValuePair("City", selcity));
            params.add(new BasicNameValuePair("control", choice + ""));

            JSONObject json1 = jParser.makeHttpRequest(getbusno, "GET", params);


            try {

                int success = json1.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Log.e("CALL SUCCESS", "123");
                    products = json1.getJSONArray(TAG_PRODUCTS);

                    for (int j = 0; j < products.length(); j++) {
                        JSONObject c = products.getJSONObject(j);


                        String busno = c.getString(TAG_BUSNO);
                        City cat = new City(busno);
                        busnoList.add(cat);
                        Log.e("JSON SUCCESS", busno);


                    }


                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String resultw) {


            super.onPostExecute(resultw);
            populatebusspinner();


        }

    }

    private void populatebusspinner() {
        selectfromto.clearCheck();
        List<String> lablebus = new ArrayList<String>();


        for (int i = 0; i < busnoList.size(); i++) {
            lablebus.add(busnoList.get(i).getBusNo());
        }
        ArrayAdapter<String> busspinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lablebus);


        busspinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        recbusno.setAdapter(busspinnerAdapter);

    }

//***********************************************************************************************


    private class DisplayPickupPoints extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            Log.e("data", "a" + selcity + "b" + valsel);
            params.add(new BasicNameValuePair("City", selcity));
            params.add(new BasicNameValuePair("busno", valsel));
            Log.e("Commm", ordercontrol + "");
            params.add(new BasicNameValuePair("oc", ordercontrol + ""));
            params.add(new BasicNameValuePair("control", choice + ""));

            JSONObject jsonpp = jpickupParser.makeHttpRequest(pickuplist, "GET", params);

            Log.d("All Pickup Points: ", jsonpp.toString());

            try {

                int success = jsonpp.getInt(TAG_SUCCESS);

                if (success == 1) {

                    pickuppoints = jsonpp.getJSONArray(TAG_PICKUP);


                    for (int i = 0; i < pickuppoints.length(); i++) {
                        JSONObject c = pickuppoints.getJSONObject(i);


                        String PPNo = c.getString(TAG_PICKUPPOINTNO);
                        String PPName = c.getString(TAG_PICKUPPOINTNAME);
                        String RECLAT = c.getString(TAG_LAT);
                        String RECLON = c.getString(TAG_LON);


                        HashMap<String, String> map = new HashMap<String, String>();


                        map.put(TAG_PICKUPPOINTNO, PPNo);
                        map.put(TAG_PICKUPPOINTNAME, PPName);


                        pickuppointList.add(map);


                        coordinatearrlat.add(RECLAT);
                        coordinatearrlon.add(RECLON);
                        pickupnamesmap.add(PPName);


                        Log.e("LAT", "" + coordinatearrlat.get(i));
                        Log.e("LON", "" + coordinatearrlon.get(i));
                        Log.e("Name", "" + pickupnamesmap.get(i));
                    }


                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String file_url) {


            runOnUiThread(new Runnable() {
                public void run() {

                    final ListAdapter adapter = new SimpleAdapter(
                            BusRoute.this, pickuppointList,
                            R.layout.pickup_point_list, new String[]{TAG_PICKUPPOINTNO,
                            TAG_PICKUPPOINTNAME},
                            new int[]{R.id.pickuppoint_ppno, R.id.pickuppoint_ppname});


                    setListAdapter(adapter);


                }
            });

        }


    }

}