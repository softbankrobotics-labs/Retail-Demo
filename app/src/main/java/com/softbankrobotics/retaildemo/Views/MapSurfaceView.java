package com.softbankrobotics.retaildemo.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.softbankrobotics.retaildemo.R;

public class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Bitmap scaled;
    private Path path;
    private Paint paint;
    private float length;
    private float curr_lenght = 0;

    public MapSurfaceView(Context context) {
        super(context);
    }

    public MapSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        getHolder().addCallback(this);
    }

    public void onDraw(Canvas canvas) {
        Log.d("DRAW","drawing");
        canvas.drawBitmap(scaled, 0, 0, null); // draw the background
        canvas.drawPath(path,paint);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.ui_map);
        float scale = (float) background.getHeight() / (float) getHeight();
        int newWidth = Math.round(background.getWidth() / scale);
        int newHeight = Math.round(background.getHeight() / scale);
        scaled = Bitmap.createScaledBitmap(background, newWidth, newHeight, true);
        path = new Path();
        path.moveTo(700, 480);
        path.lineTo(930, 480);
        path.lineTo(930, 100);
        paint = new Paint();
        CornerPathEffect cpe = new CornerPathEffect(20);
        PathMeasure measure = new PathMeasure(path, false);
        length = measure.getLength();
        paint.setPathEffect(cpe);
        paint.setStrokeWidth(10);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
    }




    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
