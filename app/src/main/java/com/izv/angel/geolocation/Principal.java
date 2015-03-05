package com.izv.angel.geolocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Principal extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient cliente;
    private Location ultimaLocalizacion;
    private LocationRequest peticionLocalizaciones;
    private TextView tvCoord;
    private double latitud, longitud;
    private PolylineOptions polilinea;
    private ObjectContainer bd;
    public static ArrayList<Localizacion> lista;
    public static ArrayList<Ruta> listaRutas;
    private Adaptador ad;
    private ListView lv;
    private Button btInicio, btFin, btMapa,btSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        bd = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), getExternalFilesDir(null) + "/mapabd.db4o");
        registerReceiver(broadcastReceiver, new IntentFilter(ServicioIntent.BROADCAST_ACTION));
        polilinea = new PolylineOptions();
        listaRutas = new ArrayList<Ruta>();
        btInicio = (Button)findViewById(R.id.btInicio);
        btFin = (Button)findViewById(R.id.btFin);
        btFin.setEnabled(false);
        btMapa = (Button)findViewById(R.id.btMapa);
        btMapa.setEnabled(false);
        btSave = (Button)findViewById(R.id.btSave);
        ad = new Adaptador(this, R.layout.detalle_lista, listaRutas);
        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(ad);
        inicializar();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getBaseContext(),ActividadMapa.class);
                i.putExtra("posicion",position);
                //lista = listaRutas.get(position).getLocalizaciones();
                startActivity(i);
            }
        });
        registerForContextMenu(lv);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        if (id == R.id.action_borrar) {
            Query consulta = bd.query();
            consulta.descend("fechaRuta").constrain(listaRutas.get(index).getFechaRuta());
            ObjectSet<Ruta> rutas = consulta.execute();
            if(rutas.hasNext()){
                Ruta r = rutas.next();
                bd.delete(r);
                bd.commit();
                inicializar();
                Toast.makeText(this,R.string.borrado,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,R.string.noeliminar,Toast.LENGTH_SHORT).show();
            }
        }
        return super.onContextItemSelected(item);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.longmenu, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_consulta){
            consulta();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        ultimaLocalizacion = LocationServices.FusedLocationApi.getLastLocation(cliente);
        if (ultimaLocalizacion != null) {

        }
        //aqui podemos realizar las peticiones
        peticionLocalizaciones = new LocationRequest();
        peticionLocalizaciones.setInterval(10000);
        peticionLocalizaciones.setFastestInterval(5000);
        peticionLocalizaciones.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(cliente, peticionLocalizaciones, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        cliente.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        cliente.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        stopService(new Intent(this, ServicioIntent.class));
        super.onDestroy();
        bd.close();
    }

    @Override
    public void onLocationChanged(Location location) {
        /*
        location.getLongitude();
        location.getLatitude();
        location.getAccuracy();
        location.getAccuracy();
        location.getAltitude();
        location.getSpeed();
        location.getBearing();*/
        ServicioIntent.startActionGeocode(this, location);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("recieve", "recibiendo");
            Bundle b = intent.getExtras();
            Address direccion = b.getParcelable("direccion");
            Log.v("recibidodireccion", direccion.toString());
            latitud = b.getDouble("latitud");
            longitud = b.getDouble("longitud");
            Localizacion loc = new Localizacion(Calendar.getInstance().getTime(), latitud, longitud,direccion.getLocality(),direccion.getAddressLine(0));
            lista.add(loc);
        }
    };

    public void iniciar(View view) {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            cliente = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            cliente.connect();
        } else {

        }
        lista = new ArrayList<Localizacion>();
        btInicio.setEnabled(false);
        btFin.setEnabled(true);
        btMapa.setEnabled(true);
        btSave.setVisibility(View.GONE);
        Toast.makeText(this,R.string.iniciar,Toast.LENGTH_SHORT).show();
    }

    public void detener(View view){
        stopService(new Intent(this, ServicioIntent.class));
        cliente.disconnect();
        btInicio.setEnabled(true);
        btFin.setEnabled(false);
        btSave.setVisibility(View.VISIBLE);
        Toast.makeText(this,R.string.detenida,Toast.LENGTH_SHORT).show();
    }

    public void mostrarMapa(View view) {
        if(!lista.isEmpty()){
            Intent i = new Intent(this, ActividadMapa.class);
            startActivity(i);
        }
    }

    public void insertar() {
        Date d = Calendar.getInstance().getTime();
        Ruta ruta = new Ruta(d, lista);
        bd.store(ruta);
        bd.commit();
        Toast.makeText(this, R.string.insertado, Toast.LENGTH_SHORT).show();
        stopService(new Intent(this, ServicioIntent.class));
        lista = new ArrayList<Localizacion>();
        inicializar();
        btSave.setVisibility(View.GONE);
    }

    public void consulta(){
        List<Ruta> rutas = bd.query(Ruta.class);
        Log.v("rutassize",rutas.size()+"");
        for (int i = 0; i < rutas.size(); i++) {
            Log.v("rutas",rutas.get(i).toString());
        }
    }

    public void inicializar(){
        listaRutas.clear();
        List<Ruta> rutas = bd.query(Ruta.class);
        for(Ruta r: rutas){
            listaRutas.add(r);
        }
        ad.notifyDataSetChanged();
    }

    public void guardar(View view){
        insertar();
    }


}
