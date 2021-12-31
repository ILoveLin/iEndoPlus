package com.company.iendo.mineui.activity.casemanage.fragment.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.bean.DetailPictureBean;
import com.company.iendo.bean.DetailVideoBean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 15:40
 * desc：病例列表适配器
 */
public class VideoAdapter extends AppAdapter<DetailVideoBean.DataDTO> {
    private String mID;

    public VideoAdapter(Context context, String mID) {
        super(context);
        this.mID = mID;

    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final AppCompatTextView mTextView;

        private ViewHolder() {
            super(R.layout.item_video);
            mTextView = findViewById(R.id.tv_video_text);
        }

        @Override
        public void onBindView(int position) {
            DetailVideoBean.DataDTO item = getItem(position);
//            http://192.168.64.28:7001/ID/FilePath
            Log.e("adapter", "" + "Path:" + item.getFilePath());

            mTextView.setText("" + item.getFilePath());

        }
    }
}