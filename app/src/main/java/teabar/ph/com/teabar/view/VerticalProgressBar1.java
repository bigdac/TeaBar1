package teabar.ph.com.teabar.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import teabar.ph.com.teabar.R;

public class VerticalProgressBar1 extends View {

    private Paint paint;// 画笔
    private Paint paint1;// 画笔
    private int progress;// 进度值
    private int width;// 宽度值
    private int height1;// 高度值
    private int height;// 高度值
    private String mText;
    private float mTextWidth;
    private Bitmap mBackgroundBitmap;
    private float mBgWidth, mBgHeight;
    private float mTextBaseLineY;
    private Paint mPaint;
    public VerticalProgressBar1(Context context, AttributeSet attrs,
                                int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public VerticalProgressBar1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerticalProgressBar1(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();

        paint1 = new Paint();

        //设置画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(15);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth() - 1;// 宽度值
        height = getMeasuredHeight() - 1;// 高度值

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        int height= getHeight()-110;
        mBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.add_make);
        mBgWidth = mBackgroundBitmap.getWidth();
        mBgHeight = mBackgroundBitmap.getHeight();
        paint1.setColor(Color.parseColor("#3DBBBBBB"));
        LinearGradient  backGradient = new LinearGradient(0, 0, (width-progress / 100f * width)/2, height, new int[]{Color.parseColor("#c2a6fa"),Color.parseColor("#8394fb")}, null, Shader.TileMode.CLAMP);
        paint.setShader(backGradient);
//        if (progress==100){
//            canvas.drawRoundRect(0, 0, width - progress / 100f * width, height,30,30,
//                    paint);// 画矩形
//        }else {
//            if (progress < 2) {
//                canvas.drawRoundRect(6, height - progress / 100f * height, width - 6, height, 10, 10,
//                        paint);// 画矩形
//            } else if (progress == 2) {
//                canvas.drawRoundRect(4, height - progress / 100f * height, width - 4, height, 10, 10,
//                        paint);// 画矩形
//                canvas.drawRect(2, height - progress / 100f * height, width - 2, height - progress / 100f * height + 2,
//                        paint);// 画矩形
//            }
//
//            else {
//                canvas.drawRect(20, 0, width - progress / 100f * width+5, height ,
//                        paint);// 画矩形
//                canvas.drawRoundRect(0, 0, width - progress / 100f * width, height,30,30,
//                        paint);// 画矩形
//            }
//        }

//        canvas.drawRect(0, height - progress / 100f * height, width, height,
//                paint);// 画矩形
        getTextLocation();
        Rect bgRect = new Rect(0,0,width/2,height);
        //计算数值背景X坐标
        float bgX = bgRect.height() * getProgress() /100;
        //计算数值背景Y坐标
        float bgY = 0;
//        if(mTextOrientation == ORIENTATION_BOTTOM) {
            bgY = mBgHeight + 10;
//        }

        //计算数值文字X坐标
        float textX = (float) (mTextBaseLineY/* + bgY + (0.16 * mBgHeight / 2) - 10*/);
        float textY = bgX + (mBgWidth - mTextWidth) / 2-90;
        Log.e("FGGGGGGG", "onDraw: -->"+textX+"...."+textY );
        //绘制文字和背景
        canvas.drawBitmap(mBackgroundBitmap, textX-50, textY, mPaint);
        Log.e("CCCSCCCCCCCCCCCCCC", "onDraw: ..."+bgX+".....--" +bgY);
        canvas.drawText(mText, textX, textY, mPaint);

        canvas.rotate(180,width/2,height/2);
        canvas.drawRect(0, height-progress / 100f * height,  width/2, height ,
                paint1);// 画矩形
        canvas.drawRect(0, 0, width/2, height-progress / 100f * height,
                paint);// 画矩形

//        canvas.drawLine(0, 0, width, 0, paint);// 画顶边
//        canvas.drawLine(0, 0, 0, height, paint);// 画左边
//        canvas.drawLine(width, 0, width, height, paint);// 画右边
//        canvas.drawLine(0, height, width, height, paint);// 画底边

//        paint.setColor(Color.RED);// 设置画笔颜色为红色
//        paint.setTextSize(width / 3);// 设置文字大小
//        canvas.drawText(progress + "%",
//                (width - getTextWidth(progress + "%")) / 2, height / 2, paint);// 画文字
        super.onDraw(canvas);
    }


    public  float  getProgress(){
        return progress;
    }


    /**
     * 计算SeekBar数值文字的显示位置
     */
    public void getTextLocation() {
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        mText = "" + String.valueOf(getProgress());
        //测量文字宽度
        mTextWidth = mPaint.measureText(mText);
        //计算文字基线Y坐标
        mTextBaseLineY = mBgHeight / 2 - fm.descent + (fm.descent - fm.ascent) / 2;
    }

    /** 设置progressbar进度 */
//    public void setProgress(int progress) {
//        this.progress =progress;
//        postInvalidate();
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.e("DDDDDDSSSSSS", "onTouchEvent: -->"+event.getY()+"...."+getHeight() );
        if (isInCiecle(event.getX(),event.getY())){
            if (event.getY() < 0) {
                setProgress(0);
                return true;
            } else if (event.getY() > getHeight()  ) {
                setProgress(100);
                return true;
            } else {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        setProgress(calculProgress(event.getY()));
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        setProgress(calculProgress(event.getY()));
                        return true;
                }
            }
        }




        return true;
    }

    /**判断落点是否在圆环上*/
    public boolean isInCiecle(float x,float y){
        int width1= getHeight();

        if (y<= width1&& y>0)
            return true;
        else
            return false;
    }
    /**
     * 计算触摸点的百分比
     *
     * @param eventX
     * @return
     */
    private int calculProgress(float eventX) {
        float proResult = (eventX  ) / (getHeight() );
        return (int) (proResult * 100);
    }

    /**
     * 设置百分比
     *
     * @param progress
     */
    public void setProgress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("progress 不可以小于0 或大于100");
        }
        this.progress = progress;
        invalidate();

        if (progressChangedListener != null) {
            progressChangedListener.onProgressChanged(this, progress);
        }
    }
    /**
     * 设置进度变化监听器
     *
     * @param onProgressChangedListener
     */
    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
        this.progressChangedListener = onProgressChangedListener;
    }

    private OnProgressChangedListener progressChangedListener;

    public interface OnProgressChangedListener {
        void onProgressChanged(View view, int progress);
    }


}