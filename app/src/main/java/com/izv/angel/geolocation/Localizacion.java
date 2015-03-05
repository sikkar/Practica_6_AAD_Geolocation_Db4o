package com.izv.angel.geolocation;

import android.location.Location;

import java.io.Serializable;
import java.util.Date;


public class Localizacion implements Serializable {

    private Date fecha;
    private double latitud;
    private double longitud;
    private String localidad;
    private String calle;

    public Localizacion(Date fecha, double latitud, double longitud) {
        this.fecha = fecha;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Localizacion(Date fecha, double latitud, double longitud, String localidad, String calle) {
        this.fecha = fecha;
        this.latitud = latitud;
        this.longitud = longitud;
        this.localidad = localidad;
        this.calle = calle;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
}
