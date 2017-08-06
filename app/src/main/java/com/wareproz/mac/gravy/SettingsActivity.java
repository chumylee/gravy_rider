package com.wareproz.mac.gravy;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * A login screen that offers login via email/password.
 */
public class SettingsActivity extends AppCompatActivity {

    ConnectionDetector connectionDetector;
    SessionManagement session;
    String id,email,first_name,last_name,user_picture,phone;
    TextView txtfirst_name,txtlast_name,txtemail,txtphone;
    String url_id = null,url_firstname = null,url_lastname = null,url_email = null;
    Button submit;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Session class instance
        session = new SessionManagement(getApplicationContext());

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        id = user.get(SessionManagement.KEY_ID);
        email = user.get(SessionManagement.KEY_EMAIL);
        first_name = user.get(SessionManagement.FIRST_NAME);
        last_name = user.get(SessionManagement.LAST_NAME);
        user_picture = user.get(SessionManagement.INVITE_CODE);
        phone = user.get(SessionManagement.PHONE);

        txtfirst_name = (TextView) findViewById(R.id.firstname);
        txtlast_name = (TextView) findViewById(R.id.lastname);
        txtphone = (TextView) findViewById(R.id.phone);
        txtemail = (TextView) findViewById(R.id.email);

        txtfirst_name.setText(first_name);
        txtlast_name.setText(last_name);
        txtemail.setText(email);
        txtphone.setText(phone);

        submit = (Button) findViewById(R.id.regsubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SaveChanges().execute();
            }
        });

    }

    private class SaveChanges extends AsyncTask<Void, Void, Void> {

        String json_result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SettingsActivity.this);
            pDialog.setMessage("Saving Changes...");
            pDialog.setCancelable(false);
            pDialog.show();
            try {
                url_id = URLEncoder.encode(id, "utf-8");
                url_firstname = URLEncoder.encode(txtfirst_name.getText().toString(), "utf-8");
                url_lastname = URLEncoder.encode(txtlast_name.getText().toString(), "utf-8");
                url_email = URLEncoder.encode(txtemail.getText().toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response

            String url = "edit_profile.php?id="+ url_id + "&firstname=" + url_firstname + "&lastname=" + url_lastname + "&email=" + url_email;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    json_result = jsonObj.getString("json_result");

                    //JSONArray contacts = jsonObj.getJSONArray("contacts");

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

            if (json_result.equals("1")){
                // Creating user login session and store some stuff
                Toast.makeText(SettingsActivity.this,"Changes saved successfully",Toast.LENGTH_LONG).show();
            }else{
                //
                Toast.makeText(SettingsActivity.this,"Unable to save changes",Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                new LogoutRider().execute();
                session.logoutUser();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class LogoutRider extends AsyncTask<Void, Void, Void> {

        String json_result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SettingsActivity.this);
            pDialog.setMessage("Loging Out...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String url = "driver_logout.php?id="+ id;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    json_result = jsonObj.getString("json_result");

                    //JSONArray contacts = jsonObj.getJSONArray("contacts");

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
            Log.d("TAG", json_result);
        }

    }

}

