package teabar.ph.com.teabar.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.lzy.imagepicker.view.GridSpacingItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.MailAdapter;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.pojo.Tea;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.ToastUtil;

public class MailFragment extends BaseFragment {
    RecyclerView rv_mail;
    List<Tea> list = new ArrayList<>();
    MailAdapter mailAdapter;
    @Override
    public int bindLayout() {
        return R.layout.fragment_mail;
    }

    @Override
    public void initView(View view) {
         rv_mail = view.findViewById(R.id.rv_mail);
         mailAdapter= new MailAdapter(getActivity(),list);
         rv_mail.setLayoutManager(new GridLayoutManager(getActivity(),2));
         rv_mail.addItemDecoration(new GridSpacingItemDecoration(2,40,false));
         rv_mail.setAdapter(mailAdapter);
         new getAllTeaListAsynTask().execute();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    String returnMsg1,returnMsg2;

    class getAllTeaListAsynTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            String code = "";
            String result =   HttpUtils.getOkHpptRequest(HttpUtils.ipAddress+"/app/getAllTea" );
            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                if (!"4000".equals(result)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
//                        JsonObject content = new JsonParser().parse(result).getAsJsonObject();
                        code = jsonObject.getString("state");
                        returnMsg1=jsonObject.getString("message1");
                        JSONArray jsonArray =  jsonObject.getJSONArray("data");
//                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        if ("200".equals(code)) {
                            Gson gson = new Gson();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //功能茶
                                Tea teaList = gson.fromJson(jsonArray.get(i).toString(),Tea.class);
                                list.add(teaList);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    code="4000";
                }
            }
            return code;
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            switch (s) {

                case "200":
                    mailAdapter.update(list);
                    break;
                case "4000":

                    ToastUtil.showShort(getActivity(), "连接超时，请重试");

                    break;
                default:

                    ToastUtil.showShort(getActivity(), returnMsg1);
                    break;

            }
        }
    }

}
