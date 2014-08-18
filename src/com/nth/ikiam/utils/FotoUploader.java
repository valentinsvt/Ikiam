package com.nth.ikiam.utils;

import android.util.Log;
import com.nth.ikiam.MainActivity;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.db.*;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Svt on 8/16/2014.
 */
public class FotoUploader implements Runnable {
    Foto foto;
    MapActivity activity;
    int id;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    final String IP = "http://www.tedein.com.ec:8080/ikiamServer/";
    public FotoUploader(MapActivity context, int id_remoto, Foto foto) {
        this.activity=context;
        this.id=id_remoto;
        this.foto=foto;
    }

    @Override
    public void run() {
        String urlstr = IP+"ruta/fotoUploader";
        try {
            // new file and and entity
            File file = new File(foto.path);
            DataOutputStream dos = null;
            HttpURLConnection conn = null;
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            FileInputStream fileInputStream = new FileInputStream(file);
            URL url = new URL(urlstr);
            int serverResponseCode = 0;

            // Open a HTTP  connection to  the URLF
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setChunkedStreamingMode(0); // when the body length is NOT known in advance,
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", foto.path);

            dos = new DataOutputStream(conn.getOutputStream());
            Coordenada coordenada = null;
            coordenada=foto.getCoordenada(activity);
            addFormPart(dos, "archivo", foto.path);
            if (coordenada != null) {
                addFormPart(dos, "lat", "" + coordenada.latitud);
                addFormPart(dos, "long", "" + coordenada.longitud);
                addFormPart(dos, "alt", "" + coordenada.altitud);
            }
            addFormPart(dos, "ruta", ""+id);


            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=foto-file ; filename=" + foto.path + lineEnd);

            dos.writeBytes(lineEnd);

//            create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();



            if (serverResponseCode == 200) {
                System.out.print("completed "+foto.path);
            } else {
                System.out.println("NOT COMPLETED: " + serverResponseCode + "   " + serverResponseMessage);
            }

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("fail upload " + e.getMessage());
            activity.setErrorMessage(activity.getString(R.string.uploader_upload_error));
            // file upload failed so abort post and close connection
        }
    }

    private void addFormPart(DataOutputStream dos, String paramName, String value) throws Exception {
        if (value != null) {
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Type: text/plain" + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=" + paramName + lineEnd);
            if (!paramName.equals("fecha")) {
                value = URLEncoder.encode(value, "utf-8");
            }
            dos.writeBytes(lineEnd + value + lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
        }
    }
}
