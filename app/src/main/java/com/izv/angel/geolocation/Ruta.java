package com.izv.angel.geolocation;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


public class Ruta implements Serializable {

    private Date fechaRuta;
    private ArrayList<Localizacion> localizaciones;

    public Ruta(Date fechaRuta, ArrayList<Localizacion> localizaciones) {
        this.fechaRuta = fechaRuta;
        this.localizaciones = localizaciones;
    }

    public Date getFechaRuta() {
        return fechaRuta;
    }

    public void setFechaRuta(Date fechaRuta) {
        this.fechaRuta = fechaRuta;
    }

    public ArrayList<Localizacion> getLocalizaciones() {
        return localizaciones;
    }


    public void setLocalizaciones(ArrayList<Localizacion> localizaciones) {
        this.localizaciones = localizaciones;
    }

    @Override
    public String toString() {
        return "Ruta{" +
                "fechaRuta=" + fechaRuta +
                ", localizaciones=" + localizaciones.size() +
                '}';
    }


}
