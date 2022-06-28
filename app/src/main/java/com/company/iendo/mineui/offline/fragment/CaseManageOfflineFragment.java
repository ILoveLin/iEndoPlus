package com.company.iendo.mineui.offline.fragment;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.company.iendo.R;
import com.company.iendo.action.StatusAction;
import com.company.iendo.app.TitleBarFragment;
import com.company.iendo.bean.event.RefreshOfflineCaseListEvent;
import com.company.iendo.bean.event.SocketRefreshEvent;
import com.company.iendo.green.db.CaseDBUtils;
import com.company.iendo.green.db.downcase.CaseDBBean;
import com.company.iendo.green.db.downcase.DownloadedNameListBean;
import com.company.iendo.mineui.activity.MainActivity;
import com.company.iendo.mineui.activity.casemanage.DetailCaseActivity;
import com.company.iendo.mineui.offline.activity.DetailCaseOfflineActivity;
import com.company.iendo.mineui.offline.entitydb.GroupEntity;
import com.company.iendo.other.Constants;
import com.company.iendo.utils.LogUtils;
import com.company.iendo.utils.SharePreferenceUtil;
import com.company.iendo.widget.StatusLayout;
import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.donkingliang.groupedadapter.layoutmanger.GroupedGridLayoutManager;
import com.donkingliang.groupedadapter.widget.StickyHeaderLayout;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/29 13:55
 * desc：tab01 病例管理,离线界面
 */
public class CaseManageOfflineFragment extends TitleBarFragment<MainActivity> implements StatusAction {
    private StatusLayout mStatusLayout;
    private RecyclerView mRecyclerView;
    private StickyHeaderLayout mStickyLayout;
    private CaseOfflineAdapter mAdapter;
    private HashMap<String, ArrayList<CaseDBBean>> mListHashMap;
    private ArrayList<String> keyList;
    public static CaseDBBean currentItemClickDBBean;
    private ArrayList<GroupEntity> mGroupList;

    public static CaseManageOfflineFragment newInstance() {
        return new CaseManageOfflineFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_offline_case_manage;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mStatusLayout = findViewById(R.id.status_hint);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mStickyLayout = (StickyHeaderLayout) findViewById(R.id.sticky_layout);

        getAdapterData();
        mAdapter = new CaseOfflineAdapter(getActivity(), mGroupList);
        mAdapter.setOnHeaderClickListener(new GroupedRecyclerViewAdapter.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder,
                                      int groupPosition) {
                String s = keyList.get(groupPosition);

            }
        });

        mAdapter.setOnChildClickListener(new GroupedRecyclerViewAdapter.OnChildClickListener() {
            @Override
            public void onChildClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder,
                                     int groupPosition, int childPosition) {
                String s = keyList.get(groupPosition);
                ArrayList<CaseDBBean> caseDBBeans = mListHashMap.get(s);
                currentItemClickDBBean = caseDBBeans.get(childPosition);
                Intent intent = new Intent(getActivity(), DetailCaseOfflineActivity.class);

                intent.putExtra("Name", currentItemClickDBBean.getName() + "");
                intent.putExtra("itemID", currentItemClickDBBean.getId() + "");
                startActivity(intent);


            }
        });

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getAttachActivity(), LinearLayoutManager.VERTICAL, false);
        GroupedGridLayoutManager gridLayoutManager = new GroupedGridLayoutManager(getActivity(), 2, mAdapter);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getAdapterData() {
        //获取当前登录者名字
        MMKV mMMKVInstace = MMKV.defaultMMKV();
        mLoginUserName = mMMKVInstace.decodeString(Constants.KEY_CurrentLoginUserName);
        //创建当前登入用户的标识bean
        DownloadedNameListBean  tagBean = new DownloadedNameListBean();
        tagBean.setDownloadedByName(mLoginUserName);
        LogUtils.e("离线列表===tagBean.toString()=="+tagBean.toString());


        mGroupList = new ArrayList<>();
        //查询当前设备码的下载过的病例
        mCurrentReceiveDeviceCode = (String) SharePreferenceUtil.get(getAttachActivity(), SharePreferenceUtil.Current_DeviceCode, "00000000000000000000000000000000");
        List<CaseDBBean> mDBList = CaseDBUtils.getQueryBeanByCode(getActivity(), mCurrentReceiveDeviceCode);
        ArrayList<String> stringsList = new ArrayList<>();

        /**
         * 获取当前登入用户下,下载过的病例集合
         */
        ArrayList<CaseDBBean> mCurrentLoginData = new ArrayList<>();
        for (int i = 0; i < mDBList.size(); i++) {
            CaseDBBean caseDBBean = mDBList.get(i);
            //获取当前这个病例下载者名单列表
            List<DownloadedNameListBean> downloadedNameList = caseDBBean.getDownloadedNameList();

            //包含,就存入
            if (downloadedNameList.contains(tagBean)) {
                mCurrentLoginData.add(caseDBBean);
            }
        }

        for (int i = 0; i < mCurrentLoginData.size(); i++) {
            String check_date = mCurrentLoginData.get(i).getCheck_date();
            stringsList.add(check_date);

        }
        //        [2022-03-23 08:13:16, 2022-03-22 08:21:45, 2022-03-22 08:22:02, 2022-03-23, 2022-03-22]

        //获取当每天的数据集合
        List<String> result = Stream.of(stringsList)
                .flatMap(Collection::stream).distinct().collect(Collectors.toList());
        //创建一个
        mListHashMap = new HashMap<>();
        keyList = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
//                //获取到时间的正确值 2022-03-23
            String tag = result.get(i);
            List<CaseDBBean> dataList = CaseDBUtils.getQueryBeanByTow(getActivity(), mCurrentReceiveDeviceCode, tag);
            int size = dataList.size();

            /**
             * 获取当前登入用户下,下载过的病例集合
             */
            ArrayList<CaseDBBean> mToEntityList = new ArrayList<>();
            for (int i1 = 0; i1 < dataList.size(); i1++) {
                CaseDBBean caseDBBean = dataList.get(i1);
                List<DownloadedNameListBean> downloadedNameList = caseDBBean.getDownloadedNameList();

                //包含,就存入
                if (downloadedNameList.contains(tagBean)) {
                    mToEntityList.add(caseDBBean);
                }
            }

            GroupEntity groupEntity1 = new GroupEntity(tag, "", (ArrayList<CaseDBBean>) mToEntityList);
            mGroupList.add(groupEntity1);
            keyList.add(tag);
            mListHashMap.put(tag, (ArrayList<CaseDBBean>) mToEntityList);
        }


        if (mGroupList.size() == 0) {
            showEmpty();
        } else {
            showComplete();
        }
    }

    @Override
    protected void initData() {

    }

    /**
     * eventbus 刷新socket数据
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void RefreshOfflineCaseListEvent(RefreshOfflineCaseListEvent event) {
        if (event.isRefresh()) {
            getAdapterData();
            mAdapter.setGroups(mGroupList);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListHashMap.clear();
        keyList.clear();
        mGroupList.clear();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }
}
