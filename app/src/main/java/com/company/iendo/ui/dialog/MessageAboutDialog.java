package com.company.iendo.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.company.iendo.R;
import com.company.iendo.aop.SingleClick;
import com.company.iendo.manager.CacheDataManager;
import com.hjq.base.BaseDialog;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/12/2
 * desc   : 消息对话框
 */
public final class MessageAboutDialog {

    public static final class Builder
            extends CommonAboutDialog.Builder<Builder> {

        @Nullable
        private OnListener mListener;

        private final TextView tv_name, tv_version, tv_copyright, tv_company, tv_update_date;

        public Builder(Context context) {
            super(context);
            setCustomView(R.layout.setting_about_dialog);
            tv_name = findViewById(R.id.tv_name);
            tv_version = findViewById(R.id.tv_version);
            tv_copyright = findViewById(R.id.tv_copyright);
            tv_company = findViewById(R.id.tv_company);
            tv_update_date = findViewById(R.id.tv_update_date);
//            mMessageView = findViewById(R.id.tv_message_message);
        }

        public Builder setVersion(@StringRes int id) {
            return setVersion(getString(id));
        }

        public Builder setVersion(CharSequence text) {
            tv_version.setText(text);
            return this;
        }

        public Builder setCopyright(@StringRes int id) {
            return setCopyright(getString(id));
        }

        public Builder setCopyright(CharSequence text) {
            tv_copyright.setText(text);
            return this;
        }

        public Builder setCompany(@StringRes int id) {
            return setCompany(getString(id));
        }

        public Builder setCompany(CharSequence text) {
            tv_company.setText(text);
            return this;
        }

        public Builder setUpdateDate(@StringRes int id) {
            return setUpdateDate(getString(id));
        }

        public Builder setUpdateDate(CharSequence text) {
            tv_update_date.setText(text);
            return this;
        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        @Override
        public BaseDialog create() {
            // 如果内容为空就抛出异常
            if ("".equals(tv_name.getText().toString())) {
                throw new IllegalArgumentException("Dialog message not null");
            }
            return super.create();
        }

        @SingleClick
        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.tv_ui_confirm) {
                autoDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onConfirm(getDialog());
            } else if (viewId == R.id.tv_ui_cancel) {
                autoDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onCancel(getDialog());
            }
        }
    }

    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(BaseDialog dialog);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {
        }
    }
}