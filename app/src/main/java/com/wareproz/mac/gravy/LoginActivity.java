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

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    Button login_button;
    TextView txtemail, txtpassword;
    ConnectionDetector connectionDetector;
    String email, password, Token;

    private ProgressDialog pDialog;

    // Session Manager Class
    SessionManagement session;

    Globals g = Globals.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        connectionDetector = new ConnectionDetector(this);
        login_button = (Button) findViewById(R.id.email_sign_in_button);
        txtemail = (TextView) findViewById(R.id.email);
        txtpassword = (TextView) findViewById(R.id.password);

        // Session Manager
        session = new SessionManagement(getApplicationContext());

//      check if user is logged on and just send him straight to home
        if(session.isLoggedIn()){
            //open home page
            Intent mIntent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(mIntent);
            finish();
        }

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if there is internet access
                if(!connectionDetector.isConnectingToInternet()){

                    Toast.makeText(LoginActivity.this,"Internet Network Not Avaliable",Toast.LENGTH_LONG).show();

                }else {

                    email = txtemail.getText().toString();
                    password = txtpassword.getText().toString();

                    if(email.trim().length() > 0 && password.trim().length() > 0){

                        new LoginDriver().execute();

                    }else{
                        Toast.makeText(LoginActivity.this,"Email And Password Must Be Entered",Toast.LENGTH_LONG).show();

                    }

                }

            }
        });

        //subscribe to push notification
        FirebaseMessaging.getInstance().subscribeToTopic("gravy");
        FirebaseInstanceId.getInstance().getToken();
    }

    private class LoginDriver extends AsyncTask<Void, Void, Void> {

        String json_result,id,first_name,last_name,phone,invite_code,user_picture;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
//            if(!session.hasToken()) {
//                Token = g.getToken();
//            }else{
//                Token = session.getTokenDetails();
//            }
            Token = g.getToken();

            String url = "rider_login.php?username="+ email +"&password="+ password +"&role=3&token="+ Token;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    json_result = jsonObj.getString("json_result");
                    id = jsonObj.getString("id");
                    first_name = jsonObj.getString("first_name");
                    last_name = jsonObj.getString("last_name");
                    phone = jsonObj.getString("phone");
                    invite_code = jsonObj.getString("invite_code");
                    user_picture = jsonObj.getString("user_picture");

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

            //do something with what is returned
            if (json_result.equals("1")){
                // Creating user login session and store some stuff
                session.createLoginSession(email,id,first_name,last_name,phone,invite_code,user_picture);

                //store token in preference
                if(!session.hasToken()) {
                    session.storeToken();
                }

                if(Token == null){ //if token is null at point of login, start activity to update token once it becomes available
                    Intent startUpdateTokenService = new Intent(LoginActivity.this, TokenUpdaterService.class);
                    startService(startUpdateTokenService);
                }

                //open home page
                Intent mIntent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(mIntent);
                finish();

            }else{
                //
                Toast.makeText(LoginActivity.this,"Invalid Login Details",Toast.LENGTH_LONG).show();
            }
        }

    }
}
