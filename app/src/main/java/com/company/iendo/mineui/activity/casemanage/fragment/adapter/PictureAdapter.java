package com.company.iendo.mineui.activity.casemanage.fragment.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.bean.DetailPictureBean;
import com.company.iendo.bean.SearchListBean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 15:40
 * desc：病例列表适配器
 */
public class PictureAdapter extends AppAdapter<DetailPictureBean.DataDTO> {
    private String mID;

    public PictureAdapter(Context context, String mID) {
        super(context);
        this.mID = mID;

    }

    @NonNull
    @Override
    public PictureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PictureAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final AppCompatImageView mImageView;

        private ViewHolder() {
            super(R.layout.item_picture);
            mImageView = findViewById(R.id.pic_image);
        }

        @Override
        public void onBindView(int position) {
            DetailPictureBean.DataDTO item = getItem(position);
//            http://192.168.64.28:7001/ID/ImagePath
            Log.e("adapter", "item==path==" + "http://192.168.64.28:7001/" + mID + "/" + item.getImagePath());

            String path = "http://192.168.64.28:7001/" + mID + "/" + item.getImagePath();
//            mImageView.setText("Path:" + item.getImagePath() + "ID:" + item.getID());
            Glide.with(getContext())
                    .load(path)
                    .error(R.mipmap.icon_case_btn)
                    .into(mImageView);
        }
    }
}