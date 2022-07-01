package com.company.iendo.bean.model;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/6/29 16:29
 * desc：选择摸个模板之后,需要传出去的数据bean,包含三条信息
 */
public class LocalDialogCaseModelBean {
    private String mMirrorSee;//镜检所见
    private String mMirrorDiagnostics;//镜检诊断
    private String mAdvice;//建议

    @Override
    public String toString() {
        return "LocalCaseModelBean{" +
                "mMirrorSee='" + mMirrorSee + '\'' +
                ", mMirrorDiagnostics='" + mMirrorDiagnostics + '\'' +
                ", mAdvice='" + mAdvice + '\'' +
                '}';
    }

    public String getMirrorSee() {
        return mMirrorSee;
    }

    public void setMirrorSee(String mMirrorSee) {
        this.mMirrorSee = mMirrorSee;
    }

    public String getMirrorDiagnostics() {
        return mMirrorDiagnostics;
    }

    public void setMirrorDiagnostics(String mMirrorDiagnostics) {
        this.mMirrorDiagnostics = mMirrorDiagnostics;
    }

    public String getAdvice() {
        return mAdvice;
    }

    public void setAdvice(String mAdvice) {
        this.mAdvice = mAdvice;
    }
}
