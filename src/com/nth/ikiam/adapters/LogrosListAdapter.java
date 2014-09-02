package com.nth.ikiam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.android.Util;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Logro;
import com.nth.ikiam.utils.Utils;

import java.net.URLDecoder;
import java.util.List;

/**
 * Created by DELL on 03/08/2014.
 */
public class LogrosListAdapter extends ArrayAdapter<Logro> {
    private final MapActivity context;
    private final List<Logro> logros;

    ImageView imageBadge;
    TextView textTitulo;
    TextView textFecha;
    TextView textDescripcion;

    public LogrosListAdapter(MapActivity context, List<Logro> logros) {
        super(context, R.layout.logros_row, logros);
        this.context = context;
        this.logros = logros;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.logros_row, null);
            }

            Logro logro = logros.get(position);

            imageBadge = (ImageView) convertView.findViewById(R.id.logros_badge);
            textTitulo = (TextView) convertView.findViewById(R.id.logros_titulo);
            textFecha = (TextView) convertView.findViewById(R.id.logros_fecha);
            textDescripcion = (TextView) convertView.findViewById(R.id.logros_descripcion);

            int imageId;
            if (logro.completo == 1) {
//                imageBadge.setVisibility(View.VISIBLE);
                textFecha.setVisibility(View.VISIBLE);
                textDescripcion.setVisibility(View.VISIBLE);

                textFecha.setText(logro.fecha);
                textDescripcion.setText(Utils.getPluralResourceByName(context, "achievement_" + logro.codigo,
                        logro.cantidad.intValue(), "" + logro.cantidad.intValue()));

                textTitulo.setTextColor(context.getResources().getColor(R.color.achievement_completed));
                textDescripcion.setTextColor(context.getResources().getColor(R.color.achievement_completed));

                imageId = Utils.getImageResourceByName(context, "ic_achievement_" + logro.codigo + "_" + logro.cantidad.intValue());
                if (imageId == 0) {
                    imageId = Utils.getImageResourceByName(context, "ic_achievement_prize_medal_128");
                }
                RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) imageBadge.getLayoutParams();
                p.height = 300;
                p.width = 300;
                imageBadge.setLayoutParams(p);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(100, 100);
//                imageBadge.setLayoutParams(layoutParams);
            } else {
//                imageBadge.setVisibility(View.GONE);
                textFecha.setVisibility(View.GONE);
                textDescripcion.setVisibility(View.GONE);

                textFecha.setText("");
                textDescripcion.setText("");

                textTitulo.setTextColor(context.getResources().getColor(R.color.achievement_not_completed));
                textDescripcion.setTextColor(context.getResources().getColor(R.color.achievement_not_completed));

                imageId = Utils.getImageResourceByName(context, "ic_achievement_locked");
                RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) imageBadge.getLayoutParams();
                p.height = 120;
                p.width = 120;
                imageBadge.setLayoutParams(p);
            }

            textTitulo.setText(Utils.getStringResourceByName(context, "achievement_" + logro.codigo + "_titulo_" + logro.cantidad.intValue()));

            imageBadge.setImageResource(imageId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
