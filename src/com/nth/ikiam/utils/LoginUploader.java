package com.nth.ikiam.utils;

import android.content.SharedPreferences;
import com.nth.ikiam.MapActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;

/**
 * Created by Svt on 8/11/2014.
 */
public class LoginUploader implements Runnable {
    private MapActivity context;
    private ExecutorService queue;
    private int retries;
    final String IP = UtilsUploaders.getIp();
    //    final String IP = "http://www.tedein.com.ec:8080/ikiamServer/";
    //final String IP = "http://192.168.1.117:8080/ikiamServer/";
    //final String IP = "http://10.0.0.3:8080/ikiamServer/";
    String email;
    String id;
    String nombre;
    String apellido;
    String pass;
    String tipo;

    public LoginUploader(MapActivity context, ExecutorService queue, int retries, String email, String pass) {
        this.context = context;
        this.queue = queue;
        this.retries = retries;
        this.email = email;
        this.pass = pass;
    }

    @Override
    public void run() {
        String urlstr = IP + "userServer/login";


        try {
            DataOutputStream dos = null;
            HttpURLConnection conn = null;

            URL url = new URL(urlstr);

            int serverResponseCode = 0;
            String parameters = "email=" + email + "&pass=" + pass;
            conn = (HttpURLConnection) url.openConnection();
            System.out.println("upload login " + urlstr + "  " + parameters.getBytes().length);
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
                System.out.println("response login!!  " + response.toString());
                if (response.toString().trim().equals("Error usuario no encontrado")) {
                    context.setErrorMessage(response.toString());
                } else {
                    String[] parts = response.toString().split(";");
                    System.out.println("parts " + parts);
                    //""+usu.id+";"+usu.email+";"+usu.nombre+";"+usu.apellido+";"+usu.esCientifico
                    SharedPreferences settings = context.getSharedPreferences(context.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("user", parts[0]);
                    editor.putString("email", parts[1]);
                    editor.putString("name", parts[2] + " " + parts[3]);
                    editor.putString("type", "ikiam");
                    editor.putString("esCientifico", parts[4]);
                    editor.commit();
                    context.userId = parts[0];
                    context.name = parts[2] + " " + parts[3];
                    context.email = parts[1];
                    context.esCientifico = parts[4];
                    context.setType("ikiam");
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
