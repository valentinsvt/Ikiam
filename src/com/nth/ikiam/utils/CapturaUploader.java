package com.nth.ikiam.utils;

import android.util.Log;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.db.*;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;

/**
 * Created by DELL on 31/07/2014.
 */
public class CapturaUploader implements Runnable {

    /**
     * Application context
     */
    private MapActivity context;

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
    public CapturaUploader(MapActivity context, ExecutorService queue, Foto foto, int retries) {
        this.context = context;
        this.queue = queue;
        this.foto = foto;
        this.retries = retries;
    }

    /**
     * Upload image
     */
    public void run() {
        //System.out.println("run del upload");
//        String urlstr = "http://192.168.1.129:8080/ikiamServer/uploadCaptura/uploadData";
//        String urlstr = "http://www.tedein.com.ec:8080/ikiamServer/uploadCaptura/uploadData";
        final String IP = UtilsUploaders.getIp();
        String urlstr = IP + "uploadCaptura/uploadData";

        try {
            FileInputStream fileInputStream = null;
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
            if (file.exists()) {
                fileInputStream = new FileInputStream(file);
            }
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

            Especie especie = null;
            Genero genero = null;
            Familia familia = null;
            Entry entry = null;
            Color color1 = null;
            Color color2 = null;
            Coordenada coordenada = null;

            especie = foto.getEspecie(context);
            entry = foto.getEntry(context);
            coordenada = foto.getCoordenada(context);
            if (especie != null) {
                color1 = especie.getColor1(context);
                color2 = especie.getColor2(context);

                genero = especie.getGenero(context);
                if (genero != null) {
                    familia = genero.getFamilia(context);
                }
            }

            if (familia != null) {
                addFormPart(dos, "familia", familia.nombre);
            }
            if (genero != null) {
                addFormPart(dos, "genero", genero.nombre);
            }

            if (especie != null) {
                addFormPart(dos, "especie", especie.nombre);
                addFormPart(dos, "comun", especie.nombreComun);
            }
            if (color1 != null) {
                addFormPart(dos, "color1", color1.nombre);
            }
            if (color2 != null) {
                addFormPart(dos, "color2", color2.nombre);
            }

            if (entry != null) {
                addFormPart(dos, "comentarios", entry.comentarios);
                addFormPart(dos, "fecha", entry.fecha);
            }

            addFormPart(dos, "cautiverio", "" + entry.cautiverio);

            addFormPart(dos, "keywords", foto.keywords);
            addFormPart(dos, "archivo", foto.path);

            if (coordenada != null) {
                addFormPart(dos, "lat", "" + coordenada.latitud);
                addFormPart(dos, "long", "" + coordenada.longitud);
                addFormPart(dos, "alt", "" + coordenada.altitud);
            }

            addFormPart(dos, "userId", context.userId); //id (faceboook - fb id, ikiam db.id
            addFormPart(dos, "userName", context.name);
            addFormPart(dos, "userType", context.type); //facebook || ikiam
            addFormPart(dos, "userMail", context.email);
            addFormPart(dos, "userCientifico", context.esCientifico); //N || S

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=foto-file ; filename=" + foto.path + lineEnd);

            dos.writeBytes(lineEnd);

            if (file.exists()) {
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
            }
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
                foto.uploaded = 1;
                foto.getEntry(context).uploaded = 1;
                foto.save();
                foto.getEntry(context).save();
                context.setErrorMessage(context.getString(R.string.uploader_upload_success));
                context.updateAchievement(context.ACHIEV_UPLOADS);
                context.checkAchiev(context.ACHIEV_UPLOADS, context.getAchievement(context.ACHIEV_UPLOADS));
            } else {
                System.out.println("NOT COMPLETED: " + serverResponseCode + "   " + serverResponseMessage);
            }

            if (file.exists()) {
                //close the streams //
                fileInputStream.close();
            }
            dos.flush();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("fail upload " + e.getMessage());
            context.setErrorMessage(context.getString(R.string.uploader_upload_error));
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

