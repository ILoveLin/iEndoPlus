package com.company.iendo.other;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 13:46
 * desc：http请求用到的常量
 */
public class HttpConstant {


    /**
     * 病例列表
     * get
     */
//    http://192.168.64.28:7001/case/list
    public static final String CaseManager_List = " http://192.168.64.28:7001/case/list";


    /**
     * 病例搜索列表
     * get
     */
    public static final String CaseManager_Search = " http://192.168.64.28:7001/case/search";

    /**
     * 获取添加病例的CaseNo
     * get
     */
    public static final String CaseManager_GetCaseNo = " http://192.168.64.28:7001/case/caseNo";


    /**
     * 获取添加病例的CaseNo
     * post
     */
    public static final String CaseManager_AddCase = " http://192.168.64.28:7001/case/add";
    /**
     * 病例详情
     * get case
     */
    public static final String CaseManager_CaseInfo = " http://192.168.64.28:7001/case/caseInfo";

    /**
     * 当前用户图片
     * get caseimages
     */
    public static final String CaseManager_CasePictures = " http://192.168.64.28:7001/case/caseimages";
    /**
     * 当前用户视频
     * get casevideos
     */
    public static final String CaseManager_CaseVideos = " http://192.168.64.28:7001/case/casevideos";


}
