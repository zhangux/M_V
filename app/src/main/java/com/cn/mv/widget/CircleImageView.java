package com.cn.mv.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/7/18.
 */
public class CircleImageView extends ImageView{
    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable=getDrawable();
        if(drawable instanceof BitmapDrawable){
            Bitmap bmp=((BitmapDrawable)drawable).getBitmap();
            if(bmp!=null){
                canvas.drawBitmap(createCircle(bmp),0,0,null);
            }else{
                super.onDraw(canvas);
            }
        }else{
            super.onDraw(canvas);
        }
    }

    private Bitmap createCircle(Bitmap src){
        int size=getWidth()>getHeight()?getHeight():getWidth();
        Bitmap bmp=Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bmp);
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawARGB(0,0,0,0);
        canvas.drawCircle(size/2,size/2,size/2,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect srcRect=new Rect(0,0,src.getWidth(),src.getHeight());
        Rect dstRect=new Rect(0,0,size,size);
        canvas.drawBitmap(src,srcRect,dstRect,paint);
        return bmp;
    }
}
