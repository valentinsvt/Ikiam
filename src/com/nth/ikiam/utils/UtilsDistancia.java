package com.nth.ikiam.utils;

import android.content.Context;
import com.nth.ikiam.db.Coordenada;
import com.nth.ikiam.db.Ruta;

import java.util.List;

/**
 * Created by luz on 02/09/14.
 */
public class UtilsDistancia {
    public static double calculaDistancia(Context activity, Ruta ruta) {
        List<Coordenada> cords = Coordenada.findAllByRuta(activity, ruta);
        double distancia = 0;
        double alturaMinima = 0;
        double alturaMaxima = 0;
        if (cords.size() > 2) {
            for (int i = 0; i < cords.size() - 1; i++) {
                Coordenada current = cords.get(i);
                Coordenada next = cords.get(i + 1);
                if (i == 0) {
                    alturaMinima = current.altitud;
                }

                if (alturaMinima > current.altitud)
                    alturaMinima = current.altitud;
                if (alturaMaxima < current.altitud)
                    alturaMaxima = current.altitud;
                if (alturaMinima > next.altitud)
                    alturaMinima = next.altitud;
                if (alturaMaxima < next.altitud)
                    alturaMaxima = next.altitud;
                distancia += dist(current.getLatitud(), current.getLongitud(), next.getLatitud(), next.getLongitud());
            }
        }
        distancia = distancia * 1000; /*para sacar en metros*/
        distancia = Math.round(distancia * 100.0) / 100.0;
        return distancia;
    }

    public static double dist(double lat1, double long1, double lat2, double long2) {
        double R = 6378.137;                          //Radio de la tierra en km
        double dLat = radianes(lat2 - lat1);
        double dLong = radianes(long2 - long1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(radianes(lat1)) * Math.cos(radianes(lat2)) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;                      //Retorna tres decimales
    }

    public static double radianes(double x) {
        return x * Math.PI / 180;
    }
}
