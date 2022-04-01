package com.company.iendo.ui.popup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.other.ArrowDrawable;
import com.company.iendo.widget.MyItemDecoration;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BasePopupWindow;
import com.hjq.base.action.AnimAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/10/18
 * desc   : 列表弹窗
 */
public final class ListSearchPopup {

    public static final class Builder
            extends BasePopupWindow.Builder<Builder>
            implements BaseAdapter.OnItemClickListener {

        @SuppressWarnings("rawtypes")
        @Nullable
        private OnListener mListener;
        private boolean mAutoDismiss = true;

        private final MenuAdapter mAdapter;

        public Builder(Context context) {
            super(context);

            RecyclerView recyclerView = new RecyclerView(context);
            recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            setContentView(recyclerView);
            mAdapter = new MenuAdapter(getContext());
            mAdapter.setOnItemClickListener(this);
            recyclerView.setAdapter(mAdapter);
            recyclerView.addItemDecoration(new MyItemDecoration(context, 1, R.drawable.shape_divideritem_decoration));
            new ArrowDrawable.Builder(context)
                    .setArrowOrientation(Gravity.TOP)
                    .setArrowGravity(Gravity.RIGHT)
                    .setArrowOffsetY(0)
                    .setShadowSize((int) getResources().getDimension(R.dimen.dp_10))
                    .setBackgroundColor(0xFFFFFFFF)
                    .apply(recyclerView);
        }

        @Override
        public Builder setGravity(int gravity) {
            switch (gravity) {
                // 如果这个是在中间显示的
                case Gravity.CENTER:
                case Gravity.CENTER_VERTICAL:
                    // 重新设置动画
                    setAnimStyle(AnimAction.ANIM_SCALE);
                    break;
                default:
                    break;
            }
            return super.setGravity(gravity);
        }

        public Builder setList(int... ids) {
            List<String> data = new ArrayList<>(ids.length);
            for (int id : ids) {
                data.add(getString(id));
            }
            return setList(data);
        }

        public Builder setList(String... data) {
            return setList(Arrays.asList(data));
        }

        @SuppressWarnings("all")
        public Builder setList(List data) {
            mAdapter.setData(data);
            return this;
        }

        public Builder setAutoDismiss(boolean dismiss) {
            mAutoDismiss = dismiss;
            return this;
        }

        @SuppressWarnings("rawtypes")
        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * {@link BaseAdapter.OnItemClickListener}
         */
        @SuppressWarnings("all")
        @Override
        public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (mListener == null) {
                return;
            }
            mListener.onSelected(getPopupWindow(), position, mAdapter.getItem(position));
        }
    }

    private static final class MenuAdapter extends AppAdapter<Object> {

        private MenuAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        private final class ViewHolder extends AppAdapter<?>.ViewHolder {

            private final TextView mTextView;

            ViewHolder() {
                super(new TextView(getContext()));
                mTextView = (TextView) getItemView();
                mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mTextView.setGravity(Gravity.CENTER_VERTICAL);
                mTextView.setTextColor(getColor(R.color.black50));
                mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.sp_13));
            }

            @Override
            public void onBindView(int position) {
//                mTextView.setText(getItem(position).toString());
//                mTextView.setPadding((int)getResources().getDimension(R.dimen.dp_6),
//                        (int)getResources().getDimension(R.dimen.dp_6),
//                        (int)getResources().getDimension(R.dimen.dp_6),
//                        (int)getResources().getDimension(R.dimen.dp_6));
////                mTextView.setPaddingRelative((int) getResources().getDimension(R.dimen.dp_12),
////                        (position == 0 ? (int) getResources().getDimension(R.dimen.dp_12) : 0),
////                        (int) getResources().getDimension(R.dimen.dp_12),
////                        (int) getResources().getDimension(R.dimen.dp_10));
////                        (int) getResources().getDimension(R.dimen.dp_10));

                mTextView.setText(getItem(position).toString());
                if (position == 0) {//搜一搜
                    Drawable record_start = getResources().getDrawable(R.drawable.icon_bg_device_search);
                    mTextView.setCompoundDrawablesWithIntrinsicBounds(record_start, null, null, null);
                } else if (position == 1) {//扫一扫
                    Drawable record_start = getResources().getDrawable(R.drawable.icon_bg_device_read);
                    mTextView.setCompoundDrawablesWithIntrinsicBounds(record_start, null, null, null);
                } else if (position == 2) {//填一填
                    Drawable record_start = getResources().getDrawable(R.drawable.icon_bg_device_writh);
                    mTextView.setCompoundDrawablesWithIntrinsicBounds(record_start, null, null, null);
                }
                mTextView.setPaddingRelative((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
                mTextView.setScaleX(0.9f);
                mTextView.setScaleY(0.9f);
                mTextView.setCompoundDrawablePadding(22);
                mTextView.setTextColor(getResources().getColor(R.color.color_31bdf3));
            }
        }
    }

    public interface OnListener<T> {

        /**
         * 选择条目时回调
         */
        void onSelected(BasePopupWindow popupWindow, int position, T t);
    }
}