package com.company.iendo.bean.event;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/3 15:51
 * desc：切换病例的时候需要刷新 病例详情的fragment
 */
public class RefreshCaseMsgEvent {
    private String caseID;

    public RefreshCaseMsgEvent(String caseID) {
        this.caseID = caseID;
    }

    public String getCaseID() {
        return caseID;
    }

    public void setCaseID(String caseID) {
        this.caseID = caseID;
    }
}
