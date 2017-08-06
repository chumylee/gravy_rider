package com.wareproz.mac.gravy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView facebook = (TextView) findViewById(R.id.facebook);
        facebook.setOnClickListener(this);
        TextView twitter = (TextView) findViewById(R.id.twitter);
        twitter.setOnClickListener(this);
        TextView legal = (TextView) findViewById(R.id.legal);
        legal.setOnClickListener(this);
        TextView contact = (TextView) findViewById(R.id.contact);
        contact.setOnClickListener(this);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.facebook){
            Intent mintent = new Intent(this,WebViewActivity.class);
            mintent.putExtra("url", "https://www.facebook.com");
            mintent.putExtra("title", "Gravy Facebook");
            startActivity(mintent);
        }

        if (view.getId() == R.id.twitter){
            Intent mintent = new Intent(this, WebViewActivity.class);
            mintent.putExtra("url", "https://www.twitter.com");
            mintent.putExtra("title", "Gravy Twitter");
            startActivity(mintent);
        }

        if (view.getId() == R.id.legal){
            Intent mintent = new Intent(this,WebViewActivity.class);
            mintent.putExtra("url", "https://gravy.com.ng/terms.php");
            mintent.putExtra("title", "Terms and Conditions");
            startActivity(mintent);
        }

        if (view.getId() == R.id.contact){
            Intent mintent = new Intent(this, WebViewActivity.class);
            mintent.putExtra("url", "https://gravy.com.ng/#contact");
            mintent.putExtra("title", "Contact Us");
            startActivity(mintent);
        }
    }

}

