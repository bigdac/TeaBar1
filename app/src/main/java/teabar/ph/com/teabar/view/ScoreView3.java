package teabar.ph.com.teabar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.jaygoo.widget.Utils;

import teabar.ph.com.teabar.R;

public class ScoreView3 extends View
{

    private Paint mPaint;
    private Paint mPaint2;
    private float mStrokeWith;
    private boolean mIsRound;
    private int mViewAangle;
    private int mStartAangle;
    private int mViewPadding;
    private Paint mPaint1;
    private int mPointColor;
    private int mPointColor1;
    private int mPointRaido;
    private int mPointRaido1;
    private float mView_x0;
    private float mView_y0;
    private int mPointAngle=0;
    private int mPointAngle1=0;
    private  OnProgressListener mOnProgressListener;
    private Paint mTextPaint;
    private Paint mTextPaint1;
    public void setOnProgressListener(OnProgressListener onProgressListener) {
        mOnProgressListener = onProgressListener;
    }

    public ScoreView3(Context context) {
        super(context);
    }

    public ScoreView3(Context context, AttributeSet attrs) {
        super(context, attrs);
        /*获取属性集合*/
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ColorCircleProgressView, 0, 0);
        /*渐变颜色值*/
        /*圆环角度，开始的角度，到边框的距离*/
        mViewAangle = typedArray.getInteger(R.styleable.ColorCircleProgressView_ViewAngle, 90);
        mStartAangle = typedArray.getInteger(R.styleable.ColorCircleProgressView_StartAngle, 0);
        mViewPadding = typedArray.getInteger(R.styleable.ColorCircleProgressView_ViewPadding, 40);

        /*圆环的大小及是否圆角*/
        mStrokeWith = typedArray.getDimension(R.styleable.ColorCircleProgressView_StrokeWith, getDimen(R.dimen.Viewsize));
        mIsRound = typedArray.getBoolean(R.styleable.ColorCircleProgressView_IsRound, true);

        /*Point的颜色和大小*/
        mPointColor = typedArray.getColor(R.styleable.ColorCircleProgressView_PointColor, Color.WHITE);
        mPointColor1 = typedArray.getColor(R.styleable.ColorCircleProgressView_PointColor1, Color.WHITE);
        mPointRaido = typedArray.getInteger(R.styleable.ColorCircleProgressView_PointRadio, 0);
        mPointRaido1 = typedArray.getInteger(R.styleable.ColorCircleProgressView_PointRadio1, 1);



        /*设置圆环画笔*/
        SetPaint();

        /*设置移动点的画笔*/
        SetPaint01();

        typedArray.recycle();
    }

    private void SetPaint01() {
        mPaint1 = new Paint();
        mPaint1.setColor(mPointColor);

    }

    private void SetPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);  /*画笔为线条线条*/
        mPaint.setStrokeWidth(mStrokeWith);     /*线条的宽*/
        mPaint.setAntiAlias(true);               /*抗锯齿*/
        mPaint.setColor(Color.parseColor("#f1f1f1"));
        if(mIsRound) {mPaint.setStrokeCap(Paint.Cap.ROUND);}  /*是否圆角*/
        mPaint2 = new Paint();
        mPaint2.setStyle(Paint.Style.STROKE);  /*画笔为线条线条*/
        mPaint2.setStrokeWidth(mStrokeWith);     /*线条的宽*/
        mPaint2.setAntiAlias(true);               /*抗锯齿*/
        mPaint2.setColor(Color.parseColor("#45e3cb"));
        if(mIsRound) {mPaint2.setStrokeCap(Paint.Cap.ROUND);}  /*是否圆角*/

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(Utils.dp2px(getContext(), 18));
        mTextPaint.setColor(Color.parseColor("#bbbbbb"));

        mTextPaint1 = new Paint();
        mTextPaint1.setAntiAlias(true);
        mTextPaint1.setTextSize(Utils.dp2px(getContext(), 25));
        mTextPaint1.setColor(Color.parseColor("#333333"));

    }
int width1 = 0;
//    String temp = "90℃";
    @Override
    protected void onDraw(Canvas canvas) {

        /*得到view的宽高*/
        int width = getWidth();
        int height = getHeight();
        width1 = getWidth()/2;
        /*把宽高赋值给全局变量,得到圆心的坐标*/
        mView_x0=width/2;
        mView_y0=height/2;


        /*设置线性渐变*/
//        SweepGradient sweepGradient = new SweepGradient(width/ 2, height/ 2, new int[]{mColor01, mColor02, mColor03, mColor04, mColor05, mColor06, mColor07}, null);
//        mPaint.setShader(sweepGradient);






        /*定义圆环的所占的矩形区域:注意view一定为正方形*/

        RectF   rectF = new RectF(0 + mViewPadding, 0 + mViewPadding, width - mViewPadding, width - mViewPadding);

        /*根据矩形区域画扇形:因为sweep的起点在右边中心处，所以先旋转90度画布*/
        canvas.rotate(90,width/2,height/2);
        canvas.drawArc(rectF, mStartAangle , mViewAangle, false, mPaint);


        /*动态获取圆上起始点的坐标*/
        //圆点坐标：width/2,height/2
        //半径：（width-mViewPadding-mViewPadding）/2
        //角度：a0


//        if(mPointAngle<=90){mPointAngle=90;}
//        else if(mPointAngle>270&mPointAngle<=360){mPointAngle=270;}


        /*将45-315范围的角度转为0-100*/
//        if(mOnProgressListener!=null) {
//            int progress = (int)((mPointAngle - 90) / 2.7);
//            mOnProgressListener.onScrollingListener(progress);
//        }

        float x0=width/2;
        float y0=height/2;
        float R = (float) ((width - mViewPadding - mViewPadding) / 2);
        float Point_x= (float) (x0+R*Math.sin(mPointAngle*3.14/180));
        float Point_y= (float) (y0+R*Math.cos(mPointAngle * 3.14 / 180));
        canvas.drawArc(rectF, mStartAangle ,mPointAngle, false, mPaint2);
//        Log.e("GGGGGGGGGGGGGG", "onDraw: -->"+(mStartAangle+90)+"...."+(mPointAngle) +"..."+mPointAngle);
//        canvas.drawCircle(Point_x,Point_y,mPointRaido1,mPaint2);
//        canvas.drawCircle(Point_x,Point_y,mPointRaido,mPaint1);


    }

    public int getTextWidth(String str,Paint paint){
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        int w = rect.width();
//        int h = rect.height();
        return w;
    }
    private float getDimen(int dimenId) {
        return getResources().getDimension(dimenId);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
    }
    public void setCurProgress(int curProgress) {

            this.mPointAngle = (int)(((float)curProgress)/25f*90);

//
//        if (0<mPointAngle&mPointAngle<90){
//            mPointAngle=90-mPointAngle;
//        }else if (mPointAngle<=360&mPointAngle>=270){
//            mPointAngle=360-mPointAngle+90;
//        }


    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {


//            /*获取点击位置的坐标*/
//            float Action_x = event.getX();
//            float Action_y = event.getY();
//
//            /*根据坐标转换成对应的角度*/
//            float get_x0 = Action_x - mView_x0;
//            float get_y0 = Action_y - mView_y0;
//            if (event.getAction()==MotionEvent.ACTION_MOVE&&isInCiecle(Action_x,Action_y)&&isTouch(Action_x,Action_y)) {
//
//
//                /*01：左下角区域*/
//                if (get_x0 <= 0 & get_y0 >= 0) {
//                    float tan_x = get_x0 * (-1);
//                    float tan_y = get_y0;
//                    double atan = Math.atan(tan_x / tan_y);
//                    mPointAngle = (int) Math.toDegrees(atan);
//                }
//
//                /*02：左上角区域*/
//                if (get_x0 <= 0 & get_y0 <= 0) {
//                    float tan_x = get_x0 * (-1);
//                    float tan_y = get_y0 * (-1);
//                    double atan = Math.atan(tan_y / tan_x);
//                    mPointAngle = (int) Math.toDegrees(atan) + 90;
//                }
//
//                /*03：右上角区域*/
//                if (get_x0 >= 0 & get_y0 <= 0) {
//                    float tan_x = get_x0;
//                    float tan_y = get_y0 * (-1);
//                    double atan = Math.atan(tan_x / tan_y);
//                    mPointAngle = (int) Math.toDegrees(atan) + 180;
//                }
//
//                /*04：右下角区域*/
//                if (get_x0 >= 0 & get_y0 >= 0) {
//                    float tan_x = get_x0;
//                    float tan_y = get_y0;
//                    double atan = Math.atan(tan_y / tan_x);
//                    mPointAngle = (int) Math.toDegrees(atan) + 270;
//                }
//                if(mOnProgressListener!=null) {
//                    if (mPointAngle>=90&mPointAngle<180){
//                        mPointAngle1=0;
//                    }else if (mPointAngle>180&mPointAngle<270){
//                        mPointAngle1=180;
//                    }
//                    if (0<mPointAngle&mPointAngle<90){
//                        mPointAngle1=90-mPointAngle;
//                    }else if (mPointAngle<=360&mPointAngle>=270){
//                        mPointAngle1=360-mPointAngle+90;
//                    }
//                    mOnProgressListener.onScrollingListener( (mPointAngle1)*25/180);
//                    Log.e("DDDDDD", "setCurProgress: -->"+mPointAngle +"...."+(mPointAngle)*25/180);
//
//                }
//                if (mPointAngle>=90&mPointAngle<180){
//                    mPointAngle=0;
//                }else if (mPointAngle>180&mPointAngle<270){
//                    mPointAngle=180;
//                }
//                if (0<mPointAngle&mPointAngle<90){
//                    mPointAngle=90-mPointAngle;
//                }else if (mPointAngle<=360&mPointAngle>=270){
//                    mPointAngle=360-mPointAngle+90;
//                }
//                /*得到点的角度后进行重绘*/
//                invalidate();
//            }

        return true;
    }
    /**判断落点是否在圆环上*/
    public boolean isInCiecle(float x,float y){
        Log.i("x","-->"+x);
        Log.i("y","-->"+y);
        float distance = (float) Math.sqrt((x-width1)*(x-width1)+(y-width1)*(y-width1));
        Log.i("distance","-->"+distance);
        int smallCircleRadus=width1-100;
        Log.i("smallCircleRadus","-->"+smallCircleRadus);
        float get_x0 = x - mView_x0;
        float get_y0 = y - mView_y0;

        if (distance>=smallCircleRadus && distance<=width1 &&(get_x0 <= 0 & get_y0 >= -5||(get_x0 >= 0 & get_y0 >= -5)))
            return true;
        else
            return false;
    }

    private boolean isTouch(float x, float y) {
        double radius = (getWidth() - getPaddingLeft() - getPaddingRight() + getCircleWidth()) / 2;
        double centerX = getWidth() / 2;
        double centerY = getHeight() / 2;
        return Math.pow(centerX - x, 2) + Math.pow(centerY - y, 2) < radius * radius;
    }
private float getCircleWidth() {
    return Math.max(50, Math.max(50, 25));
}

    public interface OnProgressListener{

        public void onScrollingListener(Integer progress);

    }



}
