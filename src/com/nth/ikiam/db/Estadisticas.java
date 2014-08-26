package com.nth.ikiam.db;

import android.content.Context;

/**
 * Created by DELL on 25/08/2014.
 */
public class Estadisticas {
    public long id = 0;
    public String fecha;

    Double distanciaRecorrida;
    Integer fotosTomadas;
    Integer fotosSubidas;

    EstadisticasDbHelper estadisticasDbHelper;

    public Estadisticas(Context context) {
        estadisticasDbHelper = new EstadisticasDbHelper(context);
    }

    public long getId() {
        return id;
    }

    public String getFecha() {
        return fecha;
    }

    public Double getDistanciaRecorrida() {
        return distanciaRecorrida;
    }

    public Integer getFotosTomadas() {
        return fotosTomadas;
    }

    public Integer getFotosSubidas() {
        return fotosSubidas;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setDistanciaRecorrida(Double distanciaRecorrida) {
        this.distanciaRecorrida = distanciaRecorrida;
    }

    public void setFotosTomadas(Integer fotosTomadas) {
        this.fotosTomadas = fotosTomadas;
    }

    public void setFotosSubidas(Integer fotosSubidas) {
        this.fotosSubidas = fotosSubidas;
    }
}
