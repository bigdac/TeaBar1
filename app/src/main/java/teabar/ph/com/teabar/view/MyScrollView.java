package teabar.ph.com.teabar.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by Administrator on 2018/7/24.
 * 动态拦截滑动事件
 */

public class MyScrollView extends NestedScrollView {

    private static final String TAG = "MyScrollView";
    private float downX = 0;
    private float downY = 0;
    private int mTouchSlop;
    private boolean enableScroll;//是否能滚动

    public MyScrollView(@NonNull Context context) {
        this(context, null);
    }

    public MyScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public MyScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e(TAG, "onInterceptTouchEvent: " );
        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:

                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                float moveX = ev.getX();
                float moveY = ev.getY();
                float xDistance = Math.abs(downX - moveX);
                float yDistance = Math.abs(downY - moveY);
                //判断是否为垂直滑动
                return (yDistance > xDistance && yDistance > mTouchSlop) && enableScroll;
        }
        return super.onInterceptTouchEvent(ev);

    }

    public void setEnableScroll(boolean enableScroll) {
        this.enableScroll = enableScroll;
    }
}
