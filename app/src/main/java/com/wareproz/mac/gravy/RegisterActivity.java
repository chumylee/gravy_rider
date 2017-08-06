package com.wareproz.mac.gravy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class RegisterActivity extends AppCompatActivity {

    TextView terms,txtfirst_name,txtlast_name,txtemail,txtphone,txtusername,txtpassword;
    ConnectionDetector connectionDetector;
    String url_firstname = null,url_lastname = null,url_email = null,url_phone = null,url_username = null,url_password = null;
    Button submit;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        terms = (TextView) findViewById(R.id.terms);

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mintent = new Intent(RegisterActivity.this, WebViewActivity.class);
                mintent.putExtra("url", "https://gravy.com.ng/terms.php");
                mintent.putExtra("title", "Terms and Conditions");
                startActivity(mintent);
            }
        });

        txtfirst_name = (TextView) findViewById(R.id.firstname);
        txtlast_name = (TextView) findViewById(R.id.lastname);
        txtphone = (TextView) findViewById(R.id.phone);
        txtemail = (TextView) findViewById(R.id.email);
        txtusername = (TextView) findViewById(R.id.username);
        txtpassword = (TextView) findViewById(R.id.password);

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
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Submitting ...");
            pDialog.setCancelable(false);
            pDialog.show();
            try {
                url_firstname = URLEncoder.encode(txtfirst_name.getText().toString(), "utf-8");
                url_lastname = URLEncoder.encode(txtlast_name.getText().toString(), "utf-8");
                url_email = URLEncoder.encode(txtemail.getText().toString(), "utf-8");
                url_phone = URLEncoder.encode(txtphone.getText().toString(), "utf-8");
                url_username = URLEncoder.encode(txtusername.getText().toString(), "utf-8");
                url_password = URLEncoder.encode(txtpassword.getText().toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response

            String url = "register.php?phone="+ url_phone + "&firstname=" + url_firstname + "&lastname=" + url_lastname + "&email=" + url_email + "&username=" + url_username + "&password=" + url_password;
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
                Toast.makeText(RegisterActivity.this,"User registered successfully",Toast.LENGTH_LONG).show();

                Intent mIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(mIntent);
                finish();
            }else{
                //
                Toast.makeText(RegisterActivity.this,"Registration Unsuccessful",Toast.LENGTH_LONG).show();
            }
        }

    }


}

