package com.company.iendo.other;

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
    public static final String Common = "http://192.168.131.114:7001";
//    public static final String Common = "http://192.168.71.41:7001";
//    public static final String Common = "http://192.168.64.28:7001";

    /**
     * 1.1病例列表
     * get
     */
//    http://192.168.64.28:7001/case/list
    public static final String CaseManager_List = Common + "/case/list";


    /**
     * 1.2病例搜索列表
     * get
     */
    public static final String CaseManager_Search = Common + "/case/search";


    /**
     * 1.2病例搜索列表
     * get
     */
    public static final String CaseManager_ListCaseDicts = Common + "/case/listDicts";


    /**
     * 1.5添加病例的
     * post
     */
    public static final String CaseManager_AddCase = Common + "/case/add";

    /**
     * 1.6修改病例的
     * post
     */
    public static final String CaseManager_ChangeCase = Common + "/case/update";
    /**
     * 1.7删除病例的
     * post
     */
    public static final String CaseManager_DeleteCase = Common + "/case/delete";
    /**
     * 1.8病例详情
     * get case
     */
    public static final String CaseManager_CaseInfo = Common + "/case/caseInfo";

    /**
     * 1.9当前用户图片
     * get caseimages
     */
    public static final String CaseManager_CasePictures = Common + "/case/caseimages";
    /**
     * 1.9当前用户视频
     * get casevideos
     */
    public static final String CaseManager_CaseVideos = Common + "/case/casevideos";


    /**
     * 2.0 查询报告医院信息
     * get hospitalInfo
     */
    public static final String CaseManager_CaseHospitalInfo = Common + "/case/hospitalInfo";


    /**
     * 2.1 修改报告医院信息
     * post updateHospitalInfo
     */
    public static final String CaseManager_CaseUpdateHospitalInfo = Common + "/case/updateHospitalInfo";

    /**
     * 2.2 上传医院徽标
     * post uploadHospitalLogo
     */
    public static final String CaseManager_CaseUpdateHospitalLogo = Common + "/case/uploadHospitalLogo";


    /**=======================================================================================*/


    /**
     * 获取用户列表
     * get
     */

    public static final String UserManager_List = Common + "/users/list";


    /**
     * 用户登录
     */

    public static final String UserManager_Login = Common + "/users/login";


    /**
     * 删除用户
     */

    public static final String UserManager_Delete = Common + "/users/deleteUserById";


    /**
     * 修改其他人的密码
     */

    public static final String UserManager_ChangeElsePassword = Common + "/users/changeElsePassword";
    /**
     * 修改自己密码
     */

    public static final String UserManager_ChangeMinePassword = Common + "/users/changeMyselfPassword";
    /**
     * 修改权限
     */
    public static final String UserManager_ChangeRelo = Common + "/users/changePurview";

    /**
     * 修添加新用户
     */
    public static final String UserManager_AddUser = Common + "/users/createUser";


}
