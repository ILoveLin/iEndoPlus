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


    public static final String BROADCASTER = "szcme";                //广播发起者名称--暂时固定szcme
    //    public static final String BROADCAST_IP = "192.168.135.255";      //广播地址
    public static final String BROADCAST_IP = "255.255.255.255";      //广播地址
    //默认值都是一样的
    public static final int BROADCAST_PORT = 7006;               //广播约定端口--->默认值
    public static final int SEND_PORT = 7006;                    //发送端口--->默认值
    public static final int RECEIVE_PORT = 7006;                 //本地监听端口--->默认值


    /**
     * 手动设置本地广播端口的port之后,服务器发送消息到app也是这个port,
     * 也就是说BROADCAST_PORT==RECEIVE_PORT,本地需要开启新的线程socket(广播端口)接收消息
     */

    //广播约定端口  只有在设备搜索的时候进行了设置才会更改这个值                                           MMKV的Key
    public static final String KEY_BROADCAST_PORT = "KEY_BROADCAST_PORT";
    //(登入后------退出前)这个时间段监听的本地监听端口,并且这个值,是时刻更新的不管是搜索还是登入之后          MMKV的Key
    public static final String KEY_RECEIVE_PORT = "KEY_RECEIVE_PORT";
    //(设备搜索的时候)监听的本地监听端口,并且这个值,只在搜索的时候设置端口变化                              MMKV的Key
    public static final String KEY_RECEIVE_PORT_BY_SEARCH = "KEY_RECEIVE_PORT_BY_SEARCH";
    //第一次开启接收线程 避免初始化的时候开启多次线程                                                    MMKV的Key
    public static final String KEY_SOCKET_RECEIVE_FIRST_IN = "KEY_SOCKET_RECEIVE_FIRST_IN";


    /**
     * 协议命令cmd-->用来区分那个socket回调的消息
     */
    public static final String UDP_FD = "FD";                 //网络发现（UDP广播）
    public static final String UDP_FC = "FC";                 //授权接入
    public static final String UDP_HAND = "30";               //握手----所有指令之前必须握手
    public static final String UDP_F0 = "F0";                 //获取当前操作病历号（ID）    获取图片界面,获取当前操作病历号（ID）和当前手机界面进入的ID号相同才能采图和录像
    public static final String UDP_F3 = "F3";                 //冻结与解冻:00冻结，01解冻     ---未调试
    public static final String UDP_12 = "12";                 //新增病历
    public static final String UDP_13 = "13";                 //更新病历     本地监听,到这个消息病例列表需要重新请求数据,病例详情界面如果当前的回调的caseid==当前操作id,这个界面也需要刷新
    public static final String UDP_14 = "14";                 //删除病历
    public static final String UDP_CUSTOM14 = "UDP_CUSTOM14";    //自定义命令     在图像采集界面,接受到删除病例,需要退到病例列表界面而不是回退病例详情界面
    public static final String UDP_15 = "15";                 //新增图片（采图）
    public static final String UDP_F1 = "F1";                 //预览报告
    public static final String UDP_F2 = "F2";                 //打印报告
    public static final String UDP_17 = "17";                 //编辑图片
    public static final String UDP_18 = "18";                 //录像    //录像--->0：查询录像状态 1：开始录像，，(我的命令)2：停止录像，(我的命令)3：正在录像，(后台返回操作)  4：未录像(后台返回操作)
    public static final String UDP_F5 = "F5";                 //查询 设备参数
    public static final String UDP_F6 = "F6";                 //设置 设备参数


    /**
     * 填写设备Dialog的设备类型标识
     */
    public static final String Type_FuKeTable = "妇科治疗台";                 //妇科治疗台
    public static final String Type_V1_YiTiJi = "一代一体机";                 //一代一体机
    public static final String Type_EarNoseTable = "耳鼻喉治疗台";            //耳鼻喉治疗台
    public static final String Type_MiNiaoTable = "泌尿治疗台";             //泌尿治疗台


    /**
     * 协议里面设备类型,文档里面和传输都用16进制表示
     *  int用十六进制表示
     * 统一用十六进制表示 协议返回的也是16进制
     */

    public static final int Type_00 = 0x00;     //"工作站";
    public static final int Type_01 = 0x01;     //"HD3摄像机";
    public static final int Type_02 = 0x02;     //冷光源
    public static final int Type_03 = 0x03;     //气腹机
    public static final int Type_04 = 0x04;     //冲洗机
    public static final int Type_05 = 0x05;     //4K摄像机
    public static final int Type_06 = 0x06;     //耳鼻喉控制板
    public static final int Type_07 = 0x07;     //一代一体机
    public static final int Type_08 = 0x08;     //耳鼻喉治疗台
    public static final int Type_09 = 0x09;     //妇科治疗台
    public static final int Type_0A = 0x0A;     //泌尿治疗台
    public static final int Type_A0 = 0xA0;     //iOS
    public static final int Type_A1 = 0xA1;     //Android

}
