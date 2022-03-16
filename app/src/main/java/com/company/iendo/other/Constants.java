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
    public static final String BROADCAST_IP = "192.168.135.255";      //广播地址
    //    public static final String BROADCAST_IP = "255.255.255.255";      //广播地址
    //默认值都是一样的
    public static final int BROADCAST_PORT = 7006;               //广播约定端口--->默认值
    public static final int SEND_PORT = 7006;                    //发送端口--->默认值
    public static final int RECEIVE_PORT = 7006;                 //本地监听端口--->默认值


    /**
     * 手动设置本地广播端口的port之后,服务器发送消息到app也是这个port,
     * 也就是说BROADCAST_PORT==RECEIVE_PORT,本地需要开启新的线程socket(广播端口)接收消息
     */
    public static final String KEY_BROADCAST_PORT = "KEY_BROADCAST_PORT";                 //广播约定端口  MMKV的Key
    public static final String KEY_RECEIVE_PORT = "KEY_RECEIVE_PORT";                     //登入后和退出前这个时候监听的 本地监听端口  MMKV的Key
    public static final String KEY_RECEIVE_PORT_BY_SEARCH = "KEY_RECEIVE_PORT_BY_SEARCH";                     //这个是设备搜索的,本地接收端口   MMKV的Key

    public static final String KEY_SOCKET_RECEIVE_FIRST_IN = "KEY_SOCKET_RECEIVE_FIRST_IN";  //第一次开启接收线程  MMKV的Key
    public static final String KEY_SEND_PORT = "KEY_SEND_PORT";                           //发送端口  MMKV的Key



//    public static final String KEY_CURRENT_RECEIVE_PORT = "KEY_CURRENT_RECEIVE_PORT";    //当前设置的,本地监听端口,只要不为空,就在此port上监听数据  MMKV的Key
//    public static final String KEY_CURRENT_BROADCAST_PORT = "KEY_CURRENT_BROADCAST_PORT";    //当前设置的,本地广播发送端口,只要不为空,就在此port上发送广播数据  MMKV的Key





    /**
     * 协议命令cmd-->用来区分那个socket回调的消息
     */
    public static final String UDP_TOAST = "UDP_TOAST";       //提示toast
    public static final String UDP_FD = "FD";                 //网络发现（UDP广播）
    public static final String UDP_FC = "FC";                 //授权接入
    public static final String UDP_HAND = "30";               //握手----所有指令之前必须握手
    public static final String UDP_F0 = "F0";                 //获取当前操作病历号（ID）    获取图片界面,获取当前操作病历号（ID）和当前手机界面进入的ID号相同才能采图和录像
    public static final String UDP_F3 = "F3";                 //冻结与解冻:00冻结，01解冻     ---未调试
    public static final String UDP_12 = "12";                 //新增病历
    public static final String UDP_13 = "13";                 //更新病历
    public static final String UDP_14 = "14";                 //删除病历

    public static final String UDP_15 = "15";                 //新增图片（采图）
    public static final String UDP_F1 = "F1";                 //预览报告
    public static final String UDP_F2 = "F2";                 //打印报告
    public static final String UDP_18 = "18";                 //录像


    /**
     * 填写设备Dialog的设备类型标识
     */


    public static final String Type_FuKeTable = "妇科治疗台";                 //妇科治疗台
    public static final String Type_V1_YiTiJi = "一代一体机";                 //一代一体机
    public static final String Type_EarNoseTable = "耳鼻喉治疗台";            //耳鼻喉治疗台
    public static final String Type_MiNiaoTable = "泌尿治疗台";             //泌尿治疗台




}
