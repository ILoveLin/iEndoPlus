package com.company.iendo.bean.event;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/5/23 17:15
 * desc：刷新用户列表的event
 */
public class RefreshUserListEvent {
    boolean isRefresh = false;

    public RefreshUserListEvent(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }
}
