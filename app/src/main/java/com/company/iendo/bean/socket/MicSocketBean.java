package com.company.iendo.bean.socket;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/6/17 9:11
 * desc：
 * ErrCode 代表错误码，取值解释：
 * 0：成功，
 * 1：上传音频流到Nginx失败
 * 2：Nginx服务未启动
 * 3：从Nginx取音频流失败
 * <p>
 * Operation 表示行为动作， 取值解释：
 * 0：表示不做任何事情
 * 1: 请求加入列表（功能开启）
 * 2：请上传音频流到Nginx；
 * 3：请从Nginx拉取音频流
 * 5：通话结束
 * 6：请求从列表中删除（功能关闭）
 * <p>
 * voiceID 表示分配给移动端拨号号码，由设备分配，移动端需保存
 * 255 或者socket请求加入列表后,获取到的id相等才开启视频声音
 */
public class MicSocketBean {

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
