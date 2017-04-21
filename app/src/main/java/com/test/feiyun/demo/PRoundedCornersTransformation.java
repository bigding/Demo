package com.test.feiyun.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * Created by 飞云 on 2017/3/14.
 */
//Picasso圆角矩形绘制转换类
public class PRoundedCornersTransformation implements Transformation {
    private int mRadius;
    private int mDiameter;
    private int mMargin;
    public PRoundedCornersTransformation(int radius,int margin){
        mRadius =radius;
        mDiameter = radius * 2;
        mMargin = margin;
    }
    @Override
    public Bitmap transform(Bitmap source) {
        int width = source.getWidth();
        int heigth = source.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width,heigth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP,Shader.TileMode.CLAMP));
        drawRoundRect(canvas,paint,width,heigth);
        source.recycle();
        return bitmap;
    }

    private void drawRoundRect(Canvas canvas, Paint paint, float width, float heigth) {
        float right = width - mMargin;
        float bottom = heigth - mMargin;
        canvas.drawRoundRect(new RectF(mMargin,mMargin,right,bottom),mRadius,mRadius,paint);
    }

    @Override
    public String key() {
        return "square()";
    }
}
