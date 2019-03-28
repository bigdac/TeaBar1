package teabar.ph.com.teabar.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2018/7/25.
 */

public class MyRecyclerView extends RecyclerView {
    private static final String TAG = "MyRecyclerView";

    private boolean enableScroll;//是否能滚动

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG,"onTouchEvent");
        if (!enableScroll) {
            //将滑动事件传递给父类
            getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        }

        return super.onTouchEvent(event);
    }

    public void setEnableScroll(boolean enableScroll) {
        this.enableScroll = enableScroll;
    }
}
