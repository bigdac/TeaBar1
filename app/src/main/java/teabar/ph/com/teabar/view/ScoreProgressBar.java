package teabar.ph.com.teabar.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

public class ScoreProgressBar extends View {

    private Paint paint;// 画笔
    private Paint paint1,paint2,paint3,paint4;// 画笔
    private int progress;// 进度值
    private int width;// 宽度值
    private int height;// 高度值
    final  int mCount=100;
    public ScoreProgressBar(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ScoreProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScoreProgressBar(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint1 = new Paint();
        paint2 = new Paint();
        paint3 = new Paint();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth() - 1;// 宽度值
        height = getMeasuredHeight() - 1;// 高度值
    }
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        paint1.setColor(Color.parseColor("#3DBBBBBB"));// 设置画笔颜色

        int coler[]=new int[]{Color.parseColor("#d94928"),Color.parseColor("#eec935"),Color.parseColor("#3ee99b"),Color.parseColor("#34f6b8")};

        LinearGradient lg=new LinearGradient(0,0,width,height,coler,null,
                Shader.TileMode.CLAMP);  //参数一为渐变起初点坐标x位置，参数二为y轴位置，参数三和四分辨对应渐变终点，最后参数为平铺方式，这里设置为镜像
        paint.setShader(lg);
        canvas.drawRect(0, 0, width , height,
                paint);// 画矩形
//
//        canvas.drawRect(0, 0, width,height-progress/100f*height,
//                paint1);// 画矩形

        super.onDraw(canvas);
    }

    public static int setColor(float val,int mCount) {
        float one = (255 + 255) / (mCount * 2 / 3);
        int r = 0, g = 0, b = 0;
        if (val < (mCount * 1 / 3)) {
            r = (int) (one * val);
            g = 255;
        } else if (val >= (mCount * 1 / 3) && val < (mCount * 2 / 3)) {
            r = 255;
            g = 255 - (int) ((val - (mCount * 1 / 3)) * one);
        } else {
            r = 255;
        }//最后一个三等分
        return Color.rgb(r, g, b);
    }
    /**
     * 拿到文字宽度
     * @param str 传进来的字符串
     * return 宽度
     */
    private int getTextWidth(String str) {
        // 计算文字所在矩形，可以得到宽高
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect.width();
    }

    /** 设置progressbar进度 */
    public void setProgress(int progress) {
        this.progress = progress;
        postInvalidate();
    }
}