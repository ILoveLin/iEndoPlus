package com.company.iendo.mineui.activity.casemanage.dowvideo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.bean.DetailDownVideoBean;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 15:40
 * desc：视频列表
 */
public class DownVideoSelectedAdapter extends AppAdapter<DetailDownVideoBean.DataDTO> {


    public DownVideoSelectedAdapter(Context context) {
        super(context);


    }


    @NonNull
    @Override
    public DownVideoSelectedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownVideoSelectedAdapter.ViewHolder();
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mTitle, mDownDes;
        private final AppCompatCheckBox mCheckBox;

        private ViewHolder() {
            super(R.layout.item_down_vide);
            mTitle = findViewById(R.id.tv_title);
            mDownDes = findViewById(R.id.tv_isdown);
            mCheckBox = findViewById(R.id.checkbox_down);
        }

        @Override
        public void onBindView(int position) {
            DetailDownVideoBean.DataDTO item = getItem(position);
            mTitle.setText("" + item.getFilePath());
            mCheckBox.setVisibility(View.VISIBLE);
            if (item.isSelected()) {
                mCheckBox.setChecked(true);
            } else {
                mCheckBox.setChecked(false);
            }
            mCheckBox.setVisibility(View.VISIBLE);
            //数据库存在
            if (item.isDowned()) {
                mDownDes.setText("已下载");
                mCheckBox.setVisibility(View.GONE);

            } else {
                //downStatue; //状态值:成功或者失败   COMPLETED:成功, ERROR:失败,  START:开始,  DOWNING:下载中
                if ("DOWNING".equals(item.getDownStatue())) {
                    mDownDes.setText("下载中");
                    mCheckBox.setVisibility(View.GONE);
                } else if ("COMPLETED".equals(item.getDownStatue())) {
                    mDownDes.setText("已下载");
                    mCheckBox.setVisibility(View.GONE);
                } else if ("START".equals(item.getDownStatue())) {
                    mDownDes.setText("等待中");
                    mCheckBox.setVisibility(View.GONE);
                } else if ("ERROR".equals(item.getDownStatue())) {
                    mDownDes.setText("下载失败");
                    mCheckBox.setVisibility(View.VISIBLE);
                } else {
                    mDownDes.setText("未下载");
                    mCheckBox.setVisibility(View.VISIBLE);
                }

            }


//
//
//            boolean inQueue = item.isInQueue();
//            if (inQueue) {
//                if ("下载中".equals(item.getDownStatueDes())){
//                    mDownDes.setText("下载中");
//
//                }else if ("已下载".equals(item.getDownStatueDes())){
//                    mDownDes.setText("已下载");
//                }else {
//                    mDownDes.setText("等待中");
//                }
//            } else {
//                if (!item.isDowned()) {
//                    mDownDes.setText("未下载");
//                    //不为空说明正在下载
//                    if (null != item.getDownStatueDes()) {
//                        mDownDes.setText(item.getDownStatueDes() + "");
//                    }
//
//                } else {
//                    mDownDes.setText("已下载");
//                    //不为空说明正在下载
//                    if (null != item.getDownStatueDes()) {
//                        mDownDes.setText(item.getDownStatueDes() + "");
//                    }
//
//                }
//            }


//            if ("未下载".equals(mDownDes.getText().toString())) {
//                mCheckBox.setVisibility(View.VISIBLE);
//                mDownDes.setVisibility(View.VISIBLE);
//            } else {
//                mCheckBox.setVisibility(View.INVISIBLE);
//                mDownDes.setVisibility(View.VISIBLE);
//
//            }


        }
    }
}