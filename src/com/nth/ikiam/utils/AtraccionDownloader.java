package com.nth.ikiam.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.image.ImageUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;

public class AtraccionDownloader implements Runnable {
    private MapActivity context;
    private ExecutorService queue;
    private int retries;
    final String IP = "http://www.tedein.com.ec:8080/ikiamServer/";

    public AtraccionDownloader(MapActivity context, ExecutorService queue, int retries) {
        this.context = context;
        this.queue = queue;
        this.retries = retries;
    }

    @Override
    public void run() {
        String urlstr = IP + "atraccionTuristica/atracciones";


        try {
            DataOutputStream dos = null;
            HttpURLConnection conn = null;

            URL url = new URL(urlstr);

            int serverResponseCode = 0;
            String parameters = "key=nth";
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

                String[] parts = response.toString().split(";");
                for (int i = 0; i < parts.length; i++) {
                    String[] datos = parts[i].split("&");

                    try {
                        if (datos.length == 6) {
                            //System.out.println("url foto "+(IP+datos[4]));
                            URL urlFoto = new URL(IP + datos[4]);
                            HttpURLConnection connection = (HttpURLConnection) urlFoto.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            Bitmap[] myBitmap = ImageUtils.dobleBitmap(input, 80, 45, context.screenWidth, 300);
                            int likes = Integer.parseInt(datos[1]);
                            double lat = Double.parseDouble(datos[2]);
                            double longi = Double.parseDouble(datos[3]);
                            context.setPing(datos[0], likes, lat, longi, myBitmap[0], myBitmap[1], datos[5]);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }


            dos.flush();
            dos.close();


        } catch (Exception e) {
            System.out.println("error login " + e);
            for (StackTraceElement ste : e.getStackTrace()) {
                System.out.println(ste);
            }
        }

    }
}
