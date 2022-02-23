package com.company.iendo.mineui.socket;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.company.iendo.utils.CalculateUtils;
import com.company.iendo.utils.LogUtils;
import com.lzh.easythread.EasyThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/1/27 8:50
 * desc：Socket的工具类
 * 使用手册:
 * 1,设置监听setOnSocketListener
 * 2,先开启接收线程,在开启发送消息线程
 * <p>
 * 注意!!!!!:
 * 退出界面记得调用closeReceiveSocket()端口接收链接
 */
public class SocketManage {
    private static DatagramSocket mSendSocket = null;
    private static DatagramSocket mSendBroadcastSocket = null;
    private static DatagramSocket mReceiveSocket = null;
    private static Runnable mSendRunnable;
    private static Runnable mSendBroadcastRunnable;
    private static Runnable mReceiveRunnable;
    private static EasyThread easyCacheThread;
    private static EasyThread easyFixed2Thread;
    private static final String TAG = "SocketManage";
    private volatile static boolean isRuning = true;

    static {
        LogUtils.e("======SocketManage=====static==");
        isRuning = true;

    }

    private static SocketManage mSocketManage;
    private static String currentIP;

    public static SocketManage getSocketManageInstance() {
        if (null == mSocketManage) {
            synchronized (SocketManage.class) {
                if (null == mSocketManage) {
                    mSocketManage = new SocketManage();
                }
            }
        }

        return mSocketManage;
    }

    private SocketManage() {
        LogUtils.e("正在执行Runnable任务：%s====SocketManage====创建了");
        isRuning = true;
        easyCacheThread = ThreadManager.getCache();
        easyFixed2Thread = ThreadManager.getIO();
    }

    /**
     * 开启一个异步回调监听
     */
//    public static void startAsyncReceive(int port) {
//        easyFixed2Thread = ThreadManager.getIO();
//
//        // 异步执行任务
//        Callable<String> callable = new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                LogUtils.e("正在执行Runnable任务：%s" + Thread.currentThread().getName());
//                byte[] receiveData = new byte[1024];
//                DatagramPacket mReceivePacket = new DatagramPacket(receiveData, receiveData.length);
//                try {
//                    mReceiveSocket = new DatagramSocket(port);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                while (true) {
//                    if (isRuning) {
//                        try {
//                            LogUtils.e("======SocketManage=====000==");
//                            mReceiveSocket.receive(mReceivePacket);
//                            LogUtils.e("======SocketManage=====111==");
//                            if (mReceivePacket != null) {
////                                SocketDataBean socketDataBean = new SocketDataBean();
////                                socketDataBean.setData("" + mReceivePacket.getData());
//                                LogUtils.e("startAsyncReceive==receivedata===" + mReceivePacket.getData());
//                                return mReceivePacket.getData() + "";
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//
//            }
//        };
//
//        // 异步回调
//        AsyncCallback<String> async = new AsyncCallback<String>() {
//            @Override
//            public void onSuccess(String str) {
//                // notify success;
//                LogUtils.e("======SocketManage=====onSuccess==" + str);
//                if (null != mListener) {
//                    mListener.onSuccess(str);
//                }
//
//            }
//
//            @Override
//            public void onFailed(Throwable t) {
//                // notify failed.
//                LogUtils.e("======SocketManage=====onFailed==");
//                if (null != mListener) {
//                    mListener.onFailed(t);
//                }
//
//            }
//        };
//
//        // 启动异步任务
//        easyFixed2Thread.async(callable, async);
//
//    }

    /**
     * 开启一个普通回调监听
     * <p>
     * 具体是那个socket的回调通过
     * 协议:命令cmd,来区分
     */
    public static void startNorReceive(int port, Activity activity) {
        //Wifi状态判断
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            currentIP = getIpString(wifiInfo.getIpAddress());
        }
//        easyFixed2Thread = ThreadManager.getIO();
        mReceiveRunnable = new Runnable() {
            @Override
            public void run() {
                LogUtils.e("正在执行Runnable任务：%s" + Thread.currentThread().getName());
                byte[] receiveData = new byte[1024];
                DatagramPacket mReceivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    mReceiveSocket = new DatagramSocket(port);  //本地监听的端口
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (true) {
                    if (isRuning) {
                        try {
                            LogUtils.e("======ReceiveThread=====000==");
                            LogUtils.e("======ReceiveThread=====mReceivePacket.getAddress()==" + mReceivePacket.getAddress());
                            LogUtils.e("======ReceiveThread=====currentIP==" + currentIP);
                            if (!currentIP.equals(mReceivePacket.getAddress())) {   //不是自己的IP不接受
                                mReceiveSocket.receive(mReceivePacket);
                                String rec = CalculateUtils.byteArrayToHexString(mReceivePacket.getData()).trim();
                                //过滤不是发送给我的消息全部不接受
                                int dd = rec.indexOf("DD");
                                String strdata = rec.substring(0, dd + 2);
                                LogUtils.e("======ReceiveThread=====接受到数据==原始数据==" + strdata);
                                LogUtils.e("======ReceiveThread=====3333==" + mReceivePacket.getData());
                                if (!"".equals(strdata)) {
                                    LogUtils.e("======ReceiveThread=====66666==");
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (null != mListener) {
                                                LogUtils.e("======ReceiveThread=====发送回调==");
                                                if (CalculateUtils.getDataIfForMe(strdata, activity)) {
                                                    mListener.onSuccess(strdata,mReceivePacket.getAddress());
                                                }
                                            }
                                        }
                                    });
                                }

                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                            if (null != mListener) {
                                mListener.onFailed(e);
                            }
                        }

                    }
                }
            }
        };
        easyFixed2Thread.execute(mReceiveRunnable);
    }


    /**
     * @param data               字节数组
     * @param receiveInetAddress 接收端的intAddress
     * @param receivePort        接收端的port
     */
    public static void startSendMessageBySocket(byte[] data, InetAddress receiveInetAddress, int receivePort, Boolean isBroadcast) {
        if (isBroadcast) {//发送广播
            getSendBroadcastRunnable(data, receiveInetAddress, receivePort);
            easyCacheThread.execute(mSendBroadcastRunnable);
        } else {  //点对点消息
            getSendSocketRunnable(data, receiveInetAddress, receivePort);
            easyCacheThread.execute(mSendRunnable);
        }

    }

    /**
     * 发送广播socket
     *
     * @param data
     * @param inetAddress
     * @param sendPort
     * @return
     */
    private static Runnable getSendBroadcastRunnable(byte[] data, InetAddress inetAddress, int sendPort) {
        mSendBroadcastRunnable = new Runnable() {
            @Override
            public void run() {
                try {
//                    byte[] sendData = data.getBytes();
                    DatagramPacket mSendPacket = new DatagramPacket(data, data.length, inetAddress, sendPort);
                    for (int i = 0; i < 5; i++) {
                        LogUtils.e("发送消息==广播==" + sendPort);
                        Thread.sleep(500);
                        //固定端口
//                      mSendBroadcastSocket = new DatagramSocket(null);
//                      mSendBroadcastSocket.bind(new InetSocketAddress(8005));
                        //随机端口
                        mSendBroadcastSocket = new DatagramSocket();
                        mSendBroadcastSocket.send(mSendPacket);
                        mSendBroadcastSocket.setBroadcast(true);
                        mSendBroadcastSocket.close();
                    }

                } catch (Exception e) {

                }
            }
        };
        return mSendBroadcastRunnable;

    }

    /**
     * 发送点对点socket
     *
     * @param data
     * @param inetAddress
     * @param sendPort
     * @return
     */
    private static Runnable getSendSocketRunnable(byte[] data, InetAddress inetAddress, int sendPort) {
        mSendRunnable = new Runnable() {
            @Override
            public void run() {
                try {
//                    byte[] sendData = data.getBytes();
                    DatagramPacket mSendPacket = new DatagramPacket(data, data.length, inetAddress, sendPort);
                    for (int i = 0; i < 5; i++) {
                        LogUtils.e("发送消息==点对点==" + sendPort);
                        Thread.sleep(500);
                        mSendSocket = new DatagramSocket();
                        mSendSocket.send(mSendPacket);
                        mSendSocket.close();
                    }
                } catch (Exception e) {

                }
            }
        };
        return mSendRunnable;
    }

    /**
     * 1,
     * 关闭接收线程的socket
     */
    public static void closeReceiveSocket() {
        if (null != mReceiveSocket) {
            mReceiveSocket.close();
        }
    }

    /**
     * 2
     * 是否接收信息
     *
     * @param status true为接收
     */
    public static void setIsRuning(Boolean status) {
        isRuning = status;
    }

    /***
     * 备注!!!
     * 备注!!!
     * 1,2两个方法都不使用的时候,不会出现退出界面两次之后接收不到数据
     * 所以这两个方法直接不适用就好
     * 备注!!!
     * 备注!!!
     */
    /**
     * 回调监听
     */
    public interface OnSocketReceiveListener {
        //str   回传过来全部的string数据  和ip地址
        void onSuccess(String str, InetAddress ip);

        void onFailed(Throwable throwable);


    }

    public static void setOnSocketReceiveListener(OnSocketReceiveListener listener) {
        mListener = listener;
    }

    private static OnSocketReceiveListener mListener;

    /**
     * 将获取到的int型ip转成string类型
     */
    private static String getIpString(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
                + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }


//    /**
//     * 开启一个普通回调监听     复制copy版本
//     * <p>
//     * 具体是那个socket的回调通过
//     * 协议:命令cmd,来区分
//     */
//    public static void startNorReceive(int port, Activity activity) {
//        //Wifi状态判断
//        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        if (wifiManager.isWifiEnabled()) {
//            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//            currentIP = getIpString(wifiInfo.getIpAddress());
//        }
////        easyFixed2Thread = ThreadManager.getIO();
//        mReceiveRunnable = new Runnable() {
//            @Override
//            public void run() {
//                LogUtils.e("正在执行Runnable任务：%s" + Thread.currentThread().getName());
//                byte[] receiveData = new byte[1024];
//                DatagramPacket mReceivePacket = new DatagramPacket(receiveData, receiveData.length);
//                try {
//                    mReceiveSocket = new DatagramSocket(port);  //本地监听的端口
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                while (true) {
//                    if (isRuning) {
//                        try {
//                            LogUtils.e("======ReceiveThread=====000==");
//                            LogUtils.e("======ReceiveThread=====mReceivePacket.getAddress()==" + mReceivePacket.getAddress());
//                            LogUtils.e("======ReceiveThread=====currentIP==" + currentIP);
//                            if (!currentIP.equals(mReceivePacket.getAddress())) {   //不是自己的IP不接受
//                                mReceiveSocket.receive(mReceivePacket);
//                                String rec = CalculateUtils.byteArrayToHexString(mReceivePacket.getData()).trim();
//                                //过滤不是发送给我的消息全部不接受
//                                int dd = rec.indexOf("DD");
//                                String strdata = rec.substring(0, dd + 2);
//                                LogUtils.e("======ReceiveThread=====接受到数据==原始数据==" + strdata);
//                                LogUtils.e("======ReceiveThread=====3333==" + mReceivePacket.getData());
//                                if (!"".equals(strdata)) {
//                                    LogUtils.e("======ReceiveThread=====66666==");
//                                    activity.runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            if (null != mListener) {
//                                                LogUtils.e("======ReceiveThread=====发送回调==");
//                                                if (CalculateUtils.getDataIfForMe(strdata, activity)) {
//                                                    mListener.onSuccess(strdata,mReceivePacket.getAddress());
//                                                }
//                                            }
//                                        }
//                                    });
//                                }
//
//                            }
//
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            if (null != mListener) {
//                                mListener.onFailed(e);
//                            }
//                        }
//
//                    }
//                }
//            }
//        };
//        easyFixed2Thread.execute(mReceiveRunnable);
//    }


}
