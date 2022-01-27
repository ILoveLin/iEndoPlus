package com.company.iendo.mineui.socket;

import android.util.Log;

import com.company.iendo.utils.LogUtils;
import com.lzh.easythread.AsyncCallback;
import com.lzh.easythread.EasyThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Callable;

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

    public static void startAsyncReceive(int port) {
        easyFixed2Thread = ThreadManager.getIO();

        // 异步执行任务
        Callable<SocketDataBean> callable = new Callable<SocketDataBean>() {
            @Override
            public SocketDataBean call() throws Exception {
                LogUtils.e("正在执行Runnable任务：%s" + Thread.currentThread().getName());
                byte[] receiveData = new byte[1024];
                DatagramPacket mReceivePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    mReceiveSocket = new DatagramSocket(port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (true) {
                    if (isRuning) {
                        try {
                            LogUtils.e("======SocketManage=====000==");
                            mReceiveSocket.receive(mReceivePacket);
                            LogUtils.e("======SocketManage=====111==");
                            if (mReceivePacket != null) {
                                SocketDataBean socketDataBean = new SocketDataBean();
                                socketDataBean.setData("" + mReceivePacket.getData());
                                LogUtils.e("startAsyncReceive==receivedata===" + mReceivePacket.getData());
                                return socketDataBean;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
        };

        // 异步回调
        AsyncCallback<SocketDataBean> async = new AsyncCallback<SocketDataBean>() {
            @Override
            public void onSuccess(SocketDataBean bean) {
                // notify success;
                LogUtils.e("======SocketManage=====onSuccess==" + bean.getData());
                if (null != mListener) {
                    mListener.onSuccess(bean);
                }

            }

            @Override
            public void onFailed(Throwable t) {
                // notify failed.
                LogUtils.e("======SocketManage=====onFailed==");
                if (null != mListener) {
                    mListener.onFailed(t);
                }

            }
        };

        // 启动异步任务
        easyFixed2Thread.async(callable, async);

    }


    /**
     * @param data        UDP发包数据(<16进制字符串>--转--<字节数组>)
     * @param inetAddress 接收端的intAddress
     * @param sendPort    接收端的port
     */
//    public static void startSendMessageBySocket(String data, InetAddress inetAddress, int sendPort, Boolean isBroadcast) {
//
//
//        easyCacheThread = ThreadManager.getCache();
//        if (isBroadcast) {//发送广播
//            getSendBroadcastRunnable(data, inetAddress, sendPort);
//            easyCacheThread.execute(mSendBroadcastRunnable);
//        } else {  //点对点消息
//            getSendSocketRunnable(data, inetAddress, sendPort);
//            easyCacheThread.execute(mSendRunnable);
//        }
//
//    }
    public static void startSendMessageBySocket(byte[] data, InetAddress inetAddress, int sendPort, Boolean isBroadcast) {


        easyCacheThread = ThreadManager.getCache();
        if (isBroadcast) {//发送广播
            getSendBroadcastRunnable(data, inetAddress, sendPort);
            easyCacheThread.execute(mSendBroadcastRunnable);
        } else {  //点对点消息
            getSendSocketRunnable(data, inetAddress, sendPort);
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
                    mSendBroadcastSocket = new DatagramSocket();
                    mSendBroadcastSocket.send(mSendPacket);
                    mSendBroadcastSocket.setBroadcast(true);
                    mSendBroadcastSocket.close();
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
                    for (int i = 0; i < 2; i++) {
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
     * 关闭接收线程的socket
     */
    public static void closeReceiveSocket() {
        if (null != mReceiveSocket) {
            mReceiveSocket.close();
        }
    }

    /**
     * 是否接收信息
     *
     * @param status true为接收
     */
    public static void setIsRuning(Boolean status) {
        isRuning = status;
    }


    /**
     * 回调监听
     */
    public interface OnSocketListener {
        void onSuccess(SocketDataBean bean);

        void onFailed(Throwable throwable);

    }

    public static void setOnSocketListener(OnSocketListener listener) {
        mListener = listener;
    }

    private static OnSocketListener mListener;


}
