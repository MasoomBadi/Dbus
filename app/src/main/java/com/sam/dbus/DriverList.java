package com.sam.dbus;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DriverList extends ListActivity {


    private ProgressDialog pDialog;
    public static int choice;

    EditText filter;
    ListAdapter adapter;
    // String webfrom = getString(R.string.webservice1);
    //  String webfolder = "dbus/db_getalldriver.php";


    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;


    private String url_all_products;


    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "driver";
    private static final String TAG_PID = "Busno";
    private static final String TAG_NAME = "DriverName";
    private static final String TAG_CONTACT = "DriverContactNo";
    private static final String TAG_CITY = "FromCity";


    JSONArray products = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverlist);
        SharedPreferences prefs = getSharedPreferences("LAN", MODE_PRIVATE);

            String lanSettings = prefs.getString("WEB", "");
            url_all_products = lanSettings + "/dbus/db_getalldriver.php";


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);



        filter = (EditText) findViewById(R.id.editText);

        productsList = new ArrayList<HashMap<String, String>>();

        Bundle b = getIntent().getExtras();
        choice = b.getInt("choice");


        new LoadAllProducts().execute();


        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                try {
                    JSONObject num = products.getJSONObject(position);
                    String number = num.getString(TAG_CONTACT);

                    Uri call = Uri.parse("tel:" + number);
                    Intent dcall = new Intent(Intent.ACTION_DIAL, call);
                    startActivity(dcall);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "ERR" + e, Toast.LENGTH_LONG).show();
                }


            }
        });

        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Toast.makeText(getApplicationContext(), "" + filter.getText(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 100) {

            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }


    class LoadAllProducts extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {

            super.onPreExecute();




                pDialog = new ProgressDialog(DriverList.this);
                pDialog.setMessage("Loading Driver list Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();

        }


        protected String doInBackground(String... args) {
            Log.e("Choice", choice + "");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("control", choice + ""));

            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);


            Log.d("All Products: ", json.toString());

            try {

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    products = json.getJSONArray(TAG_PRODUCTS);

                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);


                        String id = c.getString(TAG_PID);
                        String dname = c.getString(TAG_NAME);
                        String dcity = c.getString(TAG_CITY);
                        String dcontact = c.getString(TAG_CONTACT);

                        HashMap<String, String> map = new HashMap<String, String>();


                        map.put(TAG_PID, id);
                        map.put(TAG_NAME, dname);
                        map.put(TAG_CITY, dcity);
                        map.put(TAG_CONTACT, dcontact);

                        productsList.add(map);

                    }


                } else {


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String file_url) {

            super.onPreExecute();
            pDialog.dismiss();


            runOnUiThread(new Runnable() {
                public void run() {

                    final ListAdapter adapter = new SimpleAdapter(
                            DriverList.this, productsList,
                            R.layout.listitem, new String[]{TAG_PID,
                            TAG_NAME, TAG_CITY, TAG_CONTACT},
                            new int[]{R.id.driverbusnumber, R.id.drivername, R.id.drivercity, R.id.drivermobilenumber});

                    setListAdapter(adapter);


                }
            });

        }

    }
}