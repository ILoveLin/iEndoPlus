package com.company.iendo.mineui.activity.casemanage.fragment.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.signature.ObjectKey;
import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.bean.DetailPictureBean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 15:40
 * desc：病例列表适配器
 */
public class PictureAdapter extends AppAdapter<DetailPictureBean.DataDTO> {
    private String mID;
    private String mBaseUrl;
    private String mCurrentChangeImageID;
    private Boolean firstInitAdapter;

    public PictureAdapter(Context context, String mID, String mBaseUrl, String currentImageId, Boolean firstInitAdapter) {
        super(context);
        this.mID = mID;
        this.mBaseUrl = mBaseUrl;
        this.mCurrentChangeImageID = currentImageId;
        this.firstInitAdapter = firstInitAdapter;

    }

    public void setCurrentChangeImageID(String str) {
        this.mCurrentChangeImageID = str;
        notifyDataSetChanged();

//        setItem();
    }

    @NonNull
    @Override
    public PictureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PictureAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final AppCompatImageView mImageView;

        private ViewHolder() {
            super(R.layout.item_picture_detail);
            mImageView = findViewById(R.id.pic_image);
        }

        @Override
        public void onBindView(int position) {
            DetailPictureBean.DataDTO item = getItem(position);
//            http://192.168.64.28:7001/ID/ImagePath
//            Log.e("adapter", "item==path==" + "http://192.168.64.28:7001/" + mID + "/" + item.getImagePath());

            String path = mBaseUrl + "/" + mID + "/" + item.getImagePath();
            String url = "http://images.csdn.net/20150817/1.jpg";
//            http://192.168.64.56:7001/3/001.jpg
//            mImageView.setText("Path:" + item.getImagePath() + "ID:" + item.getID());

            Glide.with(getContext())
                    .load(path)
                    .placeholder(R.mipmap.bg_splash_des) //占位符 也就是加载中的图片，可放个gif
                    .error(R.mipmap.bg_splash_des)
                    .signature(new ObjectKey(System.currentTimeMillis()))//不使用缓存
                    .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) getResources().getDimension(R.dimen.dp_4))))
                    .into(mImageView);

//            if (firstInitAdapter) {
//                Glide.with(getContext())
//                        .load(path)
//                        .placeholder(R.mipmap.bg_splash_des) //占位符 也就是加载中的图片，可放个gif
//                        .error(R.mipmap.bg_splash_des)
//                        .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) getResources().getDimension(R.dimen.dp_4))))
//                        .into(mImageView);
//            }
//
//            if (item.getID().equals(mCurrentChangeImageID)) {
//                Glide.with(getContext())
//                        .load(path)
//                        .placeholder(R.mipmap.bg_splash_des) //占位符 也就是加载中的图片，可放个gif
//                        .error(R.mipmap.bg_splash_des)
//                        .signature(new ObjectKey(System.currentTimeMillis()))//不使用缓存
//                        .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) getResources().getDimension(R.dimen.dp_4))))
//                        .into(mImageView);
//            }

        }
    }
}