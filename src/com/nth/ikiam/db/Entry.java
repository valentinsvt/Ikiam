package com.nth.ikiam.db;

import android.content.Context;

import java.util.HashMap;
import java.util.List;

/**
 * Created by DELL on 31/07/2014.
 */
public class Entry {

    public long id;
    public String fecha;

    public Long especie_id;
    public String comentarios;

    public int uploaded = 0;
    Context context;
    EntryDbHelper entryDbHelper;

    public Entry(Context context, Especie especie, String comentarios) {
        this.especie_id = especie.id;
        this.comentarios = comentarios;
        entryDbHelper = new EntryDbHelper(context);
        this.context = context;
    }

    public Entry(Context context) {
        entryDbHelper = new EntryDbHelper(context);
        this.context = context;
    }

    //getters
    public long getId() {
        return id;
    }

    public String getFecha() {
        return fecha;
    }

    public Especie getEspecie(Context context) {
        if (especie_id != null) {
            return Especie.get(context, especie_id);
        } else {
            return null;
        }
    }

    public Long getEspecie_id() {
        return especie_id;
    }

    public String getComentarios() {
        return comentarios;
    }

    public int getUploaded() {
        return uploaded;
    }

    //setters
    public void setId(long id) {
        this.id = id;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setEspecie(Especie especie) {
        this.especie_id = especie.id;
    }

    public void setEspecie_id(Long especie_id) {
        this.especie_id = especie_id;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public void setUploaded(int uploaded) {
        this.uploaded = uploaded;
    }

    public void save() {
        if (this.id == 0) {
            this.id = this.entryDbHelper.createEntry(this);
        } else {
            this.entryDbHelper.updateEntry(this);
        }
    }

    public void delete() {
        this.entryDbHelper.deleteEntry(this);
    }

    public static Entry get(Context context, long id) {
        EntryDbHelper e = new EntryDbHelper(context);
        return e.getEntry(id);
    }

    public static List<Entry> list(Context context) {
        EntryDbHelper e = new EntryDbHelper(context);
        return e.getAllEntries();
    }

    public static int count(Context context) {
        EntryDbHelper e = new EntryDbHelper(context);
        return e.countAllEntries();
    }

    public static int countByEspecie(Context context, Especie especie) {
        EntryDbHelper e = new EntryDbHelper(context);
        return e.countEntriesByEspecie(especie);
    }

    public static int countNotUploaded(Context context) {
        EntryDbHelper e = new EntryDbHelper(context);
        return e.countEntriesByUploaded(0);
    }

    public static List<Entry> findAllByEspecie(Context context, Especie especie) {
        EntryDbHelper e = new EntryDbHelper(context);
        return e.getAllEntriesByEspecie(especie);
    }

    public static List<Entry> findAllByEspecie(Context context, long especie) {
        EntryDbHelper e = new EntryDbHelper(context);
        return e.getAllEntriesByEspecie(especie);
    }

    public static List<Entry> findAllByEspecieIsNull(Context context) {
        EntryDbHelper e = new EntryDbHelper(context);
        return e.getAllEntriesByEspecieIsNull();
    }

    public static List<Entry> findAllNotUploaded(Context context) {
        EntryDbHelper e = new EntryDbHelper(context);
        return e.getAllEntriesByUploaded(0);
    }

    public static void empty(Context context) {
        EntryDbHelper e = new EntryDbHelper(context);
        e.deleteAllEntries();
    }

    public static List<Entry> busqueda(Context context, HashMap<String, String> data) {
        EntryDbHelper e = new EntryDbHelper(context);
        return e.getBusqueda(data);
    }
}
