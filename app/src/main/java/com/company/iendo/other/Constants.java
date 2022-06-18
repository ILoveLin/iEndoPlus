package com.company.iendo.other;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/10/28 14:15
 * desc：
 */
public class Constants {
    //引导页
    public static final String SP_IS_FIRST_IN = "sp_is_first_in";                                 //是否第一次登入
    public static final String Sp_UserAgreement_Tag = "sp_useragreement_tag";                     //用户是否同意了----用户协议的标识,默认flase
    public static final String Is_Logined = "is_logined";                                         //是否已经登入   false  未登录

    /**
     * #### 神州医疗Socket通讯端口配置信息
     * #### CMEPlayer项目
     * #### 广播服务端口8006
     * #### 本地监听端口8005
     * #### Socket通讯服务器端口授权设备返回的socket端口值-->Stp:socke udp接收端口;
     * #### 不管是广播还是socket通讯,本地都是监听默认值端口
     * ####
     * ####
     * #### iEndo项目
     * #### 广播服务端口7006
     * #### 本地监听端口7005
     * #### Socket通讯服务器端口授权设备返回的socket端口值-->Stp:socke udp接收端口;
     * #### 不管是广播还是socket通讯,本地都是监听默认值端口
     */
    public static final String BROADCASTER = "szcme";                                             //广播发起者名称--暂时固定szcme
    //    public static final String BROADCAST_IP = "192.168.135.255";                            //广播地址
    public static final String BROADCAST_IP = "255.255.255.255";                                  //广播地址
    //默认值都是一样的
    public static final int BROADCAST_SERVER_PORT = 7006;                                         //广播服务端端口--->默认值
    public static final int LOCAL_RECEIVE_PORT = 7005;                                            //本地监听端口--->默认值

    /**
     * MMKV  存储的Key
     * 说明:
     * 未登录的情况下-->iEnd项目本地监听端口设置为默认值7005,服务端通讯端口默认值设置为7006
     * <p>
     * 登录的情况下-->iEnd项目本地监听端口设置为授权之后返回的(Stp:socke udp接收端口；),并且服务端通讯和本地监听都是公用这个端口
     * <p>
     * 需要注意的是,退出的时候,需要手动吧KEY_LOCAL_RECEIVE_PORT的值设置为默认值7005,再切换监听线程
     */

    //广播约定端口  iend设定为7006 CMEPlayer设定为8005 -->广播的时候本地监听端口可以随意,但是发送固定的     MMKV的Key
    public static final String KEY_BROADCAST_SERVER_PORT = "KEY_BROADCAST_SERVER_PORT";

    //本地监听的端口                                                                                  MMKV的Key
    public static final String KEY_LOCAL_RECEIVE_PORT = "KEY_LOCAL_RECEIVE_PORT";

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


    public static final String KEY_UserMan = "KEY_UserMan";                                       //用户管理(用户管理界面能不能进)
    public static final String KEY_CanPsw = "KEY_CanPsw";                                         //设置口令(修改别人密码)
    public static final String KEY_SnapVideoRecord = "KEY_SnapVideoRecord";                       //拍照录像
    public static final String KEY_CanNew = "KEY_CanNew";                                         //登记病人(新增病人)
    public static final String KEY_CanEdit = "KEY_CanEdit";                                       //修改病历
    public static final String KEY_CanDelete = "KEY_CanDelete";                                   //删除病历
    //这三个权限都是和是否能修改病例挂钩
    public static final String KEY_CanPrint = "KEY_CanPrint";                                     //打印病历
    public static final String KEY_UnPrinted = "KEY_UnPrinted";                                   //未打印病历    是否具有该权限 true的时候就需要去判断编辑病例返回的Printed
    public static final String KEY_OnlySelf = "KEY_OnlySelf";                                     //本人病历     是否具有该权限 true的时候就需要去判断编辑病例返回的UserName
    public static final String KEY_HospitalInfo = "KEY_HospitalInfo";                             //医院信息(不能进入医院信息界面)


    /**
     * 登录成功之后,存储当前设备信息
     */


    public static final String KEY_Device_Ip = "KEY_Device_Ip";                                   //选中设备的ip
    public static final String KEY_Device_Type_Num = "KEY_Device_Type_Num";                       //选中设备的类型,此处是数字比如07
    public static final String KEY_Login_Tag = "KEY_Login_Tag";                                   //选中设备的时候,是否登入成功
    public static final String KEY_Device_SocketPort = "KEY_Device_SocketPort";                   //选中设备的port
    public static final String KEY_DeviceCode = "KEY_DeviceCode";                                 //选中的设备码

    /**
     * 接受线程需要实时获取的数据
     */
    public static final String KEY_CurrentCaseID = "KEY_CurrentCaseID";                           //当前选中的病例ID
    public static final String KEY_CurrentLongSeeCaseID = "KEY_CurrentLongSeeCaseID";             //当前需要长显的病例ID
    public static final String KEY_CurrentLoginUserName = "KEY_CurrentLoginUserName";             //当前登录的用户名

    /**
     * DetailFragment  界面下载图片的时候,网速不好的情况下,退出详情界面,会getActivity获取不到上下文  空指针异常
     */

    public static final String KEY_Picture_Downing = "KEY_Picture_Downing";                       //图片下载中的标识,  true表示下载中,默认是false

    /**
     * toast 提示语
     */

    public static final String UDP_CASE_ID_DIFFERENT = "当前检查病人非本病历病人,不允许截图!\n请与检查室确认当前检查病人信息!";                 //当前病例ID和操作病例ID不相等,不能操作!
    public static final String HAVE_NO_PERMISSION = "暂无权限";
    public static final String HAVE_HAND_FAIL_OFFLINE = "远程设备连接失败,信息可能无法同步";


    /**
     * 与上位机互联,长显信息提示语
     */
    public static final String CONNECT_STATUE_OK = "服务器主机通讯已连接!";
    //正在检查的病人:  szcme27 | 测试 | 男
    public static final String CONNECT_STATUE_CURRENT_CASE_INFO = "正在检查的病人";
    public static final String CONNECT_STATUE_CHECKROOM_MIN_ONLINE = "与检查室通话保持连接中";
    public static final String CONNECT_STATUE_CHECKROOM_PICTURE_OFFLINE = "检查室图像已停止连接...";
    public static final String SOCKET_STATUE_ONLINE = "远程设备通讯已连接";
    public static final String SOCKET_STATUE_OFFLINE = "远程设备通讯已断开";


    /**
     * 协议命令cmd-->用来区分那个socket回调的消息
     */
    public static final String UDP_FD = "FD";                              //网络发现（UDP广播）
    public static final String UDP_FC = "FC";                              //授权接入
    public static final String UDP_HAND = "30";                            //握手----所有指令之前必须握手
    public static final String UDP_F0 = "F0";                              //获取当前操作病历号（ID）    获取图片界面,获取当前操作病历号（ID）和当前手机界面进入的ID号相同才能采图和录像
    public static final String UDP_F3 = "F3";                              //冻结与解冻:00冻结，01解冻     ---未调试
    public static final String UDP_12 = "12";                              //新增病历
    public static final String UDP_13 = "13";                              //更新病历     本地监听,到这个消息病例列表需要重新请求数据,病例详情界面如果当前的回调的caseid==当前操作id,这个界面也需要刷新
    public static final String UDP_14 = "14";                              //删除病历
    public static final String UDP_15 = "15";                              //新增图片（采图）
    public static final String UDP_16 = "16";                              //删除图片
    public static final String UDP_20 = "20";                              //删除视频
    public static final String UDP_F1 = "F1";                              //预览报告
    public static final String UDP_F2 = "F2";                              //打印报告
    public static final String UDP_17 = "17";                              //编辑图片
    public static final String UDP_18 = "18";                              //录像    //录像--->0：查询录像状态 1：开始录像，，(我的命令)2：停止录像，(我的命令)3：正在录像，(后台返回操作)  4：未录像(后台返回操作)
    public static final String UDP_F5 = "F5";                              //查询 设备参数
    public static final String UDP_F6 = "F6";                              //设置 设备参数
    public static final String UDP_41 = "41";                              //语音接入
    public static final String UDP_42 = "42";                              //语音广播通知命令,监听到重新获取vioceID
    public static final String UDP_43 = "43";                              //登录命令：0x43
//    public static final String UDP_F4 = "F4";                              //语音接入
    public static final String UDP_F7 = "F7";                              //通知权限变动    //相同用户名的时候 重新刷新权限
    public static final String UDP_FE = "FE";                              //程序退出命令  -->退出登录的时候发消息
    public static final String UDP_40 = "40";                              //刷新医院信息  -->从新请求数据库刷新界面


    public static final String UDP_CUSTOM14 = "UDP_CUSTOM14";                           //自定义命令     在图像采集界面,接受到删除病例,需要退到病例列表界面而不是回退病例详情界面
    public static final String UDP_CUSTOM_FINISH = "UDP_CUSTOM_FINISH";                 //自定义命令     结束DetailCaseActivity界面
    public static final String UDP_CUSTOM_TOAST = "UDP_CUSTOM_TOAST";                   //自定义命令     toast
    public static final String UDP_CUSTOM_RESTART = "UDP_CUSTOM_RESTART";               //自定义命令     监听线程异常需要重启
    public static final String UDP_CUSTOM_DOWN_OVER = "UDP_CUSTOM_DOWN_OVER";           //自定义命令     图片下载完成

    public static final String KET_MIC_CURRENT_VOICE_ID = "KET_MIC_CURRENT_VOICE_ID";   //语音通话当前的voiceID,默认255,上位机当前需要播放声音的ID
    public static final String KET_MIC_VOICE_ID_FOR_ME = "KET_MIC_VOICE_ID_FOR_ME";   //语音通话,上位机分配给我的ID

    /**
     * 视频下载状态值标识
     * statue; //状态值:成功或者失败   COMPLETED:成功, ERROR:失败,  START:开始,  DOWNING:下载中
     * 顺序如下:准备-->开始-->下载中-->成功|失败
     */
    public static final String STATUE_COMPLETED = "COMPLETED";                      //成功
    public static final String STATUE_ERROR = "ERROR";                              //失败
    public static final String STATUE_START = "START";                              //开始
    public static final String STATUE_READY = "READY";                              //准备
    public static final String STATUE_DOWNING = "DOWNING";                          //下载中
    public static final String STATUE_CANCELED = "CANCELED";                        //暂停
    public static final String TAG_DOWNING = "TAG_DOWNING";                         //线程当前下载队列是否下载中的标识,true表示正在下载,false 表示下载完毕,可以开启新的队列
    public static final String TAG_QUEUE_OVER = "TAG_QUEUE_OVER";                   //当前下载队列,true表示下载完毕,false 表示下载中


    public static final String STATUE_COMPLETED_DES = "已下载";                             //成功
    public static final String STATUE_ERROR_DES = "失败";                                   //失败
    public static final String STATUE_START_DES = "开始";                                   //开始
    public static final String STATUE_READY_DES = "准备中";                                 //准备
    public static final String STATUE_DOWNING_DES = "下载中";                               //下载中
    public static final String STATUE_CANCELED_DES = "暂停";                                //暂停


    public static final String STATUE_CANCELED_TAG = "STATUE_CANCELED_TAG";                 //当前暂停的tag 标题


    /**
     * 填写设备Dialog的设备类型标识以及配置信息默认值
     */
    public static final String Dialog_Type_Change = "修改类型";                               //修改类型
    public static final String Dialog_Type_Add = "添加类型";                                  //添加类型


    //妇科--治疗台
    public static final String Type_FuKeTable = "妇科治疗台";                                 //妇科治疗台
    public static final String Type_FuKeTable_Remark = "妇科治疗台的备注信息";                 //妇科治疗台备注信息
    public static final String Type_FuKeTable_ip = "192.168.1.200";                          //ip
    public static final String Type_FuKeTable_Account = "root";                              //账号
    public static final String Type_FuKeTable_Password = "root";                             //密码
    public static final String Type_FuKeTable_HttpPort = "7001";                             //HttpPort
    public static final String Type_FuKeTable_LivePort = "7788";                             //LivePort
    public static final String Type_FuKeTable_SocketPort = "7006";                           //SocketPort
    public static final String Type_FuKeTable_MicPort = "7789";                              //MicPort


    //一代一体机
    public static final String Type_V1_YiTiJi = "一代一体机";                                 //一代一体机
    public static final String Type_V1_YiTiJi_Remark = "一代一体机的备注信息";                 //一代一体机的备注信息
    public static final String Type_V1_YiTiJi_ip = "192.168.1.200";                          //ip
    public static final String Type_V1_YiTiJi_Account = "root";                              //账号
    public static final String Type_V1_YiTiJi_Password = "root";                             //密码
    public static final String Type_V1_YiTiJi_HttpPort = "7001";                             //HttpPort
    public static final String Type_V1_YiTiJi_LivePort = "7788";                             //LivePort
    public static final String Type_V1_YiTiJi_SocketPort = "7006";                           //SocketPort
    public static final String Type_V1_YiTiJi_MicPort = "7789";                              //MicPort


    //耳鼻--喉治疗台
    public static final String Type_EarNoseTable = "耳鼻喉治疗台";                             //耳鼻喉治疗台
    public static final String Type_EarNoseTable_Remark = "耳鼻喉治疗台的备注信息";             //耳鼻喉治疗台的备注信息
    public static final String Type_EarNoseTable_ip = "192.168.1.200";                        //ip
    public static final String Type_EarNoseTable_Account = "root";                            //账号
    public static final String Type_EarNoseTable_Password = "root";                           //密码
    public static final String Type_EarNoseTable_HttpPort = "7001";                           //HttpPort
    public static final String Type_EarNoseTable_LivePort = "7788";                           //LivePort
    public static final String Type_EarNoseTable_SocketPort = "7006";                         //SocketPort
    public static final String Type_EarNoseTable_MicPort = "7789";                            //MicPort

    //泌尿--喉治疗台
    public static final String Type_MiNiaoTable = "泌尿治疗台";                                 //泌尿治疗台
    public static final String Type_MiNiaoTable_Remark = "泌尿治疗台的备注信息";                 //泌尿治疗台的备注信息
    public static final String Type_MiNiaoTable_ip = "192.168.1.200";                          //ip
    public static final String Type_MiNiaoTable_Account = "root";                              //账号
    public static final String Type_MiNiaoTable_Password = "root";                             //密码
    public static final String Type_MiNiaoTable_HttpPort = "7001";                             //HttpPort
    public static final String Type_MiNiaoTable_LivePort = "7788";                             //LivePort
    public static final String Type_MiNiaoTable_SocketPort = "7006";                           //SocketPort
    public static final String Type_MiNiaoTable_MicPort = "7789";                              //MicPort


    /**
     * 协议里面设备类型,文档里面和传输都用16进制表示
     * int用十六进制表示
     * 统一用十六进制表示 协议返回的也是16进制
     * 备注:
     * 所有设备类型的数字用int表示
     * 所有设备类型的数字用int表示
     * 所有设备类型的数字用int表示
     */

    public static final int Type_00 = 0x00;                                                       //工作站
    public static final int Type_01 = 0x01;                                                       //HD3摄像机
    public static final int Type_02 = 0x02;                                                       //冷光源
    public static final int Type_03 = 0x03;                                                       //气腹机
    public static final int Type_04 = 0x04;                                                       //冲洗机
    public static final int Type_05 = 0x05;                                                       //4K摄像机
    public static final int Type_06 = 0x06;                                                       //耳鼻喉控制板
    public static final int Type_07 = 0x07;                                                       //一代一体机
    public static final int Type_08 = 0x08;                                                       //耳鼻喉治疗台
    public static final int Type_09 = 0x09;                                                       //妇科治疗台
    public static final int Type_0A = 0x0A;                                                       //泌尿治疗台
    public static final int Type_A0 = 0xA0;                                                       //iOS
    public static final int Type_A1 = 0xA1;                                                       //Android


    public static final String Type_00_DESC = "工作站";                                           //0x00
    public static final String Type_01_DESC = "HD3摄像机";                                        //0x01
    public static final String Type_02_DESC = "冷光源";                                           //0x02
    public static final String Type_03_DESC = "气腹机";                                           //0x03
    public static final String Type_04_DESC = "冲洗机";                                           //0x04
    public static final String Type_05_DESC = "4K摄像机";                                         //0x05
    public static final String Type_06_DESC = "耳鼻喉控制板";                                      //0x06
    public static final String Type_07_DESC = "一代一体机";                                        //0x07
    public static final String Type_08_DESC = "耳鼻喉治疗台";                                      //0x08
    public static final String Type_09_DESC = "妇科治疗台";                                        //0x09
    public static final String Type_0A_DESC = "泌尿治疗台";                                        //0x0A
    public static final String Type_A0_DESC = "iOS";                                              //0xA0
    public static final String Type_A1_DESC = "Android";                                          //0xA1

}
