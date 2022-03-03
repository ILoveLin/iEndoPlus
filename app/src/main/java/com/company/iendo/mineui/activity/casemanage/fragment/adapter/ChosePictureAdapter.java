package com.company.iendo.mineui.activity.casemanage.fragment.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.bean.PictureChoseBean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 15:40
 * desc：病例列表适配器
 */
public class ChosePictureAdapter extends AppAdapter<PictureChoseBean> {
    private String mID;
    private String mBaseUrl;

    public ChosePictureAdapter(Context context, String mID, String mBaseUrl) {
        super(context);
        this.mID = mID;
        this.mBaseUrl = mBaseUrl;

    }

    @NonNull
    @Override
    public ChosePictureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChosePictureAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {
        public final RelativeLayout mRelativeLayout;
        private final AppCompatImageView mImageView;

        private ViewHolder() {
            super(R.layout.item_picture_chose);
            mImageView = findViewById(R.id.pic_image);
            mRelativeLayout = findViewById(R.id.relative_all);
        }

        @Override
        public void onBindView(int position) {
            PictureChoseBean bean = getItem(position);
//            http://192.168.64.28:7001/ID/ImagePath
//            Log.e("adapter", "item==path==" + "http://192.168.64.28:7001/" + mID + "/" + item.getImagePath());

//            String path =  mBaseUrl + "/" + mID + "/" + item;
//            String url = "http://images.csdn.net/20150817/1.jpg";
//            Log.e("adapter", "item==path==" + "http://192.168.64.56:7001/" + mID + "/" + item);
//            Log.e("adapter", "item==path=mBaseUrl=" + mBaseUrl + "/" + mID + "/" + item);
//            http://192.168.64.56:7001/3/001.jpg
//            mImageView.setText("Path:" + item.getImagePath() + "ID:" + item.getID());
            Glide.with(getContext())
                    .load(bean.getUrl())
                    .error(R.mipmap.bg_loading_error)
                    .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) getResources().getDimension(R.dimen.dp_4))))
                    .into(mImageView);

            if (bean.isSelected()) {
                mRelativeLayout.setBackgroundResource(R.drawable.shape_bg_chose_picture_pre);
            }else {
                mRelativeLayout.setBackgroundResource(R.drawable.shape_bg_chose_picture_nor);
            }

        }
    }
}