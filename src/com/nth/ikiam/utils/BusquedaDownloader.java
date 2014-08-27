package com.nth.ikiam.utils;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import com.nth.ikiam.MapActivity;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;

/**
 * Created by DELL on 26/08/2014.
 */
public class BusquedaDownloader implements Runnable {
    private MapActivity context;
    private ExecutorService queue;
    private int retries;
    final String IP = UtilsUploaders.getIp();

    String nombreComun;
    String nombreFamilia;
    String nombreGenero;
    String nombreEspecie;

    ProgressDialog progressDialog;

    public BusquedaDownloader(MapActivity context, ExecutorService queue, int retries, String nombreComun,
                              String nombreFamilia, String nombreGenero, String nombreEspecie, ProgressDialog progressDialog) {
        this.context = context;
        this.queue = queue;
        this.retries = retries;
        this.nombreComun = nombreComun;
        this.nombreFamilia = nombreFamilia;
        this.nombreGenero = nombreGenero;
        this.nombreEspecie = nombreEspecie;
        this.progressDialog = progressDialog;
    }

    @Override
    public void run() {
        String urlstr = IP + "busquedaDescarga/buscaEspecies";
        try {

            URL url = new URL(urlstr);

            DataOutputStream dos = null;
            HttpURLConnection conn = null;
            int serverResponseCode = 0;

//            String parameters = "email=" + email + "&pass=" + pass;
            String parameters = "";
            if (nombreComun != null) {
                parameters += "comun=" + nombreComun;
            }
            if (nombreFamilia != null) {
                if (!parameters.equals("")) {
                    parameters += "&";
                }
                parameters += "familia=" + nombreFamilia;
            }
            if (nombreGenero != null) {
                if (!parameters.equals("")) {
                    parameters += "&";
                }
                parameters += "genero=" + nombreGenero;
            }
            if (nombreEspecie != null) {
                if (!parameters.equals("")) {
                    parameters += "&";
                }
                parameters += "especie=" + nombreEspecie;
            }

            // Open a HTTP  connection to  the URLF
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
            conn.setRequestProperty("Content-Language", "en-US");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            dos = new DataOutputStream(
                    conn.getOutputStream());
            dos.writeBytes(parameters);
            dos.flush();
            dos.close();

            //Get Response
            InputStream is = conn.getInputStream();
            serverResponseCode = conn.getResponseCode();

            System.out.println("response code " + serverResponseCode);
            if (serverResponseCode == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
//                System.out.println("response busqueda!!  " + response.toString());
                String respuesta = response.toString();
//                System.out.println("RESPONSE " + respuesta);
                context.strEspeciesList = respuesta;
//                progressDialog.hide();
                context.showDownloadedEspecies(respuesta, progressDialog);
                //""+usu.id+";"+usu.email+";"+usu.nombre+";"+usu.apellido+";"+usu.esCientifico
            }
            dos.flush();
            dos.close();

        } catch (Exception e) {
            System.out.println("error busqueda especies " + e);
            for (StackTraceElement ste : e.getStackTrace()) {
                System.out.println(ste);
            }
        }
    }
}
