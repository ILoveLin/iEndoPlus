package com.company.iendo.utils;

import com.company.iendo.mineui.socket.ThreadManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/3 15:40
 * desc：
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
//                    byte[] sendData = data.getBytes();
                    DatagramPacket mSendPacket = new DatagramPacket(data, data.length, finalMAddress, receivePort);
//                    for (int i = 0; i < 5; i++) {
                    DatagramSocket mSendSocket = new DatagramSocket();
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
     * @param data        字节数组
     * @param ip          ip
     * @param receivePort 接收端的port
     */
    public static void startSendBroadcastMessage(byte[] data, String ip, int receivePort) {
        InetAddress mAddress = null;
        //点对点消息,握手
        try {
            mAddress = InetAddress.getByName(ip);
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
                    DatagramPacket mSendPacket = new DatagramPacket(data, data.length, finalMAddress, receivePort);
                    for (int i = 0; i < 5; i++) {
                        LogUtils.e("SocketUtils=====发送第=====" + i + "====次广播===" + receivePort);
                        Thread.sleep(500);
                        //固定端口
//                      mSendBroadcastSocket = new DatagramSocket(null);
//                      mSendBroadcastSocket.bind(new InetSocketAddress(8005));
                        //随机端口
                        DatagramSocket mSendBroadcastSocket = new DatagramSocket();
                        mSendBroadcastSocket.send(mSendPacket);
                        mSendBroadcastSocket.setBroadcast(true);
                        mSendBroadcastSocket.close();
                    }

                } catch (Exception e) {

                }
            }
        }.start();

    }


    /**
     * @param data        字节数组
     * @param ip          ip
     * @param receivePort 接收端的port
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
                    DatagramPacket mSendPacket = new DatagramPacket(data, data.length, finalMAddress, receivePort);
//                    for (int i = 0; i < 5; i++) {
                    DatagramSocket mSendSocket = new DatagramSocket();
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
