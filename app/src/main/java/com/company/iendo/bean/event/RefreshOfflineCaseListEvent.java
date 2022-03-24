package com.company.iendo.bean.event;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/24 17:32
 * desc：删除当前病例刷新病例列表界面
 */
public class RefreshOfflineCaseListEvent {
    private boolean refresh;

    public RefreshOfflineCaseListEvent(Boolean refresh) {
        this.refresh = refresh;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }
}
