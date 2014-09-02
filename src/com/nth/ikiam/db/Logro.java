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

    public void save() {
        if (this.id == 0) {
            this.id = this.logroDbHelper.createLogro(this);
        } else {
            this.logroDbHelper.updateLogro(this);
        }
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

    public static List<Logro> list(Context context) {
        LogroDbHelper e = new LogroDbHelper(context);
        return e.getAllLogros();
    }
    public static List<Logro> findAllByTipoAndNotCompleto(Context context, int tipo) {
        LogroDbHelper e = new LogroDbHelper(context);
        return e.getAllLogrosByTipoAndNotCompleto(tipo);
    }
    public static List<Logro> findAllCompletos(Context context) {
        LogroDbHelper e = new LogroDbHelper(context);
        return e.getAllLogrosCompletos();
    }

    public static int countCompletos(Context context) {
        LogroDbHelper e = new LogroDbHelper(context);
        return e.countLogrosCompletos();
    }

    public static int count(Context context) {
        LogroDbHelper e = new LogroDbHelper(context);
        return e.countLogros();
    }
}
