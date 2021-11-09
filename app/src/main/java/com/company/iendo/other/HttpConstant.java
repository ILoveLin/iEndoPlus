package com.company.iendo.other;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 13:46
 * desc：http请求用到的常量
 */
public class HttpConstant {

//71.43

//    public static final String Common = "http://192.168.71.43:7001";
    public static final String Common = "http://192.168.64.28:7001";

    /**
     * 病例列表
     * get
     */
//    http://192.168.64.28:7001/case/list
    public static final String CaseManager_List = Common+"/case/list";


    /**
     * 病例搜索列表
     * get
     */
    public static final String CaseManager_Search = Common+"/case/search";

    /**
     * 获取添加病例的CaseNo
     * get
     */
    public static final String CaseManager_GetCaseNo =Common+"case/caseNo";


    /**
     * 添加病例的
     * post
     */
    public static final String CaseManager_AddCase = Common+"/case/add";
    /**
     * 病例详情
     * get case
     */
    public static final String CaseManager_CaseInfo = Common+"/case/caseInfo";

    /**
     * 当前用户图片
     * get caseimages
     */
    public static final String CaseManager_CasePictures = Common+"/case/caseimages";
    /**
     * 当前用户视频
     * get casevideos
     */
    public static final String CaseManager_CaseVideos = Common+"/case/casevideos";







    /**=======================================================================================*/


    /**
     * 获取用户列表
     * get
     */


    public static final String UserManager_List = Common+"/users/list";










}
