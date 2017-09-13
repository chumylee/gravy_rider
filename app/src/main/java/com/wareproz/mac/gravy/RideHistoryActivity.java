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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A screen that shows ride history of customer
 */
public class RideHistoryActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private ListView lv;

    ArrayList<HashMap<String, String>> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);

        historyList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        new GetRideHistory().execute();

    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetRideHistory extends AsyncTask<Void, Void, Void> {

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

            //get user's details
            SessionManagement sessionManagement = new SessionManagement(RideHistoryActivity.this);
            Map<String, String> userDetails = sessionManagement.getUserDetails();
            String userId = userDetails.get(SessionManagement.KEY_ID);

            // URL to get ride history JSON
            String url = "ride_history.php?uid="+userId;

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray history = jsonObj.getJSONArray("history");

                    // looping through All ride histories
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
                        String vehicle_type = c.getString("vehicle_type");
                        String payment_method = c.getString("payment_method");

                        // tmp hash map for single contact
                        HashMap<String, String> eachride = new HashMap<>();

                        // adding each child node to HashMap key => value
                        eachride.put("rideId", rideId);
                        eachride.put("driver_name", driver_name);
                        eachride.put("pickup_text", pickup_text);
                        eachride.put("dropoff_text", dropoff_text);
                        eachride.put("ride_amount", "NGN "+ride_amount);
                        eachride.put("ride_rating", ride_rating);
                        eachride.put("ride_status", ride_status);
                        eachride.put("ride_class", ride_class);
                        eachride.put("vehicle_type", vehicle_type);
                        eachride.put("payment_method", payment_method);

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                        Date parsedDate = null;
                        try {
                            parsedDate = formatter.parse(ride_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String date = formatter2.format(parsedDate);
                        eachride.put("ride_date", date);

                        // adding history to history list
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
                    R.layout.history_list_item, new String[]{"driver_name","pickup_text","dropoff_text","ride_date","ride_amount","ride_status","vehicle_type", "payment_method"},
                    new int[]{R.id.driver_name,R.id.pickup_location,R.id.dropoff_location,R.id.ride_date,R.id.ride_amount,R.id.ride_status,R.id.vehicle_name,R.id.payment_method});

            lv.setAdapter(adapter);

        }

    }


}

