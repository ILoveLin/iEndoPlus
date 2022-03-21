package com.company.iendo.bean.socket;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/2 13:56
 * 删除病例 返回的id是十六进制的
 * 新增病例返回的id 和  删除病例一样的 所以公用一个bean
 */
public class DeleteUserBean {
    @SerializedName("recordid")
    private String recordid;

    public String getRecordid() {
        return recordid;
    }

    public void setRecordid(String recordid) {
        this.recordid = recordid;
    }
}
