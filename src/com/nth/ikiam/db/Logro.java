package com.nth.ikiam.db;

import android.content.Context;

import java.util.List;

/**
 * Created by DELL on 25/08/2014.
 */
public class Logro {
    public long id = 0;
    public String fecha;

    public Integer codigo;
    public Double cantidad;
    public Integer completo;

    LogroDbHelper logroDbHelper;

    public Logro(Context context) {
        logroDbHelper = new LogroDbHelper(context);
    }

    public long getId() {
        return id;
    }

    public String getFecha() {
        return fecha;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public Integer completo() {
        return completo;
    }

    public Boolean isCompleto() {
        return completo == 1;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public void setCompleto(Integer completo) {
        this.completo = completo;
    }

    public List<Logro> findAllByTipoAndNotCompleto(Context context, int tipo) {
        LogroDbHelper e = new LogroDbHelper(context);
        return e.getAllLogrosByTipoAndNotCompleto(tipo);
    }
}
