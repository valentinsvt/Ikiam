package com.nth.ikiam.utils;

import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Coordenada;
import com.nth.ikiam.db.Entry;
import com.nth.ikiam.db.Especie;
import com.nth.ikiam.db.Foto;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

/**
 * Created by DELL on 27/08/2014.
 */
public class FotoEspecieDownloader implements Runnable {
    private MapActivity context;
    private ExecutorService queue;
    private int retries;

    Vector<String> fotosPaths;
    Vector<String> fotosLats;
    Vector<String> fotosLongs;
    Vector<String> fotosAlts;
    Vector<String> fotosComs;
    Vector<String> fotosKeys;
    Especie especie;

    final String IP = UtilsUploaders.getIp();

    public FotoEspecieDownloader(MapActivity context, ExecutorService queue, int retries, Vector<String> fotosPaths,
                                 Vector<String> fotosLats, Vector<String> fotosLongs, Vector<String> fotosAlts, Vector<String> fotosComs,
                                 Vector<String> fotosKeys, Especie especie) {
        this.context = context;
        this.queue = queue;
        this.retries = retries;
        this.fotosPaths = fotosPaths;
        this.fotosLats = fotosLats;
        this.fotosLongs = fotosLongs;
        this.fotosAlts = fotosAlts;
        this.fotosComs = fotosComs;
        this.fotosKeys = fotosKeys;
        this.especie = especie;
    }

    @Override
    public void run() {
        try {
            Integer i = 0;
            for (String fotoPath : fotosPaths) {
                URL urlFoto = new URL(IP + fotoPath);

                String basePath = Utils.getFolder(context) + "/";
                String[] parts = fotoPath.split("/");
                String fileName = basePath + parts[parts.length - 1];
                File file = new File(fileName);

                long startTime = System.currentTimeMillis();
                System.out.println("Starting download......from " + urlFoto);
                URLConnection ucon = urlFoto.openConnection();
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                    /*
                     * Read bytes to the Buffer until there is nothing more to read(-1).
                     */
                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }

                if (!file.exists()) {

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(baf.toByteArray());
                    fos.close();
                    System.out.println("Download Completed in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec to " + fileName);

                    double lat = fotosLats.get(i).equals("null") ? 0 : Double.parseDouble(fotosLats.get(i));
                    double lon = fotosLongs.get(i).equals("null") ? 0 : Double.parseDouble(fotosLongs.get(i));
                    double alt = fotosAlts.get(i).equals("null") ? 0 : Double.parseDouble(fotosAlts.get(i));

                    String com = fotosComs.get(i).equals("null") ? "" : fotosComs.get(i);
                    String keywords = fotosKeys.get(i);

                    List<Coordenada> coords = Coordenada.findAllByCoords(context, lat, lon, alt);
                    Coordenada nuevaCoord;
                    if (coords.size() > 0) {
                        nuevaCoord = coords.get(0);
                    } else {
                        nuevaCoord = new Coordenada(context);
                        nuevaCoord.altitud = alt;
                        nuevaCoord.latitud = lat;
                        nuevaCoord.longitud = lon;
                        nuevaCoord.save();
                    }

                    Entry nuevoEntry = new Entry(context);
                    nuevoEntry.setEspecie(especie);
                    nuevoEntry.uploaded = 1;
                    nuevoEntry.comentarios = com;
                    nuevoEntry.save();

                    Foto nuevaFoto = new Foto(context);
                    nuevaFoto.setCoordenada(nuevaCoord);
                    nuevaFoto.setEntry(nuevoEntry);
                    nuevaFoto.setEspecie(especie);
                    nuevaFoto.setKeywords(keywords);
                    nuevaFoto.path = fileName;
                    nuevaFoto.uploaded = 1;
                    nuevaFoto.mio = 0;
                    nuevaFoto.save();

                    context.showToast(context.getString(R.string.descarga_busqueda_download_foto_ok, especie.nombreComun));
                } else {
                    System.out.println("File " + fileName + " exists");
                    context.showToast(context.getString(R.string.descarga_busqueda_download_foto_ok, especie.nombreComun));
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
