package com.company.iendo.ui.dialog;

import android.content.Context;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.company.iendo.R;
import com.company.iendo.aop.SingleClick;
import com.hjq.base.BaseDialog;
import com.hjq.widget.view.RegexEditText;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/02/27
 * desc   : 输入对话框  --两个输入框
 */
public final class Input2SettingDialog {

    public static final class Builder
            extends CommonDialog.Builder<Builder>
            implements BaseDialog.OnShowListener,
            TextView.OnEditorActionListener {

        @Nullable
        private OnListener mListener;
        private final RegexEditText mInput2ViewLocal,mInput2ViewServer;

        public Builder(Context context) {
            super(context);
            setCustomView(R.layout.input2_setting_dialog);

            mInput2ViewLocal = findViewById(R.id.tv_input_message2_local);
            mInput2ViewServer = findViewById(R.id.tv_input_message2_server);
            mInput2ViewLocal.setOnEditorActionListener(this);
            mInput2ViewServer.setOnEditorActionListener(this);

            addOnShowListener(this);
        }



        public Builder set2Hint(CharSequence text) {
            mInput2ViewLocal.setHint(text);
            return this;
        }



        public Builder set2LocalContent(CharSequence text) {
            mInput2ViewLocal.setText(text);
            Editable editable = mInput2ViewLocal.getText();
            if (editable == null) {
                return this;
            }
            int index = editable.length();
            if (index <= 0) {
                return this;
            }
            mInput2ViewLocal.requestFocus();
            mInput2ViewLocal.setSelection(index);
            return this;
        }

        public Builder set2ServerContent(CharSequence text) {
            mInput2ViewServer.setText(text);
            Editable editable = mInput2ViewServer.getText();
            if (editable == null) {
                return this;
            }
            int index = editable.length();
            if (index <= 0) {
                return this;
            }
            mInput2ViewServer.requestFocus();
            mInput2ViewServer.setSelection(index);
            return this;
        }



        public Builder setInput2Regex(String regex) {
            mInput2ViewLocal.setInputRegex(regex);
            return this;
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
            postDelayed(() -> showKeyboard(mInput2ViewLocal), 500);
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
                Editable editable2Local = mInput2ViewLocal.getText();
                Editable editable2Server = mInput2ViewServer.getText();
                mListener.onConfirm(getDialog(), editable2Local != null ? editable2Local.toString() : "",editable2Server != null ? editable2Server.toString() : "");
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
         * 点击确定时回调
         */
        void onConfirm(BaseDialog dialog,  String content2Local,String content2Server);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {
        }
    }
}