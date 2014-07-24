package com.nth.ikiam;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by DELL on 23/07/2014.
 */
public class MapFragment extends Fragment implements Button.OnClickListener {
    private Button chooseBtn;
    public MapFragment() {
        // Empty constructor required for fragment subclasses
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_layout, container, false);
        chooseBtn = (Button) view.findViewById(R.id.btnLocate);
        chooseBtn.setOnClickListener(this);
        return view;

    }

    @Override
    public void onClick(View v){
        System.out.println("click");
    }


}