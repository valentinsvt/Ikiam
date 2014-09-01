package com.nth.ikiam.utils;

import android.graphics.Bitmap;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.image.ImageUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;

/**
 * Created by Svt on 8/31/2014.
 */
public class EspeciesDownloader implements Runnable  {
    private MapActivity context;
    private ExecutorService queue;
    private int retries;
    //    final String IP = "http://www.tedein.com.ec:8080/ikiamServer/";
    final String IP = UtilsUploaders.getIp();


    public EspeciesDownloader(MapActivity context, ExecutorService queue, int retries) {
        this.context = context;
        this.queue = queue;
        this.retries = retries;
    }

    @Override
    public void run() {
        String urlstr = IP + "especie/getEspecies";


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

                String[] parts = response.toString().split("&");
                System.out.println("parts "+parts);
                for (int i = 0; i < parts.length; i++) {
                    String[] datos = parts[i].split(";");
                    System.out.println("datos "+datos+" "+datos.length);
                    try {
                        if (datos.length == 7) {
                            System.out.println("url foto "+(IP+"uploaded/android/"+datos[2]));
                            URL urlFoto = new URL(IP +"uploaded/android/"+ datos[2]);
                            HttpURLConnection connection = (HttpURLConnection) urlFoto.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            Bitmap[] myBitmap = ImageUtils.dobleBitmap(input, 80, 45, context.screenWidth, 300);
                            int likes = Integer.parseInt(datos[6]);
                            double lat = Double.parseDouble(datos[3]);
                            double longi = Double.parseDouble(datos[4]);
                            context.setPingEspecie(datos[0], likes, lat, longi, myBitmap[0], myBitmap[1], datos[1]);
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
