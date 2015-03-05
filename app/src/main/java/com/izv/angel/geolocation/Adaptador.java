package com.izv.angel.geolocation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class Adaptador extends ArrayAdapter<Ruta> {

    private Context contexto;
    private ArrayList<Ruta> lista;
    private int recurso;
    private LayoutInflater i;

    public class ViewHolder {
        public TextView tv1;
        public int posicion;
    }

    public Adaptador(Context context, int resource, ArrayList <Ruta> objects) {
        super(context, resource, objects);
        this.contexto = context;
        this.lista = objects;
        this.recurso = resource;
        this.i = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = i.inflate(recurso, null);
            vh = new ViewHolder();
            vh.tv1 = (TextView) convertView.findViewById(R.id.tvFecha);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tv1.setText(lista.get(position).getFechaRuta().toString());
        vh.tv1.setTag(position);
        return convertView;
    }
}
