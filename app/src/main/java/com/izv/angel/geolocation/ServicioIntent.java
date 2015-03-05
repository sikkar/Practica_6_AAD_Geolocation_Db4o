package com.izv.angel.geolocation;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class ServicioIntent extends IntentService {

    private static final String ACTION_GEOCODE = "com.izv.angel.geolocation.action.GEOCODE";
    private static final String EXTRA_PARAM_LOC = "com.izv.angel.geolocation.extra.LOCATION";
    public static final String BROADCAST_ACTION = "izv.com.angel.gelocation.geocode";
    private static Geocoder geocoder;

    public static void startActionGeocode(Context context, Location location) {
        geocoder = new Geocoder(context,Locale.getDefault());
        Intent intent = new Intent(context, ServicioIntent.class);
        intent.setAction(ACTION_GEOCODE);
        intent.putExtra(EXTRA_PARAM_LOC, location);
        context.startService(intent);
    }


    public ServicioIntent() {
        super("ServicioIntent");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //su ejecuccion se realiza en una hebra
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GEOCODE.equals(action)) {
                Bundle b = intent.getExtras();
                Location location = b.getParcelable(EXTRA_PARAM_LOC);
                if(location!=null){
                   handleActionGeoDecode(location);
                }
            }
        }
    }

    private void handleActionGeoDecode(Location location) {
        Intent intent = new Intent(BROADCAST_ACTION);
        Address direccion=null;
        try {
            List<Address> direcciones = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (direcciones.size() > 0) {
                direccion = direcciones.get(0);
                direccion.getAddressLine(0);
                direccion.getLocality();
                direccion.getCountryName();
                intent.putExtra("direccion",direccion);
                intent.putExtra("latitud",direcciones.get(0).getLatitude());
                intent.putExtra("longitud",direcciones.get(0).getLongitude());
                sendBroadcast(intent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
