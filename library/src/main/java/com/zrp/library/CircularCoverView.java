package com.zrp.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

/**
 * A circular cover view.Above other views to hide the conner.You can set radians by self.
 * Created by zrp on 16-7-19.
 */
public class CircularCoverView extends View {

    private int leftTopRadians = 30;        //leftTopRadians
    private int leftBottomRadians = 30;     //leftBottomRadians
    private int rightTopRadians = 30;       //rightTopRadians
    private int rightBottomRadians = 30;    //rightBottomRadians

    private int coverColor = 0xffeaeaea;    //color of cover.

    public CircularCoverView(Context context) {
        this(context, null, 0);
    }

    public CircularCoverView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularCoverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularCoverView);
        leftTopRadians = typedArray.getDimensionPixelSize(R.styleable.CircularCoverView_left_top_radius, leftTopRadians);
        leftBottomRadians = typedArray.getDimensionPixelSize(R.styleable.CircularCoverView_left_bottom_radius, leftBottomRadians);
        rightTopRadians = typedArray.getDimensionPixelSize(R.styleable.CircularCoverView_right_top_radius, rightTopRadians);
        rightBottomRadians = typedArray.getDimensionPixelSize(R.styleable.CircularCoverView_right_bottom_radius, rightBottomRadians);
        coverColor = typedArray.getColor(R.styleable.CircularCoverView_cover_color, coverColor);
    }

    /**
     * set radians of cover.
     */
    public void setRadians(int leftTopRadians, int rightTopRadians, int leftBottomRadians, int rightBottomRadians) {
        this.leftTopRadians = leftTopRadians;
        this.rightTopRadians = rightTopRadians;
        this.leftBottomRadians = leftBottomRadians;
        this.rightBottomRadians = rightBottomRadians;
    }

    /**
     * set color of cover.
     *
     * @param coverColor cover's color
     */
    public void setCoverColor(@ColorInt int coverColor) {
        this.coverColor = coverColor;
    }

    /**
     * create a sector-bitmap as the dst.
     *
     * @param w width of bitmap
     * @param h height of bitmap
     * @return bitmap
     */
    private Bitmap drawSector(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(0xFFFFCC44);//notice:cannot set transparent color here.otherwise cannot clip at final.

        c.drawArc(new RectF(0, 0, leftTopRadians * 2, leftTopRadians * 2), 180, 90, true, p);
        c.drawArc(new RectF(0, getHeight() - leftBottomRadians * 2, leftBottomRadians * 2, getHeight()), 90, 90, true, p);
        c.drawArc(new RectF(getWidth() - rightTopRadians * 2, 0, getWidth(), rightTopRadians * 2), 270, 90, true, p);
        c.drawArc(new RectF(getWidth() - rightBottomRadians * 2, getHeight() - rightBottomRadians * 2, getWidth(), getHeight()), 0, 90, true, p);
        return bm;
    }

    /**
     * create a rect-bitmap as the src.
     *
     * @param w width of bitmap
     * @param h height of bitmap
     * @return bitmap
     */
    private Bitmap drawRect(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(coverColor);

        c.drawRect(new RectF(0, 0, leftTopRadians, leftTopRadians), p);
        c.drawRect(new RectF(0, getHeight() - leftBottomRadians, leftBottomRadians, getHeight()), p);
        c.drawRect(new RectF(getWidth() - rightTopRadians, 0, getWidth(), rightTopRadians), p);
        c.drawRect(new RectF(getWidth() - rightBottomRadians, getHeight() - rightBottomRadians, getWidth(), getHeight()), p);
        return bm;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setStyle(Paint.Style.FILL);

        //create a canvas layer to show the mix-result
        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.MATRIX_SAVE_FLAG |
                Canvas.CLIP_SAVE_FLAG |
                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        //draw sector-dst-bitmap at first.
        canvas.drawBitmap(drawSector(getWidth(), getHeight()), 0, 0, paint);
        //set Xfermode of paint.
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        //then draw rect-src-bitmap
        canvas.drawBitmap(drawRect(getWidth(), getHeight()), 0, 0, paint);
        paint.setXfermode(null);
        //restore the canvas
        canvas.restoreToCount(sc);
    }
}
