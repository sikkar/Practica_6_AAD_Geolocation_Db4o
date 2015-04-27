package com.izv.angel.geolocation;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


public class ActividadMapa extends ActionBarActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    public static GoogleMap miMapa;
    private PolylineOptions polilinea;
    private ArrayList<Localizacion> posiciones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_actividad_mapa);
        Intent i = getIntent();
        int pos=i.getIntExtra("posicion",-1);
        Log.v("pos",pos+"");
        if (pos==-1) {
            posiciones = Principal.lista;
            Log.v("lista","lista");
        }else {
            posiciones = Principal.listaRutas.get(pos).getLocalizaciones();
            Log.v("listarutas","listarutas");
        }
        polilinea = new PolylineOptions();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        miMapa=mapFragment.getMap();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng zona0 = new LatLng(posiciones.get(0).getLatitud(), posiciones.get(0).getLongitud());
        miMapa.setMyLocationEnabled(true);

        miMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(zona0, 13));

        miMapa.addMarker(new MarkerOptions()
                .title("Inicio")
                .snippet(posiciones.get(0).getCalle())
                .position(zona0));
        for (int i = 0; i <posiciones.size() ; i++) {
            polilinea.add(new LatLng(posiciones.get(i).getLatitud(), posiciones.get(i).getLongitud()));
            miMapa.addPolyline(polilinea);
        }
        LatLng zonaFin = new LatLng(posiciones.get(posiciones.size()-1).getLatitud(), posiciones.get(posiciones.size()-1).getLongitud());
        miMapa.addMarker(new MarkerOptions()
                .title("Fin")
                .snippet(posiciones.get(posiciones.size()-1).getCalle())
                .position(zonaFin));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_actividad_mapa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if(miMapa.getMapType()==GoogleMap.MAP_TYPE_NORMAL){
                miMapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }else{
                miMapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
            return true;
        }
        if (id == R.id.action_streetview) {
            Intent svintent = new Intent(this,StreetView.class);
            LatLng zonaFin = new LatLng(posiciones.get(posiciones.size()-1).getLatitud(), posiciones.get(posiciones.size()-1).getLongitud());
            svintent.putExtra("position",zonaFin);
            startActivity(svintent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapLoaded() {

    }
}
