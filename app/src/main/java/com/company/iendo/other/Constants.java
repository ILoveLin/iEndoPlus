package com.company.iendo.other;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/28 14:15
 * desc：
 */
public class Constants {
    //引导页
    public static final String SP_IS_FIRST_IN = "sp_is_first_in";  //是否第一次登入
    public static final String Sp_UserAgreement_Tag = "sp_useragreement_tag";  //用户是否同意了----用户协议的标识,默认flase
    public static final String Is_Logined = "is_logined";          //是否已经登入   false  未登录


    public static final String IS_Admin = "IS_Admin";          //是否创建了admin用户

    //系统和请求头
    public static final String Token = "token";
    public static final String Device = "android";

    public static final String BROADCASTER = "szcme";                //广播发起者名称--暂时固定szcme
    public static final String BROADCAST_IP = "255.255.255.255";      //广播地址
    public static final int BROADCAST_PORT = 8005;               //广播约定端口
    public static final int RECEIVE_PORT = 8005;                 //本地监听端口


    /**
     * 协议命令cmd-->用来区分那个socket回调的消息
     */
    public static final String UDP_FD = "FD";                 //网络发现（UDP广播）
    public static final String UDP_FC = "FC";                 //授权接入


}
