package com.company.iendo.utils;

import android.content.Context;

import com.company.iendo.other.Constants;
import com.tencent.mmkv.MMKV;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/3 15:40
 * desc：
 * 使用端口复用,
 * 1,解决发送数据的时候只能使用随机端口的问题
 * 2,解决发送数据的时候和接受数据不能公用一个端口问题
 * <p>
 * DatagramSocket mSendSocket = new DatagramSocket(null);
 * mSendSocket.setReuseAddress(true);
 * mSendSocket.bind(new InetSocketAddress(Constants.SEND_PORT));
 */
public class SocketUtils {

    private static final String TAG = "Socket发送消息===";

    /**
     * @param data 字节数组    广播 授权,使用的是设置的端口,其他的点对点消息,按照协议data的port的走
     *             发送数据的data中的  接收id 依照已下规定
     *             * 存入数据库的是:16字节16位的字符串(比如:937a5f204dc43a14)
     *             * socket通讯的是:接收和获取到的是-->16进制的32位字符串设备码(比如:39333761356632303464633433613134)
     *             * str2HexStr()----->16位转32位
     *             * hexStr2Str()----->32位转16位
     */
    public static void startSendBroadcastMessage(byte[] data, Context mContext) {
//        WifiManager manager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiManager.MulticastLock lock = manager.createMulticastLock("test wifi");


        //申请广播开启
//        lock.acquire();
        InetAddress mAddress = null;
        //点对点消息,握手
        try {
            mAddress = InetAddress.getByName(Constants.BROADCAST_IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        InetAddress finalMAddress = mAddress;

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
//                    byte[] sendData = data.getBytes();
                    MMKV kv = MMKV.defaultMMKV();
                    int mCastSendPort = kv.decodeInt(Constants.KEY_BROADCAST_PORT);

                    DatagramPacket mSendPacket = new DatagramPacket(data, data.length, finalMAddress, mCastSendPort);
                    for (int i = 0; i < 5; i++) {
                        LogUtils.e(TAG + "广播消息--->发送第:"+ i + "次广播,端口:" + mCastSendPort);
                        Thread.sleep(500);
                        //固定端口
                        DatagramSocket mSendBroadcastSocket = new DatagramSocket(null);
                        mSendBroadcastSocket.setReuseAddress(true);
                        mSendBroadcastSocket.bind(new InetSocketAddress(mCastSendPort));
                        mSendBroadcastSocket.send(mSendPacket);
                        mSendBroadcastSocket.setBroadcast(true);
                        mSendBroadcastSocket.close();

                        //随机端口
//                        DatagramSocket mSendBroadcastSocket = new DatagramSocket();
//                        mSendBroadcastSocket.send(mSendPacket);
//                        mSendBroadcastSocket.setBroadcast(true);
//                        mSendBroadcastSocket.close();
                    }
                    //释放资源
//                    lock.release();

                } catch (Exception e) {

                }
            }
        }.start();

    }


    /**
     * 发送握手消息
     *
     * @param data        协议完整的hexString数据
     * @param ip          目标地址
     * @param receivePort 目标端口
     */
    public static void startSendHandMessage(byte[] data, String ip, int receivePort, Context mContext) {
//        WifiManager manager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiManager.MulticastLock lock = manager.createMulticastLock("test wifi");
//        lock.acquire();    //申请开启
        InetAddress mAddress = null;
        //点对点消息,握手
        try {
            mAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        InetAddress finalMAddress = mAddress;
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {

                    LogUtils.e(TAG + "hand消息--->"+"发送,ip:" + finalMAddress+",port:" + receivePort+",data:" + data);
                    DatagramPacket mSendPacket = new DatagramPacket(data, data.length, finalMAddress, receivePort);
//                    for (int i = 0; i < 5; i++) {
                    //随机端口
//                    DatagramSocket mSendSocket = new DatagramSocket();
//                    mSendSocket.send(mSendPacket);
//                    mSendSocket.close();
                    //固定端口
                    DatagramSocket mSendSocket = new DatagramSocket(null);
                    mSendSocket.setReuseAddress(true);
                    mSendSocket.bind(new InetSocketAddress(receivePort));
                    mSendSocket.send(mSendPacket);
                    mSendSocket.close();

                    //释放资源
//                    lock.release();
//                    }
                } catch (Exception e) {
                    LogUtils.e(TAG + "hand消息--->"+"发生Exception:"+e);
                }
            }
        }.start();
    }

    /**
     * @param data        字节数组
     * @param ip          ip
     * @param receivePort 接收端的port
     *                    广播 授权,使用的是设置的端口,其他的点对点消息,按照协议data的port的走
     */
    public static void startSendPointMessage(byte[] data, String ip, int receivePort, Context mContext) {
//        WifiManager manager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiManager.MulticastLock lock = manager.createMulticastLock("test wifi");
//        申请开启
//        lock.acquire();

        InetAddress mAddress = null;
        //点对点消息,握手
        try {
            mAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        InetAddress finalMAddress = mAddress;
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
//                    byte[] sendData = data.getBytes();
                    LogUtils.e(TAG + "point消息--->"+"发送,ip:" + finalMAddress+",port:" + receivePort+",data:" + data);
                    DatagramPacket mSendPacket = new DatagramPacket(data, data.length, finalMAddress, receivePort);
//                    for (int i = 0; i < 5; i++) {
                    //随机端口
//                    DatagramSocket mSendSocket = new DatagramSocket();
//                    mSendSocket.send(mSendPacket);
//                    mSendSocket.close();
                    //固定端口
                    DatagramSocket mSendSocket = new DatagramSocket(null);
                    mSendSocket.setReuseAddress(true);
                    mSendSocket.bind(new InetSocketAddress(receivePort));
                    mSendSocket.send(mSendPacket);
                    mSendSocket.close();
                    //释放资源
//                    lock.release();
//                    }
                } catch (Exception e) {
                    LogUtils.e(TAG + "point消息--->"+"发生Exception:"+e);


                }
            }
        }.start();


    }
}
