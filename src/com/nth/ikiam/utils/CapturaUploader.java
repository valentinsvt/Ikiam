package com.nth.ikiam.utils;

import android.content.Context;
import android.util.Log;
import com.nth.ikiam.db.Especie;
import com.nth.ikiam.db.Familia;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.db.Genero;
import com.nth.ikiam.image.ImageItem;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;

/**
 * Created by DELL on 31/07/2014.
 */
public class CapturaUploader implements Runnable {

    /**
     * Application context
     */
    private Context context;

    /**
     * Queue that handles image uploads
     */
    private ExecutorService queue;

    /**
     * Queue items
     */
    Foto foto;

    /**
     * Number of retries for failed uploads
     */
    private int retries;

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    /**
     * Constructor
     *
     * @param context Application context
     * @param queue   Queue that handles image uploads
     * @param foto    Foto queue item
     * @param retries Number of retries for failed uploads
     */
    public CapturaUploader(Context context, ExecutorService queue, Foto foto, int retries) {
        this.context = context;
        this.queue = queue;
        this.foto = foto;
        this.retries = retries;
    }

    /**
     * Upload image
     */
    public void run() {
        System.out.println("run del upload");
        String urlstr = "http://192.168.1.129:8080/ikiamServer/nthServer/uploadData";

        try {
            // new file and and entity
            File file = new File(foto.path);
//            System.out.println("PATH::: " + foto.path);
//            System.out.println("FILE::: " + file);
//            System.out.println("EXISTE::: " + file.exists());
            DataOutputStream dos = null;
            HttpURLConnection conn = null;
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            FileInputStream fileInputStream = new FileInputStream(file);
            URL url = new URL(urlstr);
            int serverResponseCode = 0;

            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setChunkedStreamingMode(0); // when the body length is NOT known in advance,
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", foto.path);

//            conn.setRequestProperty("fecha", foto.fecha);
//            conn.setRequestProperty("keywords", foto.keywords);
//
//            conn.setRequestProperty("familia", "" + foto.especie.genero.familia.nombre);
//            conn.setRequestProperty("genero", "" + foto.especie.genero.nombre);
//            conn.setRequestProperty("especie", "" + foto.especie.nombre);
//            conn.setRequestProperty("comentarios", "" + foto.entry.comentarios);
//            conn.setRequestProperty("latitud", "" + foto.coordenada.latitud);
//            conn.setRequestProperty("longitud", "" + foto.coordenada.longitud);

            dos = new DataOutputStream(conn.getOutputStream());

//            dos.writeBytes(twoHyphens + boundary + lineEnd);
//            dos.writeBytes("Content-Type: text/plain" + lineEnd);
//            dos.writeBytes("Content-Disposition: form-data; name=familia" + lineEnd);
//            dos.writeBytes(lineEnd + foto.especie.genero.familia.nombre + lineEnd);
//            dos.writeBytes(twoHyphens + boundary + lineEnd);
            addFormPart(dos, "familia", foto.especie.genero.familia.nombre);
            addFormPart(dos, "genero", foto.especie.genero.nombre);
            addFormPart(dos, "especie", foto.especie.nombre);
            addFormPart(dos, "archivo", foto.path);
            addFormPart(dos, "keywords", foto.keywords);


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

//            os.write((delimiter + boundary + "\r\n").getBytes());
//            os.write("Content-Type: text/plain\r\n".getBytes());
//            os.write(("Content-Disposition: form-data; name=\"" + paramName + "\"\r\n").getBytes());
//            os.write(("\r\n" + value + "\r\n").getBytes());

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            if (serverResponseCode == 200) {
                System.out.print("completed");
//                foto.uploaded = 1;
//                foto.entry.uploaded = 1;
//                foto.save();
//                foto.entry.save();
            }

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (Exception e) {
            System.out.println("fail upload " + e.getMessage());
            // file upload failed so abort post and close connection
        }
    }

    private void addFormPart(DataOutputStream dos, String paramName, String value) throws Exception {
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Type: text/plain" + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=" + paramName + lineEnd);
        dos.writeBytes(lineEnd + value + lineEnd);
        dos.writeBytes(twoHyphens + boundary + lineEnd);
    }

}

