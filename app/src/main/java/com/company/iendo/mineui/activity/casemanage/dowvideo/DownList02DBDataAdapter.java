package com.company.iendo.mineui.activity.casemanage.dowvideo;

import android.content.Context;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.green.db.downcase.dwonmsg.DownVideoMessage;
import com.company.iendo.utils.FileUtil;

import java.util.ArrayList;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 15:40
 * desc：视频列表进度adapter
 */
public class DownList02DBDataAdapter extends AppAdapter<DownVideoMessage> {
    private ArrayList<DownVideoMessage> mDataLest;

    public DownList02DBDataAdapter(Context context, ArrayList<DownVideoMessage> mDataLest) {
        super(context);
        this.mDataLest = mDataLest;

    }

    @NonNull
    @Override
    public DownList02DBDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownList02DBDataAdapter.ViewHolder();
    }


    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mDownStatueDes, mSpeed, mTitle, mLengthDes;
        private final ProgressBar mProgressBar;
        private final ImageView mIconType;

        private ViewHolder() {
            super(R.layout.item_down_vide_statue);//item_down_vide_statue
            mIconType = findViewById(R.id.iv_down_statue);
            mSpeed = findViewById(R.id.tv_speed);
            mLengthDes = findViewById(R.id.tv_length_des);

            mTitle = findViewById(R.id.tv_title);
            mProgressBar = findViewById(R.id.progressBar);
            mDownStatueDes = findViewById(R.id.tv_statue);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindView(int position) {

            DownVideoMessage item = getItem(position);

            mIconType.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_down_over));


            Long maxProcess = item.getMaxProcess();
            String tag = item.getTag();

            mTitle.setText(tag);
            mDownStatueDes.setText("已完成");
            mProgressBar.setMax(Math.toIntExact(maxProcess));
            mProgressBar.setProgress(Math.toIntExact(maxProcess));
            String maxLength = FileUtil.formatFileSizeMethod(Math.toIntExact(maxProcess));
//                    long processMax01 = item.getProcessMax();
            mSpeed.setText("");
            mLengthDes.setText(maxLength);
        }
    }


}