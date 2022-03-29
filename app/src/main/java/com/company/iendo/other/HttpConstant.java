package com.company.iendo.other;

import com.company.iendo.utils.SharePreferenceUtil;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/3 13:46
 * desc：http请求用到的常量
 */
public class HttpConstant {
    // 角色 0-超级管理员 1-管理员 2-操作员 3-查询员 4-自定义
//71.43

    //    public static final String Common = "http://192.168.71.43:7001";
//    public static final String Common = "http://192.168.1.101:7001";
    public static final String Common = "http://192.168.132.102:7001";     //当前使用的是这个
//    public static final String Common = "http://192.168.131.114:7001";
//    public static final String Common = "http://192.168.71.41:7001";
//    public static final String Common = "http://192.168.64.28:7001";


    public HttpConstant() {

    }

    /**
     * 1.1病例列表
     * get
     */
//    http://192.168.64.28:7001/case/list
    public static final String CaseManager_List ="/case/list";


    /**
     * 1.2病例搜索列表
     * get
     */
    public static final String CaseManager_Search = "/case/search";


    /**
     * 1.2病例搜索列表
     * get
     */
    public static final String CaseManager_ListCaseDicts = "/case/listDicts";

    /**
     * 1.4 病例数据字典
     * post
     */
    public static final String CaseManager_CaseDialogDate = "/case/listDicts";

    /**
     * 1.5添加病例的
     * post
     */
    public static final String CaseManager_AddCase = "/case/add";


    /**
     * 1.6修改病例的
     * post
     */
    public static final String CaseManager_ChangeCase = "/case/update";
    /**
     * 1.7删除病例的
     * post
     */
    public static final String CaseManager_DeleteCase = "/case/delete";
    /**
     * 1.8病例详情
     * get case
     */
    public static final String CaseManager_CaseInfo = "/case/caseInfo";

    /**
     * 1.9当前用户图片
     * get caseimages
     */
    public static final String CaseManager_CasePictures = "/case/caseimages";
    /**
     * 1.9当前用户视频
     * get casevideos
     */
    public static final String CaseManager_CaseVideos = "/case/casevideos";


    /**
     * 2.0 查询报告医院信息
     * get hospitalInfo
     */
    public static final String CaseManager_CaseHospitalInfo = "/case/hospitalInfo";


    /**
     * 2.1 修改报告医院信息
     * post updateHospitalInfo
     */
    public static final String CaseManager_CaseUpdateHospitalInfo = "/case/updateHospitalInfo";

    /**
     * 2.2 上传医院徽标
     * post uploadHospitalLogo
     */
    public static final String CaseManager_CaseUpdateHospitalLogo = "/case/uploadHospitalLogo";
    /**
     * 2.4报告图片选择 (用于打印报告)
     * post uploadHospitalLogo
     */
    public static final String CaseManager_Report = "/report/selectImages";
    /**
     * 2.5 查询服务端是否已经生成报告
     * get uploadHospitalLogo
     */
    public static final String CaseManager_Report_Exists = "/report/reportExists";


    /**=======================================================================================*/


    /**
     * 获取用户列表
     * get
     */

    public static final String UserManager_List = "/users/list";


    /**
     * 用户登录
     */

    public static final String UserManager_Login = "/users/login";


    /**
     * 删除用户
     */

    public static final String UserManager_Delete = "/users/deleteUserById";


    /**
     * 修改其他人的密码
     */

    public static final String UserManager_ChangeElsePassword = "/users/changeElsePassword";
    /**
     * 修改自己密码
     */

    public static final String UserManager_ChangeMinePassword = "/users/changeMyselfPassword";
    /**
     * 修改权限
     */
    public static final String UserManager_ChangeRelo = "/users/changePurview";

    /**
     * 修添加新用户
     */
    public static final String UserManager_AddUser = "/users/createUser";


    /**
     * 获取用户权限
     */
    public static final String UserManager_getCurrentRelo = "/users/purview";


}
