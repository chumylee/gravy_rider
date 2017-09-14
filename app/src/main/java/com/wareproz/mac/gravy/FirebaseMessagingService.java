package com.wareproz.mac.gravy;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by filipp on 5/23/2016.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    String theactivity,ride_id,payment_method,address,ride_type,rider_rating,
            car_model, car_number, driver_name, driver_rating, driver_phone,
            driver_picture, vehicle_image, pickup_gps, pickup_address, driver_location,
            dropoff_gps, dropoff_name, message,ride_amount,driver_id;
    // Context
    Context _context;
    //get globals
    Globals g = Globals.getInstance();


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        this._context = getApplicationContext();

        theactivity = remoteMessage.getData().get("activity");

        if (theactivity.equals("rideRequest") && !g.getRideRequestVisibility()){
            ride_id = remoteMessage.getData().get("ride_id");
            payment_method = remoteMessage.getData().get("payment_method");
            address = remoteMessage.getData().get("address");
            ride_type = remoteMessage.getData().get("ride_type");
            rider_rating = remoteMessage.getData().get("rider_rating");

            Intent i = new Intent(_context, DriverEnrouteActivity.class);
            i.putExtra("ride_id", ride_id);
            i.putExtra("payment_method", payment_method);
            i.putExtra("address", address);
            i.putExtra("ride_type", ride_type);
            i.putExtra("rider_rating", rider_rating);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);

        }else if (theactivity.equals("acceptRequest")){

            ride_id = remoteMessage.getData().get("ride_id");
            car_model = remoteMessage.getData().get("car_model");
            car_number = remoteMessage.getData().get("car_number");
            driver_name = remoteMessage.getData().get("driver_name");
            driver_id = remoteMessage.getData().get("driver_id");
            driver_rating = remoteMessage.getData().get("driver_rating");
            driver_phone = remoteMessage.getData().get("driver_phone");
            driver_picture = remoteMessage.getData().get("driver_picture");
            vehicle_image = remoteMessage.getData().get("vehicle_image");
            pickup_gps = remoteMessage.getData().get("pickup_gps");
            pickup_address = remoteMessage.getData().get("pickup_address");
            driver_location = remoteMessage.getData().get("driver_location");
            dropoff_gps = remoteMessage.getData().get("dropoff_gps");
            dropoff_name = remoteMessage.getData().get("dropoff_name");

            Intent i = new Intent(_context, DriverEnrouteActivity.class);
            i.putExtra("ride_id", ride_id);
            i.putExtra("car_model", car_model);
            i.putExtra("car_number", car_number);
            i.putExtra("driver_name", driver_name);
            i.putExtra("driver_id", driver_id);
            i.putExtra("driver_rating", driver_rating);
            i.putExtra("driver_phone", driver_phone);
            i.putExtra("driver_picture", driver_picture);
            i.putExtra("vehicle_image", vehicle_image);
            i.putExtra("pickup_gps", pickup_gps);
            i.putExtra("pickup_address", pickup_address);
            i.putExtra("driver_location", driver_location);
            i.putExtra("dropoff_gps", dropoff_gps);
            i.putExtra("dropoff_name", driver_location);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);

        }else if (theactivity.equals("driverArrived")){

            message = remoteMessage.getData().get("message");
            showNotification(message);

        }else if (theactivity.equals("startRide")){

            ride_id = remoteMessage.getData().get("ride_id");
            pickup_gps = remoteMessage.getData().get("pickup_gps");
            pickup_address = remoteMessage.getData().get("pickup_address");
            dropoff_gps = remoteMessage.getData().get("dropoff_gps");
            dropoff_name = remoteMessage.getData().get("dropoff_name");

            Intent i = new Intent(_context, TripStartedActivity.class);
            i.putExtra("ride_id", ride_id);
            i.putExtra("pickup_gps", pickup_gps);
            i.putExtra("pickup_address", pickup_address);
            i.putExtra("dropoff_gps", dropoff_gps);
            i.putExtra("dropoff_name", dropoff_name);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);

        }else if (theactivity.equals("EndTrip")){

            ride_id = remoteMessage.getData().get("ride_id");
            pickup_gps = remoteMessage.getData().get("pickup_gps");
            pickup_address = remoteMessage.getData().get("pickup_address");
            dropoff_gps = remoteMessage.getData().get("dropoff_gps");
            dropoff_name = remoteMessage.getData().get("dropoff_name");
            payment_method = remoteMessage.getData().get("payment_method");
            ride_amount = remoteMessage.getData().get("ride_amount");
            driver_name = remoteMessage.getData().get("driver_name");
            driver_picture = remoteMessage.getData().get("driver_picture");

            Intent i = new Intent(_context, TripEndedActivity.class);
            i.putExtra("ride_id", ride_id);
            i.putExtra("pickup_gps", pickup_gps);
            i.putExtra("pickup_address", pickup_address);
            i.putExtra("dropoff_gps", dropoff_gps);
            i.putExtra("dropoff_name", dropoff_name);
            i.putExtra("driver_id", driver_id);
            i.putExtra("driver_name", driver_name);
            i.putExtra("driver_picture", driver_picture);
            i.putExtra("ride_amount", ride_amount);
            i.putExtra("payment_method", payment_method);

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);

        }

    }

    private void showNotification(String message) {

        Intent i = new Intent(this,HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Gravy")
                .setContentText(message)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());

    }

}
