package com.nth.ikiam.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.nth.ikiam.MapActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
public class UsuarioUploader  implements Runnable  {
    private MapActivity context;

    /**
     * Queue that handles image uploads
     */
    private ExecutorService queue;
    private int retries;
    final String IP = "http://www.tedein.com.ec:8080/ikiamServer/";
    //final String IP = "http://192.168.1.117:8080/ikiamServer/";
    //final String IP = "http://10.0.0.3:8080/ikiamServer/";
    String email;
    String id;
    String nombre;
    String apellido;
    String pass;
    String tipo;

    public UsuarioUploader(MapActivity context, ExecutorService queue,  int retries,String email,String nombre,String apellido,String pass,String tipo) {
        this.context = context;
        this.queue = queue;
        this.retries = retries;
        this.email=email;
        this.nombre=nombre;
        this.apellido=apellido;
        this.pass=pass;
        this.tipo=tipo;
    }
    public void run() {

        String urlstr = IP+"userServer/createUser";

        //System.out.println("run del upload "+urlstr);
        try {

            DataOutputStream dos = null;
            HttpURLConnection conn = null;

            URL url = new URL(urlstr);
            int serverResponseCode = 0;
            String parameters = "email="+email+"&nombre="+nombre+"&apellido="+apellido+"&tipo="+tipo+"&pass="+pass;
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", "" +Integer.toString(parameters.getBytes().length));
            conn.setRequestProperty("Content-Language", "en-US");
            conn.setUseCaches (false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            dos = new DataOutputStream (
                    conn.getOutputStream ());
            dos.writeBytes (parameters);
            dos.flush ();
            dos.close ();

            //Get Response
            InputStream is = conn.getInputStream();
            serverResponseCode = conn.getResponseCode();

            System.out.println("response code "+serverResponseCode);
            if (serverResponseCode == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                System.out.println("response!!  "+response.toString());
                if(isInteger(response.toString().trim())){
                    this.id=response.toString();
                    context.name=this.nombre+" "+this.apellido;
                    context.email=this.email;
                    context.setUserId(response.toString());
                    context.setType("ikiam");
                    System.out.println("nombre "+context.name+"  email "+context.email+"  id "+context.userId);


                }else{
                    context.setErrorMessage(response.toString());

                }
            }



            dos.flush();
            dos.close();


        }catch (Exception e){
            System.out.println("error upload usuario "+e.getMessage());
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {

            return false;
        }
        // only got here if we didn't return false

        return true;
    }



}
