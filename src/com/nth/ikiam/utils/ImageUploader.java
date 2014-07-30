package com.nth.ikiam.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;

import android.util.Log;
import com.nth.ikiam.image.ImageItem;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

/**
 * Created by svt on 7/30/2014.
 */
public class ImageUploader  implements Runnable {

    /**
     * Application context
     */
    private Context context;

    /**
     *  Queue that handles image uploads
     */
    private ExecutorService queue;

    /**
     * Image queue item
     */
    private ImageItem item;

    /**
     * Number of retries for failed uploads
     */
    private int retries;

    /**
     * Constructor
     *
     * @param context Application context
     * @param queue Queue that handles image uploads
     * @param item Image queue item
     * @param retries Number of retries for failed uploads
     */
    public ImageUploader(Context context, ExecutorService queue, ImageItem item, int retries)
    {
        this.context  = context;
        this.queue    = queue;
        this.item     = item;
        this.retries  = retries;
    }

    /**
     * Upload image to Picasa
     */
    public void run()
    {
        // create items for http client
        System.out.println("run del upload");
        //UploadNotification notification = new UploadNotification(context, item.imageId, item.imageSize, item.imageName);
        String urlstr                      = "http://10.0.0.3:8080/ikiamServer/nthServer/reciveFile";
        //HttpClient client               = new DefaultHttpClient();
        //HttpPost post                   = new HttpPost(url);

        try {
            // new file and and entity
            File file            = new File(item.imagePath);
//            Multipart multipart  = new Multipart("Media multipart posting", "END_OF_PART");
//
//            // create entity parts
//            multipart.addPart("<entry xmlns='http://www.w3.org/2005/Atom'><title>"+item.imageName+"</title><category scheme=\"http://schemas.google.com/g/2005#kind\" term=\"http://schemas.google.com/photos/2007#photo\"/></entry>", "application/atom+xml");
//            multipart.addPart(file, item.imageType);
//
//            // create new Multipart entity
//            MultipartNotificationEntity entity = new MultipartNotificationEntity(multipart, notification);
//
//            // get http params
//            HttpParams params = client.getParams();
//
//            // set protocal and timeout for httpclient
//            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
//            params.setParameter(CoreConnectionPNames.SO_TIMEOUT, new Integer(15000));
//            params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(15000));
//
//            // set body with upload entity
//            post.setEntity(entity);
//
//            // set headers
//            post.addHeader("Authorization", "GoogleLogin auth="+item.imageAuth);
//            post.addHeader("GData-Version", "2");
//            post.addHeader("MIME-version", "1.0");
//
//            // execute upload to picasa and get response and status
//            HttpResponse response = client.execute(post);
//            StatusLine line       = response.getStatusLine();
//
//            // return code indicates upload failed so throw exception
//            if (line.getStatusCode() > 201) {
//                throw new Exception("Failed upload");
//            }
//
//            // shut down connection
//            client.getConnectionManager().shutdown();
//
//            // notify user that file has been uploaded
//            notification.finished();
            DataOutputStream dos = null;
            HttpURLConnection conn = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
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
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", item.imageName);

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name='upload-file' ; filename='prueba.jpg' " + lineEnd);

                    dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
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

            Log.i("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            if(serverResponseCode == 200){

            System.out.print("competed");
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
}

