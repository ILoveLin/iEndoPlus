package com.company.iendo.mineui.offline.fragment;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.bean.DetailVideoBean;
import com.company.iendo.bean.OfflineVideoBean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 15:40
 * desc：病例列表适配器   离线
 */
public class VideoOfflineAdapter extends AppAdapter<OfflineVideoBean> {


    public VideoOfflineAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public VideoOfflineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoOfflineAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {


        private final AppCompatTextView mTextView;

        private ViewHolder() {
            super(R.layout.item_video);
            mTextView = findViewById(R.id.tv_video_text);
        }

        @Override
        public void onBindView(int position) {
            OfflineVideoBean bean = getItem(position);
//            http://192.168.64.28:7001/ID/FilePath
            Log.e("adapter", "" + "Path:" +bean.toString());

            mTextView.setText("" + bean.getTitle());

        }
    }
}