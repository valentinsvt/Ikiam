package com.nth.ikiam.adapters;

import android.content.Context;
import android.widget.ExpandableListView;

/**
 * Created by luz on 01/08/14.
 */
public class EncyclopediaSecondLevelListView extends ExpandableListView {

    int intGroupPosition, intChildPosition, intGroupid;

    public EncyclopediaSecondLevelListView(Context context) {
        super(context);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        System.out.println("<<<<<<<<<AQUI>>>>>>>>");
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(960, MeasureSpec.AT_MOST);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(1200, MeasureSpec.AT_MOST);
        System.out.println("****************" + heightMeasureSpec + "*********************************");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
