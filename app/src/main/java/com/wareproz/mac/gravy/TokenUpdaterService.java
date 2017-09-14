package com.wareproz.mac.gravy;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.wareproz.mac.gravy.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class TokenUpdaterService extends IntentService {

    public TokenUpdaterService() {
        super("TokenUpdaterService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String token = FirebaseInstanceId.getInstance().getToken();
        try {
            while (token == null) {
                Thread.sleep(3000);
                token = FirebaseInstanceId.getInstance().getToken();
            }
            updateTokenOnServer(token);
        } catch (InterruptedException e) { }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void updateTokenOnServer(final String token){

        (new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                SessionManagement session = new SessionManagement(getApplicationContext());
                Globals.getInstance().setToken(token);
                session.storeToken();
                Map<String,String> user = session.getUserDetails();
                String uid = user.get(SessionManagement.KEY_ID);
                String url = "update_token.php?uid="+uid+"&token="+token;

                HttpHandler http = new HttpHandler();
                String response = http.makeServiceCall(url);
                return null;
            }
        }).execute();
    }
}
