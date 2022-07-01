package com.company.iendo.ui.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.view.menu.MenuAdapter;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.aop.SingleClick;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.bean.model.LocalDialogCaseModelBean;
import com.company.iendo.bean.model.Province;
import com.company.iendo.widget.casemodel.ExpandableAdapterView;
import com.company.iendo.widget.casemodel.FloatItemDecoration;
import com.company.iendo.widget.casemodel.OnItemViewClickListener;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.hjq.widget.view.SmartTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/12/2
 * desc   : 菜单选择框
 */
public final class CaseModelDialog {

    public static final class Builder
            extends BaseDialog.Builder<Builder>
            implements
            View.OnLayoutChangeListener, Runnable, OnItemViewClickListener {

        @SuppressWarnings("rawtypes")
        @Nullable
        private OnListener mListener;
        private boolean mAutoDismiss = true;
        private Context mContext;

        private final RecyclerView mRecyclerView;
        private final SmartTextView mCancelView;
        private final AppCompatTextView mConfirmView;

        private final TextView mMirrorSee, mMirrorDiagnostics, mAdvice;
        private LocalDialogCaseModelBean mBean;
        private final ExpandableAdapterView mAdapter;

        public Builder(Context context, ArrayList mDataList) {
            super(context);
            this.mContext = context;
            setContentView(R.layout.dialog_case_modle);
            setAnimStyle(BaseDialog.ANIM_BOTTOM);

            mRecyclerView = findViewById(R.id.rv_menu_list);
            mCancelView = findViewById(R.id.tv_ui_cancel);
            mConfirmView = findViewById(R.id.tv_ui_confirm);
            mMirrorSee = findViewById(R.id.tv_mirror_see);
            mMirrorDiagnostics = findViewById(R.id.tv_mirror_diagnostics);
            mAdvice = findViewById(R.id.tv_advice);

            mMirrorSee.setMovementMethod(ScrollingMovementMethod.getInstance());
            mMirrorDiagnostics.setMovementMethod(ScrollingMovementMethod.getInstance());
            mAdvice.setMovementMethod(ScrollingMovementMethod.getInstance());

            setOnClickListener(mCancelView, mConfirmView);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            //模拟数据
            mAdapter = new ExpandableAdapterView(mDataList);
//            ExpandableAdapterView expandableListAdapter = new ExpandableAdapterView(mDataList);
            mRecyclerView.setAdapter(mAdapter);
            //分割线
//            FloatItemDecoration floatItemDecoration = new FloatItemDecoration(mAdapter.getObjects(), mContext);
//            mRecyclerView.addItemDecoration(floatItemDecoration);
            //监听
            mAdapter.setOnItemViewClickListener(this);
//            mAdapter.setOnUIChangeListener(floatItemDecoration);

            mBean = new LocalDialogCaseModelBean();
        }

        @Override
        public Builder setGravity(int gravity) {
            switch (gravity) {
                // 如果这个是在中间显示的
                case Gravity.CENTER:
                case Gravity.CENTER_VERTICAL:
                    // 不显示取消按钮
//                    setCancel(null);
                    // 重新设置动画
                    setAnimStyle(BaseDialog.ANIM_SCALE);
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
//            mAdapter.setData(data);
            mRecyclerView.addOnLayoutChangeListener(this);
            return this;
        }

        public Builder setCancel(@StringRes int id) {
            return setCancel(getString(id));
        }

        public Builder setCancel(CharSequence text) {
            mCancelView.setText(text);
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


        @SingleClick
        @Override
        public void onClick(View view) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (view == mCancelView) {
                if (mListener == null) {
                    return;
                }
                mListener.onCancel(getDialog());
            }


            if (view == mConfirmView) {
                if (mListener == null) {
                    return;
                }
                mListener.onConfirm(mBean);
                if (mAutoDismiss) {
                    dismiss();
                }
            }
        }


        /**
         * {@link View.OnLayoutChangeListener}
         */
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            mRecyclerView.removeOnLayoutChangeListener(this);
            // 这里一定要加延迟，如果不加在 Android 9.0 上面会导致 setLayoutParams 无效
            post(this);
        }

        @Override
        public void run() {
            final ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
            final int maxHeight = getScreenHeight() / 4 * 3;
            if (mRecyclerView.getHeight() > maxHeight) {
                if (params.height != maxHeight) {
                    params.height = maxHeight;
                    mRecyclerView.setLayoutParams(params);
                }
                return;
            }

            if (params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                mRecyclerView.setLayoutParams(params);
            }
        }

        /**
         * 获取屏幕的高度
         */
        private int getScreenHeight() {
            Resources resources = getResources();
            DisplayMetrics outMetrics = resources.getDisplayMetrics();
            return outMetrics.heightPixels;
        }

        @Override
        public void setOnViewClickListener(View view, int position, Province.City city) {

            mMirrorSee.setText("" + city.getSzEndoDesc());
            mMirrorDiagnostics.setText("" + city.getSzResult());
            mAdvice.setText("" + city.getSzTherapy());
            mBean.setMirrorSee(city.getSzEndoDesc()+"");
            mBean.setMirrorDiagnostics( city.getSzResult()+"");
            mBean.setAdvice("" + city.getSzTherapy());
        }
    }


    public interface OnListener<T> {
        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {
        }

        /**
         * 点击确定时回调
         */
        default void onConfirm(LocalDialogCaseModelBean mBean) {
        }
    }
}