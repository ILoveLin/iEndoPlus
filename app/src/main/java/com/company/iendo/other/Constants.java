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
     * MMKV  存储的Key
     */

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
     * 登录成功之后,存储用户权限说明
     * UserMan 			    -- 用户管理
     * CanPsw 			    -- 设置口令
     * SnapVideoRecord	    -- 拍照录像
     * CanNew 			    -- 登记病人
     * CanEdit 			    -- 修改病历
     * CanDelete 			-- 删除病历
     * CanPrint 			-- 打印病历
     * UnPrinted 			-- 未打印病历,病例详情返回过来的时候是状态值,和这里的不同,这里表示是否具有该权限,true表示打印过了不能编辑
     * OnlySelf 			-- 本人病历,病例详情返回过来的时候是状态值,和这里的不同,这里表示是否具有该权限,
     * HospitalInfo 		-- 医院信息
     */


    public static final String KEY_UserMan = "KEY_UserMan";             //用户管理(用户管理界面能不能进)
    public static final String KEY_CanPsw = "KEY_CanPsw";             //设置口令(修改别人密码)
    public static final String KEY_SnapVideoRecord = "KEY_SnapVideoRecord";      //拍照录像
    public static final String KEY_CanNew = "KEY_CanNew";              //登记病人(新增病人)
    public static final String KEY_CanEdit = "KEY_CanEdit";            //修改病历
    public static final String KEY_CanDelete = "KEY_CanDelete";        //删除病历
    //这三个权限都是和是否能修改病例挂钩
    public static final String KEY_CanPrint = "KEY_CanPrint";          //打印病历
    public static final String KEY_UnPrinted = "KEY_UnPrinted";        //未打印病历    是否具有该权限 true的时候就需要去判断编辑病例返回的Printed
    public static final String KEY_OnlySelf = "KEY_OnlySelf";           //本人病历     是否具有该权限 true的时候就需要去判断编辑病例返回的UserName
    public static final String KEY_HospitalInfo = "KEY_HospitalInfo";      //医院信息(不能进入医院信息界面)


    /**
     * 登录成功之后,存储当前设备信息
     */


    public static final String KEY_Device_Ip = "KEY_Device_Ip";      //选中设备的ip
    public static final String KEY_Device_Type_Num = "KEY_Device_Type_Num";      //选中设备的类型,此处是数字比如07
    public static final String KEY_Login_Tag = "KEY_Login_Tag";      //选中设备的时候,是否登入成功
    public static final String KEY_Device_SocketPort = "KEY_Device_SocketPort";      //选中设备的port
    public static final String KEY_DeviceCode = "KEY_DeviceCode";      //选中的设备码

    /**
     * 接受线程需要实时获取的数据
     */
    public static final String KEY_CurrentCaseID = "KEY_CurrentCaseID";                     //当前选中的病例ID
    public static final String KEY_CurrentLoginUserName = "KEY_CurrentLoginUserName";            //当前登录的用户名


    /**
     * toast 提示语
     */

    public static final String UDP_CASE_ID_DIFFERENT = "两设备之间选择病历不一致，请重新确认";                 //当前病例ID和操作病例ID不相等,不能操作!
    public static final String HAVE_NO_PERMISSION = "暂无权限";
//    public static final String HAVE_HAND_FAIL = "暂无权限";
    public static final String HAVE_HAND_FAIL_OFFLINE = "远程设备连接失败,信息可能无法同步";


    /**
     * 协议命令cmd-->用来区分那个socket回调的消息
     */
    public static final String UDP_FD = "FD";                    //网络发现（UDP广播）
    public static final String UDP_FC = "FC";                    //授权接入
    public static final String UDP_HAND = "30";                  //握手----所有指令之前必须握手
    public static final String UDP_F0 = "F0";                    //获取当前操作病历号（ID）    获取图片界面,获取当前操作病历号（ID）和当前手机界面进入的ID号相同才能采图和录像
    public static final String UDP_F3 = "F3";                    //冻结与解冻:00冻结，01解冻     ---未调试
    public static final String UDP_12 = "12";                    //新增病历
    public static final String UDP_13 = "13";                    //更新病历     本地监听,到这个消息病例列表需要重新请求数据,病例详情界面如果当前的回调的caseid==当前操作id,这个界面也需要刷新
    public static final String UDP_14 = "14";                    //删除病历
    public static final String UDP_15 = "15";                    //新增图片（采图）
    public static final String UDP_16 = "16";                    //删除图片
    public static final String UDP_20 = "20";                    //删除视频
    public static final String UDP_F1 = "F1";                    //预览报告
    public static final String UDP_F2 = "F2";                    //打印报告
    public static final String UDP_17 = "17";                    //编辑图片
    public static final String UDP_18 = "18";                    //录像    //录像--->0：查询录像状态 1：开始录像，，(我的命令)2：停止录像，(我的命令)3：正在录像，(后台返回操作)  4：未录像(后台返回操作)
    public static final String UDP_F5 = "F5";                    //查询 设备参数
    public static final String UDP_F6 = "F6";                    //设置 设备参数
    public static final String UDP_F4 = "F4";                    //语音接入
    public static final String UDP_F7 = "F7";                    //通知权限变动    //相同用户名的时候 重新刷新权限
    public static final String UDP_FE = "FE";                    //程序退出命令  -->退出登录的时候发消息
    public static final String UDP_40 = "40";                    //刷新医院信息  -->从新请求数据库刷新界面

    public static final String UDP_CUSTOM14 = "UDP_CUSTOM14";                    //自定义命令     在图像采集界面,接受到删除病例,需要退到病例列表界面而不是回退病例详情界面
    public static final String UDP_CUSTOM_FINISH = "UDP_CUSTOM_FINISH";          //自定义命令     结束DetailCaseActivity界面
    public static final String UDP_CUSTOM_TOAST = "UDP_CUSTOM_TOAST";            //自定义命令     toast
    public static final String UDP_CUSTOM_RESTART = "UDP_CUSTOM_RESTART";        //自定义命令     监听线程异常需要重启
    public static final String UDP_CUSTOM_DOWN_OVER = "UDP_CUSTOM_DOWN_OVER";    //自定义命令     图片下载完成


    /**
     * 填写设备Dialog的设备类型标识
     */
    public static final String Type_FuKeTable = "妇科治疗台";                 //妇科治疗台
    public static final String Type_V1_YiTiJi = "一代一体机";                 //一代一体机
    public static final String Type_EarNoseTable = "耳鼻喉治疗台";            //耳鼻喉治疗台
    public static final String Type_MiNiaoTable = "泌尿治疗台";             //泌尿治疗台


    /**
     * 协议里面设备类型,文档里面和传输都用16进制表示
     * int用十六进制表示
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
