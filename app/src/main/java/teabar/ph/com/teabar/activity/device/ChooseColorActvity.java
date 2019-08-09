package teabar.ph.com.teabar.activity.device;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.ph.teabar.database.dao.DaoImp.EquipmentImpl;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.AutoSizeCompat;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.service.MQService;
import teabar.ph.com.teabar.util.ToastUtil;
import teabar.ph.com.teabar.view.ColorPickerDialog;
import teabar.ph.com.teabar.view.ColorPlateView;
import teabar.ph.com.teabar.view.OnActivityColorPickerListener;



public class ChooseColorActvity extends BaseActivity {
    MyApplication application;

    @BindView(R.id.img_hue)
    ImageView img_hue;
    @BindView(R.id.color_plate)
    ColorPlateView color_plate;
    @BindView(R.id.plate_cursor)
    ImageView plate_cursor;//小圆圈
    @BindView(R.id.hue_cursor)
    ImageView hue_cursor;
    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.tv_color)
    Button tv_color;
    @BindView(R.id.iv_choose_light)
    ImageView iv_choose_light ;

    @BindView(R.id.seekbar)
    RangeSeekBar slide_bar;
    private OnActivityColorPickerListener mListener;
    private ColorPickerDialog mColorPickerDialog;
    private boolean supportAlpha;//颜色是否支持透明度
    private final float[] mCurrentHSV = new float[3];
    private int mAlpha;
    Equpment equpment;
    private boolean MQBound;
    EquipmentImpl equipmentDao;
    public static boolean isRunning = false;
    MessageReceiver receiver;
    int value;
    @Override
    public void initParms(Bundle parms) {
        equpment = (Equpment) parms.getSerializable("equpment");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_choosecolor;
    }
    @Override
    public Resources getResources() {
        //需要升级到 v1.1.2 及以上版本才能使用 AutoSizeCompat
//        AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()));//如果没有自定义需求用这个方法
        AutoSizeCompat.autoConvertDensity((super.getResources()), 667, false);//如果有自定义需求就用这个方法
        return super.getResources();

    }
    @Override
    public void initView(View view) {
        color_plate.setHue(getColorHue());
        if (application == null) {
            application = (MyApplication) getApplication();
        }

        isRunning = true;
        application.addActivity(this);
        equipmentDao = new EquipmentImpl(getApplicationContext());
        int defauleColor = getResources().getColor(R.color.colorPrimary);
        defauleColor = defauleColor | 0xff000000;
        if (equpment!=null){
            if(!TextUtils.isEmpty(equpment.getLightColor())){
                String[] aa =equpment.getLightColor().split("/");
                int red =0;
                int green=0;
                int blue=0;
                if (aa.length>=3){
                    red = Integer.valueOf(aa[0]);
                    green = Integer.valueOf(aa[1]);
                    blue = Integer.valueOf(aa[2]);
                }
                defauleColor = Color.rgb(red, green, blue);
                iv_choose_light.setImageTintList(ColorStateList.valueOf(Color.rgb(red,green,blue)));

            }
            if (equpment.getBringht()==0){
                slide_bar.setValue(1);
            }else {
                slide_bar.setValue(equpment.getBringht());
            }


        }

        Color.colorToHSV(defauleColor,mCurrentHSV);
        mAlpha = Color.alpha(defauleColor);
        color_plate .setHue(getColorHue());
        initOnTouchListener();
        initGlobalLayoutListener(container);
//        bt_choose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mColorPickerDialog = new ColorPickerDialog(ChooseColorActvity.this,getResources().getColor(R.color.colorPrimary),supportAlpha,mOnColorPickerListener).show();
//                supportAlpha = !supportAlpha;
//            }
//        });
        MQintent = new Intent(this, MQService.class);
        MQBound =  bindService(MQintent, MQconnection, Context.BIND_AUTO_CREATE);

        slide_bar.setIndicatorTextDecimalFormat("0");
        slide_bar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                Log.i("TrackingTouch", "-->" + leftValue + "," + rightValue);

                value = Math.round(leftValue);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                Log.i("TrackingTouch", "-->停止滑动"+MQservice);


                if (MQservice != null ) {
                    if (equpment.getMStage()!=0xb6&&equpment.getMStage()!=0xb7){
                    equpment.setBringht(value);
                    MQservice.send(equpment.getMacAdress(), value);
                    }else {
                        ToastUtil.showShort(ChooseColorActvity.this, getText(R.string.toast_updata_no).toString());
                    }
                }
            }
        });
        IntentFilter intentFilter = new IntentFilter("ChooseColorActvity");
        receiver = new  MessageReceiver();
        registerReceiver(receiver, intentFilter);
    }
    Equpment msg1;
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("qqqqqZZZZ???", "11111");
            String msg = intent.getStringExtra("msg");
            msg1 = (Equpment) intent.getSerializableExtra("msg1");
            RefrashColor(msg1);
        }
    }

    public void RefrashColor(Equpment equpment ){
        if (equpment!=null){
            this.equpment =equpment;
            if(!TextUtils.isEmpty(equpment.getLightColor())){
                String[] aa =equpment.getLightColor().split("/");
                int red =0;
                int green=0;
                int blue=0;
                if (aa.length>=3){
                    red = Integer.valueOf(aa[0]);
                    green = Integer.valueOf(aa[1]);
                    blue = Integer.valueOf(aa[2]);
                }
                int  defauleColor = Color.rgb(red, green, blue);
                iv_choose_light.setImageTintList(ColorStateList.valueOf(Color.rgb(red,green,blue)));
            }
            if (equpment.getBringht()==0){
                slide_bar.setValue(1);
            }else {
                slide_bar.setValue(equpment.getBringht());
            }


        }
    }

    Intent MQintent;
    MQService MQservice;
    boolean boundservice;
    ServiceConnection MQconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            MQservice = binder.getService();
            boundservice = true;
            Log.e("QQQQQQQQQQQDDDDDDD", "onServiceConnected: ------->");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning=false;
        if (MQservice!=null)
            unbindService(MQconnection);
        if (receiver!=null)
            unregisterReceiver(receiver);
    }

    @OnClick({R.id.iv_equ_fh,R.id.tv_color})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_equ_fh:
                finish();
                break;

            case R.id.tv_color:
                if (red==0&&green==0&&blue==0){
                    if(!TextUtils.isEmpty(equpment.getLightColor())) {
                        String[] aa = equpment.getLightColor().split("/");
                        if (aa.length >= 3) {
                            red = Integer.valueOf(aa[0]);
                            green = Integer.valueOf(aa[1]);
                            blue = Integer.valueOf(aa[2]);
                        }
                    }
                }
                if (equpment.getMStage()!=0xb6&&equpment.getMStage()!=0xb7){
                    MQservice.sendLightColor(equpment.getMacAdress(),0,red,green,blue,0);
                    equpment.setLightColor(red+"/"+green+"/"+blue);
                    equipmentDao.update(equpment);
                    setResult(3100);
                    finish();
                }else {
                    ToastUtil.showShort(ChooseColorActvity.this, getText(R.string.toast_updata_no).toString());
                }

                break;
        }
    }

    /**
     * 设置色彩
     * @param color
     */
    private void setColorHue(float color){
        mCurrentHSV[0] = color;
    }

    private float getColorHue(){
        return mCurrentHSV[0];
    }
    /**
     * 设置颜色深浅
     */
    private void setColorSat(float color) {
        this.mCurrentHSV[1] = color;
    }

    private float getColorSat(){
        return this.mCurrentHSV[1];
    }

    /**
     * 设置颜色明暗
     */
    private void setColorVal(float color){
        this.mCurrentHSV[2] = color;
    }

    private float getColorVal(){
        return mCurrentHSV[2];
    }

    /**
     * 获取int颜色
     */
    int red;
    int green;
    int blue;
    private int getColor(){
        final  int argb = Color.HSVToColor(mCurrentHSV);
         red = ((argb & 0x00FF0000) >> 16);
         green = ((argb & 0x0000FF00) >> 8);
         blue = argb & 0x000000FF;
        Log.e(TAG, "getColor: -->"+red+","+green+","+blue );
        return mAlpha << 24 | (argb & 0x00ffffff);
    }

    /**
     * 全局布局状态监听
     */
    private void initGlobalLayoutListener(final View view) {
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)//api 16
            @Override
            public void onGlobalLayout() {
                moveHueCursor();
                movePlateCursor();
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });
    }

    /**
     * 触摸监听
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initOnTouchListener() {
        //色彩板的触摸监听
        img_hue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
                    float y = event.getY();
                    if (y < 0.f) y = 0.f;
                    if (y > img_hue.getMeasuredHeight()) y = img_hue.getMeasuredHeight() - 0.001f;
                    float colorHue = 360.f - 360.f / img_hue.getMeasuredHeight() * y-1f;
                    if (colorHue == 360.f) colorHue = 0.f;
                    setColorHue(colorHue);
                    color_plate.setHue(colorHue);

                    moveHueCursor();

                    if (mListener != null) {
                        mListener.onColorChange(ChooseColorActvity.this, getColor());
                    }
//                    updateAlphaView();
                    return true;
                }
                return false;
            }
        });

        //颜色样板的触摸监听
        color_plate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP){
                    float x = event.getX();
                    float y = event.getY();
                    if (x < 0.f) x = 0.f;
                    if (x > color_plate.getMeasuredWidth()) x = color_plate.getMeasuredWidth();
                    if (y < 0.f) y = 0.f;
                    if (y > color_plate.getMeasuredHeight()) y = color_plate.getMeasuredHeight();

                    setColorSat(1.f / color_plate.getMeasuredWidth() * x);//颜色深浅
                    setColorVal(1.f - (1.f / color_plate.getMeasuredHeight() * y));//颜色明暗
                    movePlateCursor();
//                    tv_color.setBackgroundColor(getColor());

                    iv_choose_light.setImageTintList(ColorStateList.valueOf(Color.rgb(red,green,blue)));
                    Log.e("DDDDDDDDDDDDDDDDDDDZ", "onTouch: -->"+getColor() );
                    if (mListener!=null){
                        mListener.onColorChange(ChooseColorActvity.this,getColor());
                    }
                    return true;
                }
                return false;
            }
        });
    }


        /**
         * 移动色彩样板指针
         */
        private void moveHueCursor(){//ConstraintLayout$LayoutParams
            float y = img_hue.getMeasuredHeight() - (getColorHue() * img_hue.getMeasuredHeight() / 360.f);
            if (y == img_hue.getMeasuredHeight()) y = 0.f;
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) hue_cursor.getLayoutParams();
//            layoutParams.leftMargin = (int) (img_hue.getLeft() - Math.floor(hue_cursor.getMeasuredWidth() / 9) - container.getPaddingLeft());
            layoutParams.topMargin = (int) (img_hue.getTop() + y - Math.floor(hue_cursor.getMeasuredHeight() / 2) - container.getPaddingTop());
            hue_cursor.setLayoutParams(layoutParams);
        }

    /**
     * 移动最终颜色样板指针
     */
    private void movePlateCursor(){
        final float x = getColorSat() * color_plate.getMeasuredWidth();
        final float y = (1.f - getColorVal()) * color_plate.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) plate_cursor.getLayoutParams();
        layoutParams.leftMargin = (int) (color_plate.getLeft() + x - Math.floor(plate_cursor.getMeasuredWidth() / 2) - container.getPaddingLeft());
        layoutParams.topMargin = (int) (color_plate.getTop() + y - Math.floor(plate_cursor.getMeasuredHeight() / 2) - container.getPaddingTop());
        plate_cursor.setLayoutParams(layoutParams);

    }
    /**
     * 更新透明度UI
     */
    private void updateAlphaView(){
        final GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{Color.HSVToColor(mCurrentHSV),0x0});
//        mViewAlphaOverlay.setBackgroundDrawable(gd);
    }



}
