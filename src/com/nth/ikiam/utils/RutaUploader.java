package com.nth.ikiam.utils;

import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Coordenada;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.db.Ruta;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RutaUploader implements Runnable {
    private MapActivity context;
    private ExecutorService queue;
    private int retries;
    //    final String IP = "http://www.tedein.com.ec:8080/ikiamServer/";
    final String IP = UtilsUploaders.getIp();
    Ruta ruta;
    List<Foto> fotos;
    List<Coordenada> cords;
    int id_remoto;

    public RutaUploader(MapActivity activity, Ruta ruta, List<Coordenada> cords, List<Foto> fotos) {
        this.context = activity;
        this.ruta = ruta;
        this.cords = cords;
        this.fotos = fotos;
    }

    @Override
    public void run() {
        String urlstr = IP + "ruta/rutaUploader";
        try {
            System.out.println("ruta uploader  usuario " + context.userId);
            if (context.userId.equals("-1") || context.userId == null) {
                context.setErrorMessage(context.getString(R.string.uploader_no_login));
            } else {
                DataOutputStream dos = null;
                HttpURLConnection conn = null;

                URL url = new URL(urlstr);
                int serverResponseCode = 0;
                String parameters = "userId=" + context.userId + "&nombre=" + context.name + "&tipo=+" + context.type + "&ruta=" + ruta.descripcion + "&fecha=" + ruta.fecha + "&coords=";
                String cordsParams = "";
                for (int i = 0; i < cords.size(); i++) {
                    Coordenada current = cords.get(i);
                    cordsParams += current.getLatitud() + ";" + current.getLongitud() + ";" + current.getAltitud() + "|";
                }
                parameters += cordsParams;
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

                // System.out.println("response code "+serverResponseCode);
                if (serverResponseCode == 200) {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    System.out.println("response!! ruta  " + response.toString());
                    if (isInteger(response.toString().trim())) {
                        if (response.toString().trim().equals("-1")) {
                            //context.setErrorMessage("Ha ocurrido un error");
                            System.out.println("me respondio huevadas");
                        } else {
                            if (ruta.idRemoto == null) {
                                id_remoto = Integer.parseInt(response.toString().trim());
                                ruta.idRemoto = response.toString().trim();
                                ruta.save();
                                context.setRuta_remote_id(response.toString().trim());
                            }
                            ExecutorService queue = Executors.newSingleThreadExecutor();
                            for (int i = 0; i < fotos.size(); i++) {
                                queue.execute(new FotoUploader(context, id_remoto, fotos.get(i)));
                            }
                        }

                    } else {
                        System.out.println("me respondio huevadas");
                        // context.setErrorMessage(response.toString());

                    }
                }
                dos.flush();
                dos.close();
            }


        } catch (Exception e) {
            System.out.println("error upload usuario ");
            e.printStackTrace();
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {

            return false;
        }
        // only got here if we didn't return false

        return true;
    }
}
