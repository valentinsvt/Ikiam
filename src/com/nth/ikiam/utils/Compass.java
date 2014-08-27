package com.nth.ikiam.utils;

/**
 * Created by svt on 8/27/2014.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Compass extends View {
    private float direction;

    public Compass(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public Compass(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public Compass(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int w = 280;
        int h = 280;
        int r;
        if(w > h){
            r = h/2;
        }else{
            r = w/2;
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        canvas.drawCircle((w / 2) + 20, (h / 2) + 20, r, paint);
        paint.setColor(Color.rgb(83, 147, 63));
        canvas.drawLine(
                (w / 2) + 20,
                (h / 2) + 20,
                (float) (((w / 2) + 20) + r * Math.sin(-direction)),
                (float) (((h / 2) + 20) - r * Math.cos(-direction)),
                paint);


    }

    public void update(float dir){
        direction = dir;
        invalidate();
    }

}
