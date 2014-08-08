package com.nth.ikiam.db;

import android.content.Context;

import java.util.List;

/**
 * Created by DELL on 26/07/2014.
 */
public class Foto {
    public long id = 0;
    public String fecha;
    //    public Especie especie;
//    public Coordenada coordenada;
    public String keywords;
//    public Entry entry;

    public Long especie_id;
    public Long coordenada_id;
    public Long entry_id;
    public Long ruta_id;

    public String path;

    public int uploaded;

    Context context;

    FotoDbHelper fotoDbHelper;

    public Foto(Context context) {
        fotoDbHelper = new FotoDbHelper(context);
    }

    public Foto(Context context, Especie especie, String comentarios, String keywords, Coordenada coordenada) {
        this.coordenada_id = coordenada.id;
        this.keywords = keywords;
        this.especie_id = especie.id;
        this.context = context;
        fotoDbHelper = new FotoDbHelper(context);
    }

    public Foto(Context context, Especie especie, String comentarios, String keywords) {
        this.keywords = keywords;
        this.especie_id = especie.id;
        this.context = context;
        fotoDbHelper = new FotoDbHelper(context);
    }

    public Foto(Context context, Especie especie, String comentarios) {
        this.especie_id = especie.id;
        this.context = context;
        fotoDbHelper = new FotoDbHelper(context);
    }

    public Foto(Context context, Especie especie) {
        this.especie_id = especie.id;
        this.context = context;
        fotoDbHelper = new FotoDbHelper(context);
    }

    //getters
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

    public long getId() {
        return id;
    }

    public Coordenada getCoordenada(Context context) {
        if (coordenada_id != null) {
            return Coordenada.get(context, coordenada_id);
        } else {
            return null;
        }
    }

    public Long getCoordenada_id() {
        return coordenada_id;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getFecha() {
        return fecha;
    }

    public String getPath() {
        return path;
    }

    public Entry getEntry(Context context) {
        if (entry_id != null) {
            return Entry.get(context, entry_id);
        } else {
            return null;
        }
    }

    public Long getEntry_id() {
        return entry_id;
    }

    public int getUploaded() {
        return uploaded;
    }

    public Ruta getRuta(Context context) {
        if (ruta_id != null) {
            return Ruta.get(context, this.ruta_id);
        } else {
            return null;
        }
    }

    public long getRutaId() {
        return this.ruta_id;
    }

    //setter
    public void setEspecie(Especie especie) {
        this.especie_id = especie.id;
    }

    public void setEspecie_id(Long especie_id) {
        this.especie_id = especie_id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCoordenada(Coordenada coordenada) {
        this.coordenada_id = coordenada.id;
    }

    public void setCoordenada_id(Long coordenada_id) {
        this.coordenada_id = coordenada_id;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setEntry(Entry entry) {
        this.entry_id = entry.id;
    }

    public void setEntry_id(Long entry_id) {
        this.entry_id = entry_id;
    }

    public void setUploaded(int uploaded) {
        this.uploaded = uploaded;
    }

    public void setRuta(Ruta ruta) {
        this.ruta_id = ruta.id;
    }

    public void setRuta_id(long id) {
        this.ruta_id = id;
    }

    public void save() {
        if (this.id == 0) {
            this.id = this.fotoDbHelper.createFoto(this);
        } else {
            this.fotoDbHelper.updateFoto(this);
        }
    }

    public void delete() {
        this.fotoDbHelper.deleteFoto(this);
    }

    public static Foto get(Context context, long id) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getFoto(id);
    }

    public static List<Foto> list(Context context) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getAllFotos();
    }

    public static int count(Context context) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.countAllFotos();
    }

    public static int countByEspecie(Context context, Especie especie) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.countFotosByEspecie(especie);
    }

    public static int countNotUploaded(Context context) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.countFotosByUploaded(0);
    }

    public static List<Foto> findAllByEspecie(Context context, Especie especie) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getAllFotosByEspecie(especie);
    }

    public static List<Foto> findAllByEntry(Context context, Entry entry) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getAllFotosByEntry(entry);
    }

    public static List<Foto> findAllByKeyword(Context context, String keyword) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getAllFotosByKeyword(keyword);
    }

    public static List<Foto> findAllNotUploaded(Context context) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getAllFotosByUploaded(0);
    }

    public static void empty(Context context) {
        FotoDbHelper e = new FotoDbHelper(context);
        e.deleteAllFotos();
    }

}
