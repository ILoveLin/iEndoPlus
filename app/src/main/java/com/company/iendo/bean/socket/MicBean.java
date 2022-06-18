package com.company.iendo.bean.socket;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/6/17 8:47
 * desc：最新的语言通话Bean
 * @author Administrator
 */
public class MicBean {

    @SerializedName("ErrCode")
    private String errCode;
    @SerializedName("Operation")
    private String operation;
    private String voiceID;
    @SerializedName("StringParam")
    private String stringParam;
    private String url;

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getVoiceID() {
        return voiceID;
    }

    public void setVoiceID(String voiceID) {
        this.voiceID = voiceID;
    }

    public String getStringParam() {
        return stringParam;
    }

    public void setStringParam(String stringParam) {
        this.stringParam = stringParam;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
