package com.wareproz.mac.gravy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LandingActivity extends AppCompatActivity implements View.OnClickListener {

    // Session Manager Class
    SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        Button btnLogin = (Button) findViewById(R.id.login);
        btnLogin.setOnClickListener(this);
        Button btnRegister = (Button) findViewById(R.id.register);
        btnRegister.setOnClickListener(this);

        // Session Manager
        session = new SessionManagement(getApplicationContext());

        //check if user is logged on and just send him straight to home
        if(session.isLoggedIn()){
            //open home page
            Intent mIntent = new Intent(LandingActivity.this, HomeActivity.class);
            startActivity(mIntent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {

        //check if login was clicked
        if (view.getId() == R.id.login){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }

        //check if signup was clicked
        if (view.getId() == R.id.register){
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}
