package teabar.ph.com.teabar.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.activity.EquipmentDetailsActivity;
import teabar.ph.com.teabar.adpter.EqupmentAdapter;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.pojo.Equpment;

public class EqumentFragment extends BaseFragment {
//    @BindView(R.id.rv_equment)
//    RecyclerView rv_equment;
    List<Equpment> equpments;
    EqupmentAdapter equpmentAdapter;
    @Override
    public int bindLayout() {
        return R.layout.fragment_equpment;
    }

    @Override
    public void initView(View view) {
        RecyclerView rv_equment = view.findViewById(R.id.rv_equment);
        equpments= new ArrayList<>();
        for (int i =0;i<14;i++){
            Equpment equpment = new Equpment();
            equpments.add(equpment);
        }
        equpmentAdapter = new EqupmentAdapter(getActivity(),equpments);
        rv_equment.setLayoutManager( new LinearLayoutManager(getActivity()));
        rv_equment.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        rv_equment.setAdapter(equpmentAdapter);
        equpmentAdapter.SetOnItemClick(new EqupmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                    startActivity(new Intent(getActivity(),EquipmentDetailsActivity.class));
            }

            @Override
            public void onLongClick(View view, int posotion) {

            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }



    public class DividerItemDecoration extends RecyclerView.ItemDecoration {


        private final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

        private Drawable mDivider;

        private int mOrientation;

        Paint paint ;
        public DividerItemDecoration(Context context, int orientation) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
            setOrientation(orientation);

        }

        public void setOrientation(int orientation) {
            if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
                throw new IllegalArgumentException("invalid orientation");
            }
            mOrientation = orientation;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent) {
//        Log.v("recyclerview - itemdecoration", "onDraw()");

            if (mOrientation == VERTICAL_LIST) {
//                drawVertical(c, parent);
            } else {
//                drawHorizontal(c, parent);
            }
        }


        public void drawVertical(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                RecyclerView v = new RecyclerView(parent.getContext());
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);


            }
        }

        public void drawHorizontal(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
            int itemCount = parent.getLayoutManager().getItemCount();
            if (mOrientation == VERTICAL_LIST) {

                if (itemPosition==itemCount-1){
                    outRect.set(0, 0, 0, 240);
                }else {
                    outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
                }
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        }
    }
}
