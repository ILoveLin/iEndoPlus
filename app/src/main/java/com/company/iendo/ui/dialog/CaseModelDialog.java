package com.company.iendo.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.aop.SingleClick;
import com.company.iendo.app.AppActivity;
import com.company.iendo.bean.model.LocalDialogCaseModelBean;
import com.company.iendo.bean.model.ModelBean;
import com.company.iendo.mineui.activity.casemanage.AddCaseActivity;
import com.company.iendo.widget.casemodel.ExpandCollapseGroupAdapter;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.base.BaseDialog;
import com.hjq.widget.view.SmartTextView;
import com.sunfusheng.StickyHeaderDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
            View.OnLayoutChangeListener, Runnable {
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
//        private final ExpandableAdapterView mAdapter;
        public static ArrayList<String> mTitleList;
        public static LinkedHashMap<String, ArrayList<ModelBean>> mBeanHashMap;
        public static LinkedHashMap<String, ArrayList<String>> mStringHashMap;
        public static String[][] mItems;

        public Builder(Context context, ArrayList<String> mTitleList, LinkedHashMap<String, ArrayList<ModelBean>> mBeanHashMap, LinkedHashMap<String, ArrayList<String>> mStringHashMap
                , String[][] items) {
            super(context);
            this.mContext = context;
            this.mTitleList = mTitleList;
            this.mBeanHashMap = mBeanHashMap;
            this.mStringHashMap = mStringHashMap;
            this.mItems = items;
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
            mBean = new LocalDialogCaseModelBean();

            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.addItemDecoration(new StickyHeaderDecoration());
            ExpandCollapseGroupAdapter expandableAdapter = new ExpandCollapseGroupAdapter(mContext, mItems);
            mRecyclerView.setAdapter(expandableAdapter);
            //主动关闭所有父节点
            for (int i = 0; i < mTitleList.size(); i++) {
                expandableAdapter.collapseGroup(i, true);
            }
            expandableAdapter.setOnItemClickListener((adapter, data, groupPosition, childPosition) -> {
                if (adapter.isHeader(groupPosition, childPosition)) {
                    if (expandableAdapter.isExpand(groupPosition)) {
                        expandableAdapter.collapseGroup(groupPosition, true);
                    } else {
                        expandableAdapter.expandGroup(groupPosition, true);
                    }
                    if (true) {
                        expandableAdapter.updateItem(groupPosition, childPosition, expandableAdapter.getItem(groupPosition, childPosition));
                    }
                }

                //不是标题被点击
                if (!mTitleList.contains(data) || childPosition != 0) {
                    //获取子Bean
                    String s = mTitleList.get(groupPosition);
                    ArrayList<ModelBean> itemBeanList = mBeanHashMap.get(s);
                    ModelBean itemBean = itemBeanList.get(childPosition - 1);//因为包含了分类,所以减1

                    mMirrorSee.setText("" + itemBean.getSzEndoDesc());
                    mMirrorDiagnostics.setText("" + itemBean.getSzResult());
                    mAdvice.setText("" + itemBean.getSzTherapy());
                    mBean.setMirrorSee(itemBean.getSzEndoDesc() + "");
                    mBean.setMirrorDiagnostics(itemBean.getSzResult() + "");
                    mBean.setAdvice("" + itemBean.getSzTherapy());
                }


            });

            expandableAdapter.setOnItemLongClickListener((adapter, data, groupPosition, childPosition) -> {
            });

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