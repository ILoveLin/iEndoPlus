package com.company.iendo.mineui.offline.fragment;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.utils.LogUtils;

import java.io.File;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 15:40
 * desc：病例列表适配器  离线
 */
public class PictureOfflineAdapter extends AppAdapter<String> {
    private Context mContext;

    public PictureOfflineAdapter(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @NonNull
    @Override
    public PictureOfflineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PictureOfflineAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final AppCompatImageView mImageView;

        private ViewHolder() {
            super(R.layout.item_picture_detail);
            mImageView = findViewById(R.id.pic_image);
        }

        @Override
        public void onBindView(int position) {
            String item = getItem(position);
            File file = new File(item);
            LogUtils.e("图片url==file=url=" + item);

            Glide.with(mContext)
                    .load(file)
                    .placeholder(R.mipmap.bg_splash_des) //占位符 也就是加载中的图片，可放个gif
                    .error(R.mipmap.bg_splash_des)
//                  .signature(new ObjectKey(System.currentTimeMillis()))//不使用缓存
                    .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) getResources().getDimension(R.dimen.dp_4))))
                    .into(mImageView);


        }
    }
}