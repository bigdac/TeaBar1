package teabar.ph.com.teabar.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import teabar.ph.com.teabar.R;

public class AlermDialog4 extends Dialog {
    Unbinder unbinder;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.tv_content) TextView tv_content;
    public AlermDialog4(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alerm4);
        unbinder=ButterKnife.bind(this);
    }

    @OnClick({/*R.id.btn_cancel,*/R.id.btn_ensure})
    public void onClick(View v){
        switch (v.getId()){
//            case R.id.btn_cancel:
//                if (onNegativeClickListener!=null){
//                    onNegativeClickListener.onNegativeClick();
//                }
//                break;
            case R.id.btn_ensure:
                if (onPositiveClickListener!=null){
                    onPositiveClickListener.onPositiveClick();
                }
                break;
        }
    }
    int mode=0;
    String content;//显示内容

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    private String line;

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
    String title;
    int [] wrong = {R.string.alarm_equ_water,R.string.alarm_equ_open,R.string.alarm_equ_lg,R.string.alarm_equ_50,R.string.alarm_equ_hot,R.string.alarm_equ_low,R.string.alarm_equ_nowater,R.string.alarm_equ_NTC};
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("AlermDialog4","-->"+mode);

        title="";
         String[] aa = line.split(",");
         for (int i=0;i<aa.length;i++){
             if ("1".equals(aa[i])){

                 title= title+  this.getContext().getText( wrong[i])+",";
             }
         }
        tv_title.setText(R.string.alarm_equ_wrong);
        tv_content.setText(title);
//        if (mode==0){
//            tv_content.setText(R.string.alarm_equ_water);
//        }else if (mode==1){
//            tv_content.setText(R.string.alarm_equ_open);
//        }else if (mode==2){
//
//            tv_content.setText(R.string.alarm_equ_lg);
//        }else if (mode==3){
//
//            tv_content.setText(R.string.alarm_equ_50);
//        }else if (mode==4){
//
//            tv_content.setText(R.string.alarm_equ_hot);
//        }else if (mode==5){
//
//            tv_content.setText(R.string.alarm_equ_low);
//        }else if (mode==6){
//
//            tv_content.setText( R.string.alarm_equ_nowater);
//        }else if (mode==7){
//            tv_content.setText( R.string.alarm_equ_NTC);
//        }
    }
    private int inputType;

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    private String tips;

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("dialog","-->onStop");
        unbinder.unbind();
    }

    private OnPositiveClickListener onPositiveClickListener;

    public void setOnPositiveClickListener(OnPositiveClickListener onPositiveClickListener) {


        this.onPositiveClickListener = onPositiveClickListener;
    }

    private OnNegativeClickListener onNegativeClickListener;

    public void setOnNegativeClickListener(OnNegativeClickListener onNegativeClickListener) {

        this.onNegativeClickListener = onNegativeClickListener;
    }

    public interface OnPositiveClickListener {
        void onPositiveClick();
    }

    public interface OnNegativeClickListener {
        void onNegativeClick();
    }
}
