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
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(960,
                MeasureSpec.AT_MOST);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(600,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
