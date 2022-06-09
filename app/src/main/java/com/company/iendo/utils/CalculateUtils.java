package com.company.iendo.utils;

import android.content.Context;

import com.company.iendo.other.Constants;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/7/16 8:31
 * desc：各种进制计算的工具类
 */
public class CalculateUtils {
    private static final String TAG = "计算工具类===";
    private static Gson mGson = new Gson();
    private static String sendCommandString;
/**
 * 获取发送方设备类型
 */
    /**
     * Send_Type(Received_Type)	发送(接收方)方设备类型
     * 设备类型：
     * 00-工作站， 01-HD3摄像机，02-冷光源，03-气腹机，04-冲洗机，
     * 05-4K摄像机，06-耳鼻喉控制板，07-一代一体机，8-耳鼻喉治疗台，
     * 9-妇科治疗台，10-泌尿治疗台，A0-iOS，A1-Android
     * FF-所有设备
     * 更多设备类型依次类推，平台最大可连接255种受控设备
     * 相对于pad来说的发送方也就是上位机的设备设备类型
     *
     *
     * 相对于pad来说的发送方也就是上位机的设备类型,比如一带一体
     */


    public static String getSendDeviceType(String string) {

        //字符串--48位--50位表示的是设备类型
        if (!("".equals(string)) && string.length() >= 50) {
            String str = string.substring(14, 16);
            int i = hex16To10(str);
            String result = null;
            if (Constants.Type_00 == i) {
                result = Constants.Type_00_DESC;
            } else if (Constants.Type_01 == i) {
                result = Constants.Type_01_DESC;
            } else if (Constants.Type_02 == i) {
                result = Constants.Type_02_DESC;
            } else if (Constants.Type_03 == i) {
                result = Constants.Type_03_DESC;
            } else if (Constants.Type_04 == i) {
                result = Constants.Type_04_DESC;
            } else if (Constants.Type_05 == i) {
                result =Constants.Type_05_DESC;
            } else if (Constants.Type_06 == i) {
                result = Constants.Type_06_DESC;
            } else if (Constants.Type_07 == i) {
                result =Constants.Type_07_DESC;
            } else if (Constants.Type_08 == i) {
                result = Constants.Type_08_DESC;
            } else if (Constants.Type_09 == i) {
                result = Constants.Type_09_DESC;
            } else if (Constants.Type_0A == i) {
                result = Constants.Type_0A_DESC;
            } else if (Constants.Type_A0 == i) {
                result = Constants.Type_A0_DESC;
            } else if (Constants.Type_A1 == i) {
                result = Constants.Type_A1_DESC;
            }
//            if (Constants.Type_00.equals(str)) {
//                result = "工作站";
//            } else if (Constants.Type_01.equals(str)) {
//                result = "HD3摄像机";
//            } else if (Constants.Type_02.equals(str)) {
//                result = "冷光源";
//            } else if (Constants.Type_03.equals(str)) {
//                result = "气腹机";
//            } else if (Constants.Type_04.equals(str)) {
//                result = "冲洗机";
//            } else if (Constants.Type_05.equals(str)) {
//                result = "4K摄像机";
//            } else if (Constants.Type_06.equals(str)) {
//                result = "耳鼻喉控制板";
//            } else if (Constants.Type_07.equals(str)) {
//                result = "一代一体机";
//            } else if (Constants.Type_08.equals(str)) {
//                result = "耳鼻喉治疗台";
//            } else if (Constants.Type_09.equals(str)) {
//                result = "妇科治疗台";
//            } else if (Constants.Type_0A.equals(str)) {
//                result = "泌尿治疗台";
//            } else if (Constants.Type_A0.equals(str)) {
//                result = "iOS";
//            } else if (Constants.Type_A1.equals(str)) {
//                result = "Android";
//            }
            return result;
        }
        return "传入的String有误";

    }

    /**
     * * Send_Type(Received_Type)	发送(接收方)方设备类型
     * * 设备类型：
     * * 00-工作站， 01-HD3摄像机，02-冷光源，03-气腹机，04-冲洗机，
     * * 05-4K摄像机，06-耳鼻喉控制板，07-一代一体机，8-耳鼻喉治疗台，
     * * 9-妇科治疗台，10-泌尿治疗台，A0-iOS，A1-Android
     * * FF-所有设备
     * * 更多设备类型依次类推，平台最大可连接255种受控设备
     * <p>
     * 我们手动吧 8  9  改成了 08 09 所以需要在这里替换下
     * <p>
     * string从随机之开始到校验和处结束的String
     *
     * @return
     */
    public static String getDeviceTypeFromRoom(String string) {
        //字符串--48位--50位表示的是设备类型
        if (!("".equals(string)) && string.length() >= 38) {
            String str = string.substring(2, 4);
            String result = null;
            int i = hex16To10(str);
            if (Constants.Type_00 == i) {
                result = "工作站";
            } else if (Constants.Type_01 == i) {
                result = "HD3摄像机";
            } else if (Constants.Type_02 == i) {
                result = "冷光源";
            } else if (Constants.Type_03 == i) {
                result = "气腹机";
            } else if (Constants.Type_04 == i) {
                result = "冲洗机";
            } else if (Constants.Type_05 == i) {
                result = "4K摄像机";
            } else if (Constants.Type_06 == i) {
                result = "耳鼻喉控制板";
            } else if (Constants.Type_07 == i) {
                result = "一代一体机";
            } else if (Constants.Type_08 == i) {
                result = "耳鼻喉治疗台";
            } else if (Constants.Type_09 == i) {
                result = "妇科治疗台";
            } else if (Constants.Type_0A == i) {
                result = "泌尿治疗台";
            } else if (Constants.Type_A0 == i) {
                result = "iOS";
            } else if (Constants.Type_A1 == i) {
                result = "Android";
            }
            return result;

        }
        return "null";

    }

    /**
     * 获取协议---命令cmd---然后处理不同socket回调转换数据bean
     */
    public static String getCMD(String string) {
        try {

//        AAC501006A22EE0700000000000000005618B1F96D92837Ca1f9432b11b93e8bb4ae34539b7472c20eFD7b227469746c65223a2241494f2d454e54222c2272656d61726b223a226f6e65686f6d65222c22656e646f74797065223a2233222c22616363657074223a2230227db4DD
            //字符串--50位--66位表示的是设备类型
            if (!("".equals(string)) && string.length() >= 83) {
                String str = string.substring(82, 84);
                return str;

            }
        } catch (Exception e) {
            LogUtils.e(TAG+"getCMD()-->Exception,str="+string);
        }
        return "";
    }

    /**
     * 获取发送方设备id ---唯一表示
     * 相对于pad来说的发送方也就是上位机的设备id
     */
    public static String getSendDeviceOnlyCode(String string) {

        try {

            //字符串--50位--66位表示的是设备类型
            if (!("".equals(string)) && string.length() >= 70) {
                String str = string.substring(16, 48);

                return str;

            }
        } catch (Exception e) {
            LogUtils.e(TAG+"getSendDeviceOnlyCode()-->Exception,str="+string);
        }

        return "";
    }

    /**
     * 获取发送方设备id ---唯一表示
     * 从随机之开始到Data结束的String
     */
    public static String getDeviceOnlyCodeFromRoom(String string) {
        //字符串--50位--66位表示的是设备类型
        if (!("".equals(string)) && string.length() >= 66) {
            String str = string.substring(38, 70);
            return str;

        }
        return "";
    }

    /**
     * 判断当前信息是否发给我的(FF-全部设备, A1-android)
     * 48, 50
     * 1,先判断出去data长度的字符串长度够不够179   (除去data的长度)
     * 2,在判断接收方是android 并且发送的data 的设备id必须和我本机android设备id相同
     * 3,再次检验验算发送过来string的检验值,正确才回调数据
     *
     *
     * @param string 全部hexstring 数据DetailFragment$22
     * @return true 是发给我的  false 不是发给我的
     */
    public static Boolean getDataIfForMe(String string, Context activity) {
//        String oldstring = "AAC501007027EE0700000000000000005618B1F96D92837CA1F9432B11B93E8BB4AE34539B7472C20EFD7B227469746C65223A2241494F2D454E54222C2272656D61726B223A2231E58FB7E58685E9959CE5AEA4222C22656E646F74797065223A2233222C22616363657074223A2231227D8CDD";
//
//        String datastring = "7B227469746C65223A2241494F2D454E54222C2272656D61726B223A2231E58FB7E58685E9959CE5AEA4222C22656E646F74797065223A2233222C22616363657074223A2231227D";

        //长度不够直接不接受
        if (!(("".equals(string)) && string.length() >= 179)) {
            String currentDevice = MD5ChangeUtil.Md5_32(DeviceIdUtil.getDeviceId(activity)); //记得大写
            String currentDeviceID = currentDevice.toUpperCase(); //记得大写
            if (string.length() < 82) {
                return false;
            }
            //获取发送给什么设备类型的
            String str = string.substring(48, 50);
            //获取发送给什么设备的id
            String substring = string.substring(50, 82);
            String sendType = string.substring(7, 9);


            LogUtils.e(TAG + "sendType=="+sendType);
            //android发送给android的 直接过滤
            if (("A1".equals(sendType))) {
                LogUtils.e(TAG + "发送类型=="+sendType);
                LogUtils.e(TAG + "直接过滤:返回false-->Android发送给Android的消息");

                return false;
            }
            //获取接收的设备id 必须本机的设备id相同,返回false

            if (!(currentDeviceID.equals(substring))) {
                LogUtils.e(TAG + "iPad_ID_本机=="+currentDeviceID);
                LogUtils.e(TAG + "iPad_ID_协议=="+substring);
                LogUtils.e(TAG + "直接过滤:返回false-->接收的设备id 必须本机的设备id相同");
                return false;
            }
            //接收方不是android-A1 返回false
            if (!("A1".equals(str))) {
                LogUtils.e(TAG + "iPad_type=="+str);
                LogUtils.e(TAG + "直接过滤:返回false-->接收方不是android");
                return false;

            }
            //再次校验下校验和
            String substring1 = string.substring(6, string.length() - 4);
            String oldCSData = string.substring(string.length() - 4, string.length() - 2);
            String hexXORData = get16HexXORData("AA" + substring1);
            //获取接收的设备id 必须和本机的设备id相同
            if (oldCSData.equals(hexXORData.toUpperCase())) {
                LogUtils.e(TAG + "数据校验OK,返回ture,可以解析,监听到的16进制String数据");
                return true;
            }
        } else {
            return false;
        }
        return false;
    }


/*************************************************************计算协议数据的个方法***********************************************************************/
    /**
     * 获取协议Length   的长度
     *
     * @return
     */
    public static String getLength(String data) {
//        40(固定长度)加变化data的长度然后-->字符串的长度转成hex进制
        byte[] bytes = hexString2Bytes(data);
        int i = bytes.length + 40;
        //10进制转换成16进制  --保留 4位，不足补0
        String s = hex10To16Result4(i);
        return s.toUpperCase();
    }


    /**
     * 获取十六进制的随机数
     */

    /**
     * 获取16进制随机数
     *
     * @param len len是指你要生成几位，
     * @return
     * @throws
     */
    public static String getRandomHexString(int len) {
        try {
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < len; i++) {
                result.append(Integer.toHexString(new Random().nextInt(16)));
            }
            return result.toString().toUpperCase();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        }
        return null;

    }

    /**
     * 获取当前时间字符串
     */
    public static String getCurrentTimeString() {
        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String format = dateFormat.format(date);
//        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyyMdHms");
//        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//        String format1 = LocalDateTime.parse(format, inputFormat).format(outputFormat);
        return format;
    }

    /**
     * 字符串转换成为16进制(无需Unicode编码)
     *
     * @param str
     * @return
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }


    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     * 十六进制转字符串
     *
     * @param hexStr 字母必须为大写
     * @return
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();

        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * 获取发送数据的16进制字符串--转--字节数组的byte
     *
     * @param mContext      :上下文
     * @param bean          :(Bean数据Json字符串)mData的bean对象,N字节，
     * @param Received_Type :接收方设备类型(数值参数,1字节):00-工作站， 01-HD3摄像机，02-冷光源，03-气腹机，04-冲洗机，05-4K摄像机，06-耳鼻喉控制板，07-一代一体机，8-耳鼻喉治疗台，9-妇科治疗台，10-泌尿治疗台，A0-iOS，A1-Android，FF-所有设备
     * @param Received_ID   :接收方设备唯一标识,16字节，32位,不够补0
     * @param CMD           :控制命令，1字节0xFF（即0-255）不同设备控制命令可雷同
     * @return
     */

    public static byte[] getSendByteData(Context mContext, String bean, String Received_Type,
                                         String Received_ID, String CMD) {
//                0000000000000000     广播的
//                30303030303030303030303030303030
//                937a5f204dc43a14
//                39333761356632303464633433613134
//      BroadCastDataBean bean = new BroadCastDataBean();
//      bean.setBroadcaster("szcme");                              //设备名字
//      bean.setRamdom(CalculateUtils.getCurrentTimeString());
//      广播发起随机时间戳:20220127104645
        //不转换的时候最后在CalculateUtils.hexString2Bytes(sendCommandString);回返回null,因为我那边模了不是偶数就会返回null

        if ("9".equals(Received_Type)) {
            Received_Type = "09";
        }
        if ("8".equals(Received_Type)) {
            Received_Type = "08";
        }
        if ("7".equals(Received_Type)) {
            Received_Type = "07";
        }
        String mSend_IDBy32 = MD5ChangeUtil.Md5_32(DeviceIdUtil.getDeviceId(mContext));
        String mData = CalculateUtils.str2HexStr(bean);                   //data  json字符串转16进制
        String mHead = "AAC5";                                             //帧头    ---2字节
        String mVer = "01";                                                //版本号  ---1字节
        String mLength = CalculateUtils.getLength(mData);                  //长度   ---2字节   40加data的长度 字符串的长度转成hex进制
        String mRandom = CalculateUtils.getRandomHexString(2);         // //随机数  ---1字节
        String mCMD_ID = "FF";                                              //命令ID   ---2字节-暂时规定,主动发起方为FF 接收方为随机值,PS--移动端目前交互写死值=FF
        String mSend_Type = "A1";                                           //发送方设备类型。--1字节-Android=A1  FF为所有设备
        String mSend_ID = mSend_IDBy32.toUpperCase();                       //发送方设备唯一标识。   --16字节
        String mReceived_Type = Received_Type;                              //接收方设备类型。   --FF是是所有设备
        String Received_ID32 = CalculateUtils.str2HexStr(Received_ID);
        String mReceived_ID = Received_ID32;                                  //接收方设备唯一标识。   --16字节--目前暂时给32个0,模拟后台给的数据
        String mCMD = CMD;                                                  //UDP广播   --一个字节
        // 校验和，0xAA 依次与“Length、Random、CMD_ID、Send_Type、Send_ID、Received_Type、Received_ID、CMD、Data” 异或运算后的结果
        String CSString = "AA" + mLength + mRandom + mCMD_ID + mSend_Type + mSend_ID + mReceived_Type + mReceived_ID + mCMD + mData;
        String mCheck_Sum = CalculateUtils.get16HexXORData(CSString).toUpperCase();

        sendCommandString = mHead + mVer + mLength + mRandom + mCMD_ID + mSend_Type + mSend_ID + mReceived_Type +
                mReceived_ID + mCMD + mData + mCheck_Sum + "DD";

        LogUtils.e(TAG + "随机数========" + mRandom);
        LogUtils.e(TAG + "iPadID(32)===" + mSend_IDBy32);
        LogUtils.e(TAG + "上位机类型====" + mReceived_Type);
        LogUtils.e(TAG + "上位机ID(16)==" + Received_ID);
        LogUtils.e(TAG + "上位机ID(32)==" + Received_ID32);
        LogUtils.e(TAG + "命令CMD=======" + Received_ID32);
        LogUtils.e(TAG + "data的16进制==" + mData);
        LogUtils.e(TAG + "异或的结果=====" + mCheck_Sum);
        LogUtils.e(TAG + "最后发送数据长度:" + sendCommandString.length());
        LogUtils.e(TAG + "最后发送数据==" + sendCommandString);

        byte[] bytes = CalculateUtils.hexString2Bytes(sendCommandString);
        //AAC5 01 0059 EE22 FF A1 f9432b11b93e8bb4ae34539b7472c20e FF 00000000000000000000000000000000
        //FD 7B2262726F6164636173746572223A22737A636D65222C2272616D646F6D223A223230323230313237313132373535227D
        // F8 DD
        return bytes;
    }




    /**
     * 获取接收data的数据--data
     *
     * @param str 传入接收指令所有长度的,16进制的string
     * @return
     */
    public static String getReceiveDataString(String str) {
//        String str = "AAC5 01 0059 D8 FF A1f9432b11b93e8bb4ae34539b7472c20eFF00000000000000000000000000000000FD7B2262726F6164636173746572223A22737A636D65222C2272616D646F6D223A223230323230313237313133353130227DEEDD";
        if (str.length() < 82) {
            return "";
        }
        String substring = str.substring(82 + 2, str.length() - 4);

        return substring;

    }

    /**
     * 获取接收socket的数据--data
     *
     * @param str 传入接收指令所有长度为(命令id主从机---命令id主从机)包含这两者,16进制的string
     * @return
     */
    public static String getReceiveDataStringFromRoomForBroadCast(String str) {
//        String str = "EE0700000000000000005618B1F96D92837Ca1f9432b11b93e8bb4ae34539b7472c20eFD7b227469746c65223a2241494f2d454e
//        54222c2272656d61726b223a226f6e65686f6d65222c22656e646f74797065223a2233222c22616363657074223a2230227d==192.168.132.102";
        int i = str.indexOf("==");
        String substring = str.substring(72, i);
        return substring;

    }

    /**
     * 获取接收socket的数据--data
     *
     * @param str 传入接收指令所有长度为(命令id主从机---命令id主从机)包含这两者,16进制的string
     * @return
     */
    public static String getReceiveDataStringFromRoomForPoint(String str) {
//        String str = "EE0700000000000000005618B1F96D92837Ca03399cbe9a32d4786abf24e39d3cad576FC7b226970223a223139322e3136382e36342e3133
//        222c227a7074223a2237373838222c226964223a22726f6f74222c227077223a22726f6f74222c2266726f6d223a2241494f2d454e54222c22737470223a22
//        38303035222c22687074223a2237303031222c2272656d61726b223a2231E58FB7E58685E9959CE5AEA4222c2274797065223a223037222c226574223a2233
//        222c22726574636f6465223a2230227dd5DD==192.168.132";
        int i = str.indexOf("==");
        String substring = str.substring(72, i);
        return substring;

    }

    /**
     * 获取接收socket的数据--ReceiveType
     *
     * @param str 传入接收指令所有长度为(命令id主从机---命令id主从机)包含这两者,16进制的string
     * @return
     */
    public static String getReceiveType(String str) {
//     String str = "EE0700000000000000005618B1F96D92837CA1F9432B11B93E8BB4AE34539B7472C20EFD7B227469746C65223A2241494F2D454E54222C2272656D61726B223A2231E58FB7E58685E9959CE5AEA4222C22656E646F74797065223A2233222C22616363657074223A2231227D";

        String substring = str.substring(2, 4);
        return substring;

    }

    /**
     * 获取接收socket的数据--SendID
     *
     * @param str 传入接收指令所有长度为(命令id主从机---命令id主从机)包含这两者,16进制的string
     * @return
     */
    public static String getSendID(String str) {
//      String str = "EE0700000000000000005618B1F96D92837CA1F9432B11B93E8BB4AE34539B7472C20EFD7B227469746C65223A2241494F2D454E54222C2272656D61726B223A2231E58FB7E58685E9959CE5AEA4222C22656E646F74797065223A2233222C22616363657074223A2231227D";
        String substring = str.substring(4, 36);
        return substring;

    }

    /**
     * 获取接收socket的数据--ReceiveID
     *
     * @param str 传入接收指令所有长度为(命令id主从机---命令id主从机)包含这两者,16进制的string
     * @return
     */
    public static String getReceiveID(String str) {
//      String str = "EE0700000000000000005618B1F96D92837CA1F9432B11B93E8BB4AE34539B7472C20EFD7B227469746C65223A2241494F2D454E54222C2272656D61726B223A2231E58FB7E58685E9959CE5AEA4222C22656E646F74797065223A2233222C22616363657074223A2231227D";
        String substring = str.substring(4, 36);
        LogUtils.e(TAG+"UDP==命令===getReceiveID===" + substring);

        return substring;

    }

    /**
     * 获取接收socket的数据--随机数之后到data结尾的String
     *
     * @param str 传入接收指令所有长度的,16进制的string
     * @return  返回-->随机数结尾,CMD_ID开始+发送设备+......+Data结尾,整个十六进制字符串(不包含Check_Sum,和DD)
     * random
     * getRandomEndData2DataEndResultHexString
     */
    public static String getRandomEndData2DataEndResultHexString(String str) {
        try {
            LogUtils.e(TAG+"getRandomEndData2DataEndResultHexString()-->str=="+str);

//String str = "AAC501006A22 EE0700000000000000005618B1F96D92837Ca1f9432b11b93e8bb4ae34539b7472c20eFD7b227469746c65223a2241494f2d454e54222c2272656d61726b223a226f6e65686f6d65222c22656e646f74797065223a2233222c22616363657074223a2230227d b4DD";
            String substring = str.substring(12, str.length() - 4);

            return substring;
        } catch (Exception e) {
            LogUtils.e(TAG+"getRandomEndData2DataEndResultHexString()-->Exception,str=="+str);
        }

        return "";

    }
    /*************************************************************计算协议数据的个方法***********************************************************************/


    /**
     * 十六进制异或运算
     * //16 to 10
     * String string = Integer.toHexString(10);
     * //10  to 16
     * int a = Integer.parseInt("A", 16);
     *
     * @param para 修改了校验和异或结果有些时候是一位数表示十六进制的错误 get16HexXORData()
     * @return 获取异或值  校验结果
     */
//    校验和，0xAA 依次与“Length、Random、CMD_ID、Send_Type、Send_ID、Received_Type、Received_ID、CMD、Data” 异或运算后的结果
    public static String get16HexXORData(String para) {
        int length = para.length() / 2;
        String[] dateArr = new String[length];

        for (int i = 0; i < length; i++) {
            dateArr[i] = para.substring(i * 2, i * 2 + 2);
        }
        String code = "00";
        for (int i = 0; i < dateArr.length; i++) {
            code = xor(code, dateArr[i]);
        }
        /**
         * 此处,出现了是以为十六进制数字的时候,后续获取byte数组错误问题,
         * 在这里,个位显示的十六进制转换成两位的即可解决此bug
         */
        String result = code;
        if (result.length() == 1) {
//            LogUtils.e("UDP==命令===最后发送的String===异或的CSString==长度=1==错误数据" + code);
            result = "0" + code;
//            LogUtils.e("UDP==命令===最后发送的String===异或的CSString==长度=1==修正数据" + result);
        } else {
            result = code;
//            LogUtils.e("UDP==命令===最后发送的String===异或的CSString==长度=2==计算数据" + code);
        }
        return result;
//        return code;
    }

    private static String xor(String strHex_X, String strHex_Y) {
        //将x、y转成二进制形式
        String anotherBinary = Integer.toBinaryString(Integer.valueOf(strHex_X, 16));
        String thisBinary = Integer.toBinaryString(Integer.valueOf(strHex_Y, 16));
        String result = "";
        //判断是否为8位二进制，否则左补零
        if (anotherBinary.length() != 8) {
            for (int i = anotherBinary.length(); i < 8; i++) {
                anotherBinary = "0" + anotherBinary;
            }
        }
        if (thisBinary.length() != 8) {
            for (int i = thisBinary.length(); i < 8; i++) {
                thisBinary = "0" + thisBinary;
            }
        }
        //异或运算
        for (int i = 0; i < anotherBinary.length(); i++) {
            //如果相同位置数相同，则补0，否则补1
            if (thisBinary.charAt(i) == anotherBinary.charAt(i))
                result += "0";
            else {
                result += "1";
            }
        }
//        Log.e("code", result);
        return Integer.toHexString(Integer.parseInt(result, 2));
    }


    /**
     * 如何把udp返回的无符号byte数组,转换成string 类型?
     * 1:用byte数组接收，因为unsigned char范围是0-255，byte是-128-127，所有用byte类型数组接收后将byte转为int类型接收
     * 2:再将int类型转化为十六进制。
     */

    public static String getByteData2StringHexDate(byte[] data) {
        String str = "";
        if (null != data) {
            //创建等长度的int bate
            int[] intData = new int[data.length];
            for (int i = 0; i < data.length; i++) {
                //按位与，将signed类型转化为int的数字。因为Java中没有
                intData[i] = data[i] & 0xff;
                str = str + numToHex8(data[i]);
                if ("dd".equals(numToHex8(data[i]))) {
                    break;
                }
            }
            return str;
        } else {
            return "getStringHexDate  byte[] data is null";

        }

    }


    /**
     * 已下是把int类型转换成16进制
     *
     * @param b
     * @return 返回 使用1字节就可以表示b
     */
    //使用1字节就可以表示b  //15
    public static String numToHex8(int b) {
        return String.format("%02x", b);//2表示需要两个16进制数
    }

    //需要使用2字节表示b   //00 15
    public static String numToHex16(int b) {
        return String.format("%04x", b);
    }

    //需要使用4字节表示b   //00 00 00 15
    public static String numToHex32(int b) {
        return String.format("%08x", b);
    }


    /**
     * 16进制字符串--转--字节数组-----UDP---发包
     */
    public static byte[] hexString2Bytes(String hex) {
        if ((hex == null) || (hex.equals(""))) {
            return null;
        } else if (hex.length() % 2 != 0) {
            return null;
        } else {
            hex = hex.toUpperCase();
            int len = hex.length() / 2;
            byte[] b = new byte[len];
            char[] charDate = hex.toCharArray();
            for (int i = 0; i < len; i++) {
                int p = 2 * i;
                b[i] = (byte) (charToByte(charDate[p]) << 4 | charToByte(charDate[p + 1]));

            }

            return b;
        }

    }

    /**
     * 字符转换为字节
     */
    private static byte charToByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
//        aac5000008000001211597dd
//        AAC5000008000001211597DD
        return b;
    }


    /**
     * 16进制转换成10进制
     */

    public static int hex16To10(String strHex) {
        BigInteger bigInteger = new BigInteger(strHex, 16);

        return bigInteger.intValue();
    }

    /**
     * 10进制转换成16进制  --保留 2位，不足补0
     */
    public static String hex10To16Result2(int intValue) {

        return String.format("%02x", intValue);// 2表示需要两个16进制数
    }

    /**
     * 10进制转换成16进制  --保留 4位，不足补0
     */
    public static String hex10To16Result4(int intValue) {

        return String.format("%04x", intValue);
    }


    /**********************************转换字节数组为16进制字串****************第一种方式*******************************/
    /**
     * 字节数组转16进制字符串-----UDP---接受包数据
     */


//没有分隔符
    public static String getHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder("");

        if (bytes != null && bytes.length > 0) {
            for (int i = 0; i < bytes.length; ++i) {
                int v = bytes[i] & 255;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }

                stringBuilder.append(hv);
            }

            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    public static String bytes2HexString(byte[] b) {
        String r = "";
        int count = 0;


        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            //如果自定义接收的byte字节是1024 就需要手动判断,提高性能,如果一开始就约定好协议是12个字节,就不需要判断
//            if ("dd".equals(hex) || "DD".equals(hex)) {
//                count++;
//            }
            r = r + hex.toUpperCase();
//如果自定义接收的byte字节是1024 就需要手动判断,提高性能,如果一开始就约定好协议是12个字节,就不需要判断
//            if (count == 1) {
//                return r;
//            }
//            r += hex.toUpperCase();
        }
        int dd = r.indexOf("DD");
        String recIp = r.substring(0, dd + 2);

        return r;
    }
    /**********************************转换字节数组为16进制字串****************第二种方式*******************************/

    /**
     *     * 将byte转为16进制
     *     * @param bytes
     *     * @return
     *
     */
//    public static String byteArrayToHexString(byte[] bytes) {
//        StringBuffer stringBuffer = new StringBuffer();
//        String temp = null;
//        for (int i = 0; i < bytes.length; i++) {
//            temp = Integer.toHexString(bytes[i] & 0xFF);
//            if (temp.length() == 1) {
//                //1得到一位的进行补0操作
//                stringBuffer.append("0");
//            }
//            stringBuffer.append(temp);
//        }
//        return stringBuffer.toString();
//    }

    /**
     * 转换字节数组为16进制字串
     *
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {

        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }

        return resultSb.toString();
    }

    public static String MD5Encode(String origin) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes()));
        } catch (Exception ex) {
        }
        return resultString;
    }

    public static final String EMPTY_STRING = "";
    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    /**
     * 转换字节数组为16进制字串
     *
     * @param b 字节数组
     * @return 16进制字串
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }


}
