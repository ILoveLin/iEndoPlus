package com.company.iendo.utils;

import com.company.iendo.mineui.socket.ThreadManager;
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
 * 使用端口复用,解决发送数据的时候只能使用随机端口的问题
 *
 *      DatagramSocket mSendSocket = new DatagramSocket(null);
 *               mSendSocket.setReuseAddress(true);
 *              mSendSocket.bind(new InetSocketAddress(Constants.SEND_PORT));
 */
public class SocketUtils {


    /**
     * 发送握手消息
     *
     * @param data        协议完整的hexString数据
     * @param ip          目标地址
     * @param receivePort 目标端口
     */
    public static void startSendHandMessage(byte[] data, String ip, int receivePort) {
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
                    LogUtils.e("SocketUtils===发送消息==点对点==hand==ip==" + ip);

//                    byte[] sendData = data.getBytes();
                    DatagramPacket mSendPacket = new DatagramPacket(data, data.length, finalMAddress, receivePort);
//                    for (int i = 0; i < 5; i++) {
                    //随机端口
//                    DatagramSocket mSendSocket = new DatagramSocket();
//                    mSendSocket.send(mSendPacket);
//                    mSendSocket.close();
                    //固定端口
                    DatagramSocket mSendSocket = new DatagramSocket(null);
                    mSendSocket.setReuseAddress(true);
                    mSendSocket.bind(new InetSocketAddress(Constants.SEND_PORT));
                    mSendSocket.send(mSendPacket);
                    mSendSocket.close();
                    LogUtils.e("SocketUtils===发送消息==点对点==hand==" + receivePort);
                    LogUtils.e("SocketUtils===发送消息==点对点==hand==ip==" + ip);

//                    }
                } catch (Exception e) {

                }
            }
        }.start();
    }


    /**
     * @param data        字节数组    广播 授权,使用的是设置的端口,其他的点对点消息,按照协议data的port的走
     */
    public static void startSendBroadcastMessage(byte[] data) {
        InetAddress mAddress = null;
        //点对点消息,握手
        try {
            mAddress = InetAddress.getByName(Constants.BROADCAST_IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        InetAddress finalMAddress = mAddress;


        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
//                    byte[] sendData = data.getBytes();
                    MMKV kv = MMKV.defaultMMKV();
                    int mCastSendPort = kv.decodeInt(Constants.KEY_BROADCAST_PORT);
                    DatagramPacket mSendPacket = new DatagramPacket(data, data.length, finalMAddress, mCastSendPort);
//                    for (int i = 0; i < 5; i++) {
//                        LogUtils.e("SocketUtils=====发送第=====" + i + "====次广播==mCastSendPort==" + mCastSendPort);
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
//                    }

                } catch (Exception e) {

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
    public static void startSendPointMessage(byte[] data, String ip, int receivePort) {

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
//                    MMKV kv = MMKV.defaultMMKV();
//                    int mSendPort = kv.decodeInt(Constants.KEY_BROADCAST_PORT);
                    DatagramPacket mSendPacket = new DatagramPacket(data, data.length, finalMAddress, receivePort);
//                    for (int i = 0; i < 5; i++) {
                    //随机端口

//                    DatagramSocket mSendSocket = new DatagramSocket();
//                    mSendSocket.send(mSendPacket);
//                    mSendSocket.close();
                    //固定端口
                    DatagramSocket mSendSocket = new DatagramSocket(null);
                    mSendSocket.setReuseAddress(true);
                    mSendSocket.bind(new InetSocketAddress(Constants.SEND_PORT));
                    mSendSocket.send(mSendPacket);
                    mSendSocket.close();
                    LogUtils.e("SocketUtils===发送消息==点对点==Point==" + receivePort);

//                    }
                } catch (Exception e) {

                }
            }
        }.start();


    }
}
