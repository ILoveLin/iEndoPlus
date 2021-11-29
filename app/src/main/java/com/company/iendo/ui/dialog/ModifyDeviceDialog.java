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

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/02/27
 * desc   : 设备输入对话框
 */
public final class ModifyDeviceDialog {

    public static final class Builder
            extends CommonDialog.Builder<Builder>
            implements BaseDialog.OnShowListener,
            TextView.OnEditorActionListener {

        @Nullable
        private OnListener mListener;
        private final ClearEditText mDeviceName, mDeviceCode, mDeviceNoteMessage, mDeviceIP,
                mDeviceAccount, mDevicePassword, mHttpPort, mSocketPort, mLivePort, mMicPort, mDeviceType;

        public Builder(Context context) {
            super(context);
            setCustomView(R.layout.advice_input_dialog);

            mDeviceName = findViewById(R.id.device_name);
            mDeviceCode = findViewById(R.id.device_code);
            mDeviceNoteMessage = findViewById(R.id.cet_cme_note_message);
            mDeviceIP = findViewById(R.id.cet_cme_ip);
            mDeviceAccount = findViewById(R.id.cet_cme_account);
            mDevicePassword = findViewById(R.id.cet_cme_password);
            mHttpPort = findViewById(R.id.http_port);
            mSocketPort = findViewById(R.id.socket_port);
            mLivePort = findViewById(R.id.cet_cme_port);
            mMicPort = findViewById(R.id.cet_cme_mic_port);
            mDeviceType = findViewById(R.id.cet_cme_start_type);

            addOnShowListener(this);
        }


//        public Builder setInputRegex(String regex) {
//            mInputView.setInputRegex(regex);
//            return this;
//        }

        public Builder setListener(OnListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * {@link BaseDialog.OnShowListener}
         */
        @Override
        public void onShow(BaseDialog dialog) {
            postDelayed(() -> showKeyboard(mDeviceAccount), 500);
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
                Editable editable0 = mDeviceName.getText();
                String mDeviceName = editable0 != null ? editable0.toString() : "";
                Editable editable01 = mDeviceCode.getText();
                String mDeviceCode = editable01 != null ? editable01.toString() : "";
                Editable editable02 = mDeviceNoteMessage.getText();
                String mDeviceNoteMessage = editable02 != null ? editable02.toString() : "";
                Editable editable03 = mDeviceIP.getText();
                String mDeviceIP = editable03 != null ? editable03.toString() : "";
                Editable editable04 = mDeviceAccount.getText();
                String mDeviceAccount = editable04 != null ? editable04.toString() : "";
                Editable editable05 = mDevicePassword.getText();
                String mDevicePassword = editable05 != null ? editable05.toString() : "";
                Editable editable06 = mHttpPort.getText();
                String mHttpPort = editable06 != null ? editable06.toString() : "";
                Editable editable07 = mSocketPort.getText();
                String mSocketPort = editable07 != null ? editable07.toString() : "";
                Editable editable08 = mLivePort.getText();
                String mLivePort = editable08 != null ? editable08.toString() : "";
                Editable editable09 = mMicPort.getText();
                String mMicPort = editable09 != null ? editable09.toString() : "";
                Editable editable10 = mDeviceType.getText();
                String mDeviceType = editable10 != null ? editable10.toString() : "";

                mListener.onConfirm(getDialog(), mDeviceName, mDeviceCode, mDeviceNoteMessage, mDeviceIP,
                        mDeviceAccount, mDevicePassword, mHttpPort, mSocketPort, mLivePort, mMicPort, mDeviceType);
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


        /**
         * 1,设备名
         */
        public Builder setDeviceNameContent(@StringRes int id) {
            return setDeviceNameContent(getString(id));
        }

        public Builder setDeviceNameContent(CharSequence text) {
            mDeviceName.setText(text);
            Editable editable = mDeviceName.getText();
            if (editable == null) {
                return this;
            }
            int index = editable.length();
            if (index <= 0) {
                return this;
            }
            mDeviceName.requestFocus();
            mDeviceName.setSelection(index);
            return this;
        }

        /**
         * 2,设备码
         */
        public Builder setDeviceCodeContent(@StringRes int id) {
            return DeviceCodeContent(getString(id));
        }

        public Builder DeviceCodeContent(CharSequence text) {
            mDeviceCode.setText(text);
            Editable editable = mDeviceCode.getText();
            if (editable == null) {
                return this;
            }
            int index = editable.length();
            if (index <= 0) {
                return this;
            }
            mDeviceCode.requestFocus();
            mDeviceCode.setSelection(index);
            return this;
        }

        /**
         * 3,备注信息
         */
        public Builder DeviceNoteContent(@StringRes int id) {
            return DeviceNoteContent(getString(id));
        }

        public Builder DeviceNoteContent(CharSequence text) {
            mDeviceNoteMessage.setText(text);
            Editable editable = mDeviceNoteMessage.getText();
            if (editable == null) {
                return this;
            }
            int index = editable.length();
            if (index <= 0) {
                return this;
            }
            mDeviceNoteMessage.requestFocus();
            mDeviceNoteMessage.setSelection(index);
            return this;
        }

        /**
         * 4,ip
         */
        public Builder DeviceIPContent(@StringRes int id) {
            return DeviceIPContent(getString(id));
        }

        public Builder DeviceIPContent(CharSequence text) {
            mDeviceIP.setText(text);
            Editable editable = mDeviceIP.getText();
            if (editable == null) {
                return this;
            }
            int index = editable.length();
            if (index <= 0) {
                return this;
            }
            mDeviceIP.requestFocus();
            mDeviceIP.setSelection(index);
            return this;
        }

        /**
         * 5,设备账号
         *
         * @param id
         * @return
         */
        public Builder setAccountContent(@StringRes int id) {
            return setAccountContent(getString(id));
        }

        public Builder setAccountContent(CharSequence text) {
            mDeviceAccount.setText(text);
            Editable editable = mDeviceAccount.getText();
            if (editable == null) {
                return this;
            }
            int index = editable.length();
            if (index <= 0) {
                return this;
            }
            mDeviceAccount.requestFocus();
            mDeviceAccount.setSelection(index);
            return this;
        }



        /**
         * 6,设备密码
         *
         * @param id
         * @return
         */
        public Builder setPasswordContent(@StringRes int id) {
            return setPasswordContent(getString(id));
        }

        public Builder setPasswordContent(CharSequence text) {
            mDevicePassword.setText(text);
            Editable editable = mDevicePassword.getText();
            if (editable == null) {
                return this;
            }
            int index = editable.length();
            if (index <= 0) {
                return this;
            }
            mDevicePassword.requestFocus();
            mDevicePassword.setSelection(index);
            return this;
        }

        /**
         * 7,http端口
         *
         * @param id
         * @return
         */
        public Builder setHttpPortContent(@StringRes int id) {
            return setHttpPortContent(getString(id));
        }

        public Builder setHttpPortContent(CharSequence text) {
            mHttpPort.setText(text);
            Editable editable = mHttpPort.getText();
            if (editable == null) {
                return this;
            }
            int index = editable.length();
            if (index <= 0) {
                return this;
            }
            mHttpPort.requestFocus();
            mHttpPort.setSelection(index);
            return this;
        }

        /**
         * 8,socket 端口
         *
         * @param id
         * @return
         */
        public Builder setSocketPortContent(@StringRes int id) {
            return setSocketPortContent(getString(id));
        }

        public Builder setSocketPortContent(CharSequence text) {
            mSocketPort.setText(text);
            Editable editable = mSocketPort.getText();
            if (editable == null) {
                return this;
            }
            int index = editable.length();
            if (index <= 0) {
                return this;
            }
            mSocketPort.requestFocus();
            mSocketPort.setSelection(index);
            return this;
        }

        /**
         * 9,直播 端口
         *
         * @param id
         * @return
         */
        public Builder setLivePortContent(@StringRes int id) {
            return setLivePortContent(getString(id));
        }

        public Builder setLivePortContent(CharSequence text) {
            mLivePort.setText(text);
            Editable editable = mLivePort.getText();
            if (editable == null) {
                return this;
            }
            int index = editable.length();
            if (index <= 0) {
                return this;
            }
            mLivePort.requestFocus();
            mLivePort.setSelection(index);
            return this;
        }

        /**
         * 10,语音 端口
         *
         * @param id
         * @return
         */
        public Builder setMicPortContent(@StringRes int id) {
            return setMicPortContent(getString(id));
        }

        public Builder setMicPortContent(CharSequence text) {
            mMicPort.setText(text);
            Editable editable = mMicPort.getText();
            if (editable == null) {
                return this;
            }
            int index = editable.length();
            if (index <= 0) {
                return this;
            }
            mMicPort.requestFocus();
            mMicPort.setSelection(index);
            return this;
        }

        /**
         * 10,设备类型
         *
         * @param id
         * @return
         */
        public Builder setTypeContent(@StringRes int id) {
            return setTypeContent(getString(id));
        }

        public Builder setTypeContent(CharSequence text) {
            mDeviceType.setText(text);
            Editable editable = mDeviceType.getText();
            if (editable == null) {
                return this;
            }
            int index = editable.length();
            if (index <= 0) {
                return this;
            }
            mDeviceType.requestFocus();
            mDeviceType.setSelection(index);
            return this;
        }

    }

    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(BaseDialog dialog, String mDeviceName, String mDeviceCode, String mDeviceNoteMessage, String mDeviceIP, String mDeviceAccount, String mDevicePassword, String mHttpPort, String mSocketPort, String mLivePort, String mMicPort, String content);

        /**
         * 点击取消时回调
         */
        default void onCancel(BaseDialog dialog) {
        }
    }
}