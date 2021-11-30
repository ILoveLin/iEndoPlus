package com.company.iendo.ui.dialog;

import android.content.Context;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.company.iendo.R;
import com.company.iendo.aop.SingleClick;
import com.hjq.base.BaseDialog;
import com.hjq.widget.view.ClearEditText;
import com.hjq.widget.view.RegexEditText;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/02/27
 * desc   : 添加用户-输入对话框
 */
public final class InputAddUserDialog {

    public static final class Builder
            extends CommonDialog.Builder<Builder>
            implements BaseDialog.OnShowListener,
            TextView.OnEditorActionListener {

        @Nullable
        private OnListener mListener;
        private final ClearEditText mUserName, mPasswrod, mRelo;

        public Builder(Context context) {
            super(context);
            setCustomView(R.layout.user_add_dialog);

            mUserName = findViewById(R.id.user_name);
            mPasswrod = findViewById(R.id.user_password);
            mRelo = findViewById(R.id.user_relo);

            addOnShowListener(this);
        }


        public Builder setUserNameContent(@StringRes int id) {
            return setUserNameContent(getString(id));
        }

        public Builder setUserNameContent(CharSequence text) {
            mUserName.setText(text);
            return this;
        }

        public Builder setUserPasswordContent(@StringRes int id) {
            return setUserPasswordContent(getString(id));
        }

        public Builder setUserPasswordContent(CharSequence text) {
            mPasswrod.setText(text);
            return this;
        }

        public Builder setReloContent(@StringRes int id) {
            return setReloContent(getString(id));
        }

        public Builder setReloContent(CharSequence text) {
            mRelo.setText(text);
            return this;
        }

        public ClearEditText getRelo(){
            return mRelo;
        }


        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }
        /**
         * {@link BaseDialog.OnShowListener}
         */
        @Override
        public void onShow(BaseDialog dialog) {
            postDelayed(() -> showKeyboard(mUserName), 500);
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
                Editable userName = mUserName.getText();
                Editable passwrod = mPasswrod.getText();
                Editable relo = mRelo.getText();
                mListener.onConfirm(getDialog(), userName.toString(), passwrod.toString(), relo.toString());
            } else if (viewId == R.id.tv_ui_cancel) {
                autoDismiss();
                if (mListener == null) {
                    return;
                }
                mListener.onCancel(getDialog());
            }
        }

        /**
         * {@link TextView.OnEditorActionListener}
         */
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // 模拟点击确认按钮
                onClick(findViewById(R.id.tv_ui_confirm));
                return true;
            }
            return false;
        }
    }

    public interface OnListener {


        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {
        }

        /**
         * 点击确定时回调
         */
        void onConfirm(BaseDialog dialog, String userName, String passwrod, String relo);
    }
}