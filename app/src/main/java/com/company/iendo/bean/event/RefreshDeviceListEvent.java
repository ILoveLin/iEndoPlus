package com.company.iendo.bean.event;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/31 9:30
 * desc：智能搜索界面,设备添加完成!需要刷新设备界面
 */
public class RefreshDeviceListEvent {
    private boolean refresh;

    public RefreshDeviceListEvent(Boolean refresh) {
        this.refresh = refresh;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }
}
