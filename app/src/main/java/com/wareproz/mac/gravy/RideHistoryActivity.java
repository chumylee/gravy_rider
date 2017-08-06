package com.wareproz.mac.gravy;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A login screen that offers login via email/password.
 */
public class RideHistoryActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get contacts JSON
    String url = "ride_history.php?uid=1";

    ArrayList<HashMap<String, String>> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);

        historyList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        new GetContacts().execute();

    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(RideHistoryActivity.this);
            pDialog.setMessage("Loading ride history ...");
            //pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray history = jsonObj.getJSONArray("history");

                    // looping through All Contacts
                    for (int i = 0; i < history.length(); i++) {
                        JSONObject c = history.getJSONObject(i);

                        String rideId = c.getString("rideId");
                        String driver_name = c.getString("driver_name");
                        String pickup_text = c.getString("pickup_text");
                        String dropoff_text = c.getString("dropoff_text");
                        String ride_date = c.getString("ride_date");
                        String ride_amount = c.getString("ride_amount");
                        String ride_rating = c.getString("ride_rating");
                        String ride_status = c.getString("ride_status");
                        String ride_class = c.getString("ride_class");

                        // tmp hash map for single contact
                        HashMap<String, String> eachride = new HashMap<>();

                        // adding each child node to HashMap key => value
                        eachride.put("rideId", rideId);
                        eachride.put("driver_name", driver_name);
                        eachride.put("pickup_text", pickup_text);
                        eachride.put("dropoff_text", dropoff_text);
                        eachride.put("ride_date", ride_date);
                        eachride.put("ride_amount", ride_amount);
                        eachride.put("ride_rating", ride_rating);
                        eachride.put("ride_status", ride_status);
                        eachride.put("ride_class", ride_class);

                        // adding contact to contact list
                        historyList.add(eachride);
                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    RideHistoryActivity.this, historyList,
                    R.layout.history_list_item, new String[]{"driver_name","pickup_text","dropoff_text","ride_date","ride_amount","ride_rating","ride_status","ride_class"},
                    new int[]{R.id.driver_name,R.id.pickup_location,R.id.dropoff_location,R.id.ride_date,R.id.ride_amount,R.id.ride_rating,R.id.ride_status,R.id.ride_class});

            lv.setAdapter(adapter);

        }

    }


}

