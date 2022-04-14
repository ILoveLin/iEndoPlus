package com.company.iendo.bean.event;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/31 9:30
 * desc：搜索结果,点击item需要刷新MAinActivity的itemID
 */
public class RefreshItemIdEvent {
    private boolean refresh;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RefreshItemIdEvent(Boolean refresh) {
        this.refresh = refresh;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }
}
