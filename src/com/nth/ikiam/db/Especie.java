package com.nth.ikiam.db;

import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by DELL on 26/07/2014.
 */
public class Especie {
    public long id = 0;
    public String fecha;
    //    public Genero genero;
    public Long genero_id;
    public String nombre;
    public String nombreComun;
    public String comentarios;

    //    public Color color1;
//    public Color color2;
    public Long color1_id;
    public Long color2_id;

    EspecieDbHelper especieDbHelper;

    Context context;

    public Especie(Context context) {
        especieDbHelper = new EspecieDbHelper(context);
        this.context = context;
    }

    public Especie(Context context, String nombreComun) {
        this.nombreComun = nombreComun;
        this.context = context;
        especieDbHelper = new EspecieDbHelper(context);
    }

    public Especie(Context context, String nombreEspecie, int algo) {
        this.nombre = nombreEspecie;
        this.context = context;
        especieDbHelper = new EspecieDbHelper(context);
    }

    public Especie(Context context, String nombreComun, Genero genero, String nombre, String comentarios) {
        this.nombre = nombre;
        this.nombreComun = nombreComun;
        this.genero_id = genero.id;
        this.comentarios = comentarios;
        this.context = context;
        especieDbHelper = new EspecieDbHelper(context);
    }

    public Especie(Context context, Genero genero, String nombre, String comentarios) {
        this.nombre = nombre;
        this.genero_id = genero.id;
        this.comentarios = comentarios;
        this.context = context;
        especieDbHelper = new EspecieDbHelper(context);
    }

    public Especie(Context context, Genero genero, String nombre) {
        this.nombre = nombre;
        this.genero_id = genero.id;
        this.context = context;
        especieDbHelper = new EspecieDbHelper(context);
    }

    //getters
    public long getId() {
        return id;
    }

    public String getNombreComun() {
        return nombreComun;
    }

    public Genero getGenero() {
        return Genero.get(context, genero_id);
    }

    public long getGenero_id() {
        return genero_id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getComentarios() {
        return comentarios;
    }

    public String getFecha() {
        return fecha;
    }

    public Color getColor1() {
        return Color.get(context, color1_id);
    }

    public Color getColor2() {
        return Color.get(context, color2_id);
    }

    public long getColor1_id() {
        return color1_id;
    }

    public long getColor2_id() {
        return color2_id;
    }

    public String getNombreCientifico() {
        return getGenero().nombre + " " + nombre;
    }

    //setters
    public void setId(long id) {
        this.id = id;
    }

    public void setNombreComun(String nombreComun) {
        this.nombreComun = nombreComun;
    }

    public void setGenero_id(long genero) {
        this.genero_id = genero;
    }

    public void setGenero(Genero genero) {
        this.genero_id = genero.id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setColor1_id(long color1) {
        this.color1_id = color1;
    }

    public void setColor2_id(long color2) {
        this.color2_id = color2;
    }

    public void setColor1(Color color1) {
        this.color1_id = color1.id;
    }

    public void setColor2(Color color2) {
        this.color2_id = color2.id;
    }

    public void save() {
        if (this.id == 0) {
            this.id = this.especieDbHelper.createEspecie(this);
        } else {
            this.especieDbHelper.updateEspecie(this);
        }
    }

    public static Especie get(Context context, long id) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        return e.getEspecie(id);
    }

    public static Especie getByNombreOrCreate(Context context, String nombreEspecie) {
        Especie especie;
        List<Especie> listEspecies = findAllByNombre(context, nombreEspecie);
        if (listEspecies.size() == 0) {
            especie = new Especie(context, nombreEspecie, 1);
            especie.save();
        } else if (listEspecies.size() == 1) {
            especie = listEspecies.get(0);
        } else {
            Log.e("getByNombreOrCreate especie", "Se encontraron " + listEspecies.size() + " especies con nombre " + nombreEspecie);
            especie = listEspecies.get(0);
        }
        return especie;
    }

    public static List<Especie> list(Context context) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        return e.getAllEspecies();
    }

    public static List<Especie> findAllByColor(Context context, Color color) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        return e.getAllEspeciesByColor(color);
    }

    public static List<Especie> findAllByGenero(Context context, Genero genero) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        return e.getAllEspeciesByGenero(genero);
    }

    public static List<Especie> findAllByNombre(Context context, String especie) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        return e.getAllEspeciesByNombre(especie);
    }

    public static int count(Context context) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        return e.countAllEspecies();
    }

    public static int countByColor(Context context, Color color) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        return e.countEspeciesByColor(color);
    }

    public static int countByGenero(Context context, Genero genero) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        return e.countEspeciesByGenero(genero);
    }

    public static int countByNombre(Context context, String especie) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        return e.countEspeciesByNombre(especie);
    }

    public static void empty(Context context) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        e.deleteAllEspecies();
    }


}
