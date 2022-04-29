package com.company.iendo.mineui.activity.casemanage;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.iendo.R;
import com.company.iendo.app.AppAdapter;
import com.company.iendo.bean.DetailDownVideoBean;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.FileUtil;
import com.company.iendo.utils.LogUtils;

import java.util.ArrayList;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 15:40
 * desc：视频下载列表进度adapter
 */
public class DownStatueAdapter extends AppAdapter<DetailDownVideoBean.DataDTO> {
    private ArrayList<DetailDownVideoBean.DataDTO> mDataLest;

    public DownStatueAdapter(Context context, ArrayList<DetailDownVideoBean.DataDTO> mDataLest) {
        super(context);
        this.mDataLest = mDataLest;

    }

    @NonNull
    @Override
    public DownStatueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownStatueAdapter.ViewHolder();
    }


    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mDownStatueDes, mSpeed, mTitle, mLengthDes;
        private final ProgressBar mProgressBar;
        private final ImageView mIconType;

        private ViewHolder() {
            super(R.layout.item_down_vide_statue);
            mSpeed = findViewById(R.id.tv_speed);
            mLengthDes = findViewById(R.id.tv_length_des);
            mIconType = findViewById(R.id.iv_down_statue);
            mTitle = findViewById(R.id.tv_title);
            mProgressBar = findViewById(R.id.progressBar);
            mDownStatueDes = findViewById(R.id.tv_statue);
        }

        @Override
        public void onBindView(int position) {

            DetailDownVideoBean.DataDTO item = getItem(position);
            LogUtils.e("DownStatueActivity====onBindView==item==itemgetDownStatue== " + item.getDownStatue());

            String currentStatue = item.getDownStatue();
            if (null!=currentStatue){
                switch (currentStatue) {
                    case Constants.STATUE_READY://准备完毕
                        long processMax01 = item.getProcessMax();
                        LogUtils.e("DownStatueActivity====onBindView==item=准备完毕=processMax== " + processMax01);
                        LogUtils.e("DownStatueActivity====onBindView==item=准备完毕=item.getFileName()== " + item.getFileName());
                        mProgressBar.setMax((int) processMax01);
                        mTitle.setText(item.getFileName());
                        mIconType.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_down_over));

                        break;
                    case Constants.STATUE_START://开始下载
                        long processMax02 = item.getProcessMax();
                        LogUtils.e("DownStatueActivity====onBindView==item=开始下载=processMax== " + processMax02);
                        mProgressBar.setMax((int) processMax02);
                        mTitle.setText(item.getFileName());
                        break;
                    case Constants.STATUE_ERROR://下载错误
                        String maxLengthError = FileUtil.formatFileSizeMethod((int) item.getProcessMax());
                        mDownStatueDes.setText(item.getDownStatueDes() + "");
                        mSpeed.setText("");
                        mProgressBar.setProgress(0);
                        mLengthDes.setText("0MB/" + maxLengthError);
                        mIconType.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_down_error));

                        break;
                    case Constants.STATUE_COMPLETED://下载成功
                        LogUtils.e("DownStatueActivity====onBindView==item=下载成功=processMax== " + item.getProcessMax());
                        String maxLength = FileUtil.formatFileSizeMethod((int) item.getProcessMax());
                        mDownStatueDes.setText(item.getDownStatueDes() + "");
                        mProgressBar.setMax((int) item.getProcessMax());
                        mProgressBar.setProgress((int) item.getProcessMax());
                        mSpeed.setText("");
                        mLengthDes.setText(maxLength + "/" + maxLength);
                        mIconType.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_down_over));
                        break;
                    case Constants.STATUE_DOWNING://下载中
                        String formatOffsetLength = FileUtil.formatFileSizeMethod((int) item.getProcessOffset());
                        String formatOffMaxLength = FileUtil.formatFileSizeMethod((int) item.getProcessMax());

                        mProgressBar.setMax((int) item.getProcessMax());
                        mProgressBar.setProgress((int) item.getProcessOffset());
                        mDownStatueDes.setText(item.getDownStatueDes() + "");
                        String fileName = item.getFileName();
                        mTitle.setText(item.getFileName());
                        String speed = item.getSpeed();
                        mSpeed.setText(speed + "");
                        mLengthDes.setText(formatOffsetLength + "/" + formatOffMaxLength);
                        mIconType.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_down_dowing));

                        LogUtils.e("DownStatueActivity下载中Adapter=speed== " + speed + ",position==" + position + ",tag==" + fileName);

                        LogUtils.e("DownStatueActivity====onBindView==STATUE_DOWNING==item.getSpeed()== " + item.getSpeed());
                        break;
                    case Constants.STATUE_CANCELED://暂停
                        String formatOffsetLengthStop = FileUtil.formatFileSizeMethod((int) item.getProcessOffset());
                        String formatOffMaxLengthStop = FileUtil.formatFileSizeMethod((int) item.getProcessMax());
                        mProgressBar.setMax((int) item.getProcessMax());
                        mProgressBar.setProgress((int) item.getProcessOffset());
                        mDownStatueDes.setText(item.getDownStatueDes() + "");
                        String fileNameStop = item.getFileName();
                        mTitle.setText(item.getFileName());
                        String speedStop = item.getSpeed();
                        mSpeed.setText(speedStop + "");
                        mLengthDes.setText(formatOffsetLengthStop + "/" + formatOffMaxLengthStop);
                        mIconType.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_down_stop));

                        break;
                    case "":                     //未知
                        break;

                }
            }

        }
    }

    /**
     * 获取当前 数据bean在当前recycleview的position
     *
     * @param tag
     * @return
     */
    public int getCurrentRefreshBeanPosition(ArrayList<DetailDownVideoBean.DataDTO> mDataLest, String tag) {

        for (int i = 0; i < mDataLest.size(); i++) {
            DetailDownVideoBean.DataDTO dataDTO = mDataLest.get(i);
            if (tag.equals(dataDTO.getFileName())) {
                return i;
            }
        }
        return 100;

    }

    /**
     * 设置item 进度条最大值
     *
     * @param tag
     * @return
     */
//    public void setProgressMax(String tag, DetailDownVideoBean.DataDTO bean) {
//        for (int i = 0; i < mDataLest.size(); i++) {
//            DetailDownVideoBean.DataDTO dataDTO = mDataLest.get(i);
//            if (tag.equals(dataDTO.getFileName())) {
//                LogUtils.e("DownStatueActivity====onBindView==item==计算tag== " + tag);
//                LogUtils.e("DownStatueActivity====onBindView==item==计算dataDTO.getFileName()== " + dataDTO.getFileName());
//                LogUtils.e("DownStatueActivity====onBindView==item==计算bean.getProcessMax()== " + bean.getProcessMax());
//
//                dataDTO.setProcessMax(bean.getProcessMax());
//                dataDTO.setDownStatue(bean.getDownStatue());
//                this.setItem(i, dataDTO);
//
//            }
//        }
//    }
    public void setProgressMax(String tag, DetailDownVideoBean.DataDTO bean) {
        for (int i = 0; i < mDataLest.size(); i++) {
            DetailDownVideoBean.DataDTO dataDTO = mDataLest.get(i);
            if (tag.equals(dataDTO.getFileName())) {
                LogUtils.e("DownStatueActivity====onBindView==item==计算tag== " + tag);
                LogUtils.e("DownStatueActivity====onBindView==item==计算dataDTO.getFileName()== " + dataDTO.getFileName());
                LogUtils.e("DownStatueActivity====onBindView==item==计算bean.getProcessMax()== " + bean.getProcessMax());

                dataDTO.setProcessMax(bean.getProcessMax());
                dataDTO.setDownStatue(bean.getDownStatue());
                this.setItem(i, dataDTO);

            }
        }
    }

    /**
     * 暴露用于修改进度值的方法
     *
     * @param progress
     * @param position
     * @param progressStr
     */
    public void setProgress(int progress, int position, String progressStr) {
//        mDataLest.get(position).setProgress(progress);
//        mDataLest.get(position).setDownloadPerSize(progressStr);
        notifyItemChanged(position, 1);
    }


    /**
     * 暴露用于修改按钮文字值的方法
     *
     * @param position
     * @param buttonStr
     */
    public void setButtonStatus(int position, String buttonStr) {
//        mDataLest.get(position).setDownloadPerSize(buttonStr);
        notifyItemChanged(position, 1);
    }


}