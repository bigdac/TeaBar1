package teabar.ph.com.teabar.activity.my;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.yancy.gallerypick.config.GalleryConfig;
import com.yancy.gallerypick.config.GalleryPick;
import com.yancy.gallerypick.inter.IHandlerCallBack;
import com.yancy.gallerypick.inter.ImageLoader;
import com.yancy.gallerypick.widget.GalleryImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.PersonSetActivity;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseWeakAsyncTask;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.util.BitmapCompressUtils;
import teabar.ph.com.teabar.util.HttpUtils;

/*
* 個人信息設置頁面 頭像，用戶名，ID
* */
public class PersonnalActivity extends BaseActivity  {


    @BindView(R.id.iv_power_fh)
    ImageView iv_power_fh;
    @BindView(R.id.iv_person_pic)
    ImageView iv_person_pic;
    @BindView(R.id.tv_person_name)
    TextView tv_person_name;
    @BindView(R.id.tv_person_id)
    TextView tv_person_id;
    @BindView(R.id.rl_person_mes)
     RelativeLayout rl_person_mes;
    SharedPreferences preferences;
    MyApplication application;
    String id;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_personal;
    }

    @Override
    public void initView(View view) {

        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my",MODE_PRIVATE);
        int type1 = preferences.getInt("type1",0);
        if (type1==1){
            rl_person_mes.setVisibility(View.GONE);
        }
        String name = preferences.getString("userName","");
        tv_person_name.setText(name);
        id = preferences.getString("userId","");
        String photoUrl = preferences.getString("photoUrl","");
        if (!TextUtils.isEmpty(photoUrl)){
            Glide.with(this).load(photoUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.my_pic).transform(new teabar.ph.com.teabar.util.GlideCircleTransform(this)).into(iv_person_pic);
        }

        tv_person_id.setText(id+"");
        initPermissions();
        initGallery();
        initImage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String photo = preferences.getString("photo","");
        if (!TextUtils.isEmpty(photo)){
            Glide.with(this).load(photo).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.my_pic).transform(new GlideCircleTransform(this)).into(iv_person_pic);
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "同意授权");
                GalleryPick.getInstance().setGalleryConfig(galleryConfig).open(PersonnalActivity.this);
                isNeedCheck = false;
            } else {
                Log.i(TAG, "拒绝授权");
            }
        }
    }
    private boolean isNeedCheck = true;
    private void initPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "需要授权 ");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.i(TAG, "拒绝过了");
//                Toast.makeText(this, "请在 设置-应用管理 中开启此应用的储存授权。", Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "进行授权");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            Log.i(TAG, "不需要授权 ");
            isNeedCheck = true;
            GalleryPick.getInstance().setGalleryConfig(galleryConfig).open(PersonnalActivity.this);
        }
    }
    private IHandlerCallBack iHandlerCallBack;

    private void initGallery() {
        iHandlerCallBack = new IHandlerCallBack() {
            @Override
            public void onStart() {
                Log.i(TAG, "onStart: 开启");
            }

            @Override
            public void onSuccess(List<String> photoList) {
                Log.i(TAG, "onSuccess: 返回数据");
                try {
                    if (photoList != null && !photoList.isEmpty()) {
                        String path = photoList.get(0);
                        File file = new File(path);
                        if (file!=null && file.exists()){
                            Glide.with(PersonnalActivity.this).load(file).transform(new GlideCircleTransform(getApplicationContext())).into(iv_person_pic);
                            Map<String,Object> params = new HashMap<>();
                            params.put("userId",id);
                            Map<String,Object> params1 = new HashMap<>();
                            params1.put("photo",file);
                            preferences.edit().putString("photo",file.getPath()).commit();
                            new LoadUserInfo(PersonnalActivity.this).execute(params,params1);
                        }
//                        params.put("deviceId", deviceId);
//                        new UpImageAysnc().execute(file).get(3, TimeUnit.SECONDS);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: 取消");
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "onFinish: 结束");
            }

            @Override
            public void onError() {
                Log.i(TAG, "onError: 出错");
            }
        };

    }
    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 8;
    private GalleryConfig galleryConfig;
    private void initImage() {
        galleryConfig = new GalleryConfig.Builder()
                .imageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(Activity activity, Context context, String path, GalleryImageView galleryImageView, int width, int height) {
                        Glide.with(context)
                                .load(path)
                                .placeholder(R.mipmap.gallery_pick_photo)
                                .centerCrop()
                                .into(galleryImageView);
                    }

                    @Override
                    public void clearMemoryCache() {

                    }
                })    // ImageLoader 加载框架（必填）
                .iHandlerCallBack(iHandlerCallBack)     // 监听接口（必填）
                .provider("teabar.ph.com.teabar.fileprovider2")   // provider(必填)
                .multiSelect(false)                      // 是否多选   默认：false
                .multiSelect(false, 9)                   // 配置是否多选的同时 配置多选数量   默认：false ， 9
                .maxSize(9)                             // 配置多选时 的多选数量。    默认：9
                .crop(true)                             // 快捷开启裁剪功能，仅当单选 或直接开启相机时有效
                .crop(true, 1, 1, 500, 500)             // 配置裁剪功能的参数，   默认裁剪比例 1:1
                .isShowCamera(true)                     // 是否现实相机按钮  默认：false
                .filePath("/Gallery/Pictures")          // 图片存放路径
                .build();
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                if (popupWindow!=null&&popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                setResult(7300);
                finish();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
    @OnClick({ R.id.iv_power_fh,R.id.rl_person_pic,R.id.rl_person_nick,R.id.rl_person_id,R.id.rl_person_mes})
    public void onClick(View view){
        switch (view.getId()){

            case R.id.iv_power_fh:
                if (popupWindow!=null&&popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                setResult(7300);
                finish();
                break;

            case R.id.rl_person_pic:
                showPopup();
                break;

            case R.id.rl_person_nick:

                break;

            case R.id.rl_person_id:

                break;

            case R.id.rl_person_mes:
                Intent intent = new Intent(this,PersonSetActivity.class);
                startActivityForResult( intent,2000 );
                break;

        }
    }




    private View contentViewSign;
    private Button camera, gallery;
    private PopupWindow popupWindow;
    TextView cancel;
    boolean isPopup=false;
    private void showPopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        contentViewSign = LayoutInflater.from(PersonnalActivity.this).inflate(R.layout.view_changepicture, null);
        popupWindow = new PopupWindow(contentViewSign);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(false);
//        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(false);
        backgroundAlpha(0.5f);
        //添加pop窗口关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        popupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        camera = (Button) contentViewSign.findViewById(R.id.camera);
        gallery = (Button) contentViewSign.findViewById(R.id.gallery);
        cancel = (TextView) contentViewSign.findViewById(R.id.cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    //在此处添加你的按键处理 xxx
                    if (Build.VERSION.SDK_INT >= 23) {
                        //android 6.0权限问题
                        if (ContextCompat.checkSelfPermission(PersonnalActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(PersonnalActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                            ToastUtil.showShort(PersonnalActivity.this,"请打开相机权限");
                            ActivityCompat.requestPermissions(PersonnalActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERAPRESS);

                        } else {
                            startCamera();
                        }
                    } else {
                        startCamera();
                    }
                }
                backgroundAlpha(1.0f);
                popupWindow.dismiss();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
//                    if (Build.VERSION.SDK_INT >= 23) {
//                        //android 6.0权限问题
//                        if (ContextCompat.checkSelfPermission(PersonnalActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
//                                ContextCompat.checkSelfPermission(PersonnalActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                            ToastUtil.showShort(PersonnalActivity.this,"请打开相机权限");
//                            ActivityCompat.requestPermissions(PersonnalActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, ICONPRESS);
//                        } else {
//                            startGallery();
//                        }
//
//                    } else {
//                        startGallery();
//                    }
                    GalleryPick.getInstance().setGalleryConfig(galleryConfig).open(PersonnalActivity.this);
                }
                backgroundAlpha(1.0f);
                popupWindow.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    isPopup=false;
                    backgroundAlpha(1.0f);
                    popupWindow.dismiss();
                }
            }
        });
    }

    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }
    final static int CAMERA = 1;//拍照
    final static int ICON = 2;//相册
    final static int CAMERAPRESS = 3;//拍照权限
    final static int ICONPRESS = 4;//相册权限
    final static int PICTURE_CUT = 5;//剪切图片
    private static final String TAG = "RoomContentActivity";
    private Uri outputUri;//裁剪完照片保存地址
    Uri imageUri; //图片路径
    File imageFile; //图片文件
    String imagePath;
    private boolean isClickCamera;//是否是拍照裁剪

    //拍照
    public void startCamera() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        imageFile = new File(getExternalCacheDir(), "background3.png");
        backgroundAlpha(1.0f);
        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
        } catch (IOException e) {

            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 24) {
            imageUri = Uri.fromFile(imageFile);
        } else {
            //Android 7.0系统开始 使用本地真实的Uri路径不安全,使用FileProvider封装共享Uri
            //参数二:fileprovider绝对路径 com.dyb.testcamerademo：项目包名

            imageUri = FileProvider.getUriForFile(PersonnalActivity.this, "teabar.ph.com.teabar.fileprovider2", imageFile);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //照相
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定图片输出地址
        startActivityForResult(intent, CAMERA); //启动照相
    }

    //打开相册
    public void startGallery() {
        backgroundAlpha(1.0f);
        Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        startActivityForResult(intent1, ICON);
    }

    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        // 创建File对象，用于存储裁剪后的图片，避免更改原图
        File file = new File(getExternalCacheDir(), "crop_image2.jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        outputUri = Uri.fromFile(file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        //裁剪图片的宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("crop", "true");//可裁剪
        // 裁剪后输出图片的尺寸大小
//        intent.putExtra("outputX", 150);
//        intent.putExtra("outputY", 150);
        intent.putExtra("scale", true);//支持缩放
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片格式
        intent.putExtra("noFaceDetection", true);//取消人脸识别
        startActivityForResult(intent, PICTURE_CUT);
    }

    // 4.4及以上系统使用这个方法处理图片 相册图片返回的不再是真实的Uri,而是分装过的Uri
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        cropPhoto(uri);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        imagePath = getImagePath(uri, null);
        cropPhoto(uri);
    }

    /*  完成后回调*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        File file;

        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "requestCode" + requestCode + "resultCode" + resultCode);
        switch (requestCode) {
            case CAMERA:
                if (resultCode == RESULT_OK) {
                    cropPhoto(imageUri);
                }
                break;
            case ICON:
//                if (resultCode == RESULT_OK) {
//                    if (Build.VERSION.SDK_INT >= 19) {
//                        // 4.4及以上系统使用这个方法处理图片
//                        handleImageOnKitKat(data);
//                    } else {
//                        // 4.4以下系统使用这个方法处理图片
//                        handleImageBeforeKitKat(data);
//                    }
//                }
                break;
            case PICTURE_CUT://裁剪完成
                isClickCamera = true;
                Bitmap bitmap2 = null;
                try {
                    if (isClickCamera) {
                        bitmap2 = BitmapFactory.decodeStream(getContentResolver().openInputStream(outputUri));
                    } else {
                        bitmap2 = BitmapFactory.decodeFile(imagePath);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (bitmap2 == null) {
                    break;
                }
                File file2 = BitmapCompressUtils.compressImage(bitmap2);
                if (!TextUtils.isEmpty(file2.getPath())){
                    Glide.with(PersonnalActivity.this).load(file2).transform(new GlideCircleTransform(getApplicationContext())).into(iv_person_pic);
                }
                Map<String,Object> params = new HashMap<>();
                params.put("userId",id);
                Map<String,Object> params1 = new HashMap<>();
                params1.put("photo",file2);
                preferences.edit().putString("photo",file2.getPath()).commit();
                new LoadUserInfo(this).execute(params,params1);
                break;
        }
        if (resultCode==2000){
            String name = preferences.getString("userName","");
            tv_person_name.setText(name);
        }
    }

    String returnMsg1,returnMsg2;
    class LoadUserInfo extends BaseWeakAsyncTask<Map<String,Object>,Void,String,BaseActivity> {

        public LoadUserInfo(BaseActivity baseActivity) {
            super(baseActivity);
        }

        @Override
        protected String doInBackground(BaseActivity baseActivity, Map<String, Object>... maps) {
            String code = "";
            Map<String,Object> param1 = maps[0];
            Map<String,Object> param2 = maps[1];
            String result = HttpUtils.upFileAndDesc(HttpUtils.ipAddress+"/api/changePhoto",  param1,param2);
            if (!TextUtils.isEmpty(result)) {
                try {
                    if (!"4000".equals(result)){
                        JSONObject jsonObject = new JSONObject(result);
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                    }
                    else {
                        code="4000";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return code;
        }


        @Override
        protected void onPostExecute(BaseActivity baseActivity, String code) {

            switch (code) {
                case "200":
                    toast(  getText(R.string.toast_update_cg).toString());

                    break;
                default:
                   toast( getText(R.string.toast_update_sb).toString());
                    break;
            }

        }

    }
    public class GlideCircleTransform extends BitmapTransformation {
        public GlideCircleTransform(Context context) {
            super(context);
        }

        @Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private  Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override public String getId() {
            return getClass().getName();
        }
    }

}
