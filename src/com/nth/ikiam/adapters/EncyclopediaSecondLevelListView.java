package com.nth.ikiam.adapters;

import android.content.Context;
import android.widget.ExpandableListView;
import com.nth.ikiam.db.Especie;

import java.util.List;

/**
 * Created by luz on 01/08/14.
 */
public class EncyclopediaSecondLevelListView extends ExpandableListView {

    int intGroupPosition, intChildPosition, intGroupid;
    List<Especie> especies;

    public EncyclopediaSecondLevelListView(Context context) {
        super(context);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //System.out.println("<<<<<<<<<measure>>>>>>>>  "+especies);
        int h = 600;
        if(especies!=null){
            if(especies.size()>0)
                h=especies.size()*220;
        }
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(960, MeasureSpec.AT_MOST);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.AT_MOST);
        //System.out.println("****************" + heightMeasureSpec + "*********************************");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
