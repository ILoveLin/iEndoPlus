package com.company.iendo.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/7/27 8:50
 * desc：
 */
public class MD5ChangeUtil {
    /**
     * md5 16位 加密
     *
     * @param plainText
     * @return
     */
    public static String Md5_16(String plainText) {
        StringBuffer buf = new StringBuffer("");
        int i;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            System.out.println("result: " + buf.toString());// 32位的加密

            System.out.println("result: " + buf.toString().substring(8, 24));// 16位的加密

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString().substring(8, 24);
    }

    /**
     * md5 32位 加密
     *
     * @param plainText
     */
    public static String Md5_32(String plainText) {
        StringBuffer buf = new StringBuffer("");
        int i;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            System.out.println("result: " + buf.toString());// 32位的加密

            System.out.println("result: " + buf.toString().substring(8, 24));// 16位的加密

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    public static enum EncryptLength {
        EL_16, EL_32
    }

    public static String calcMD5(String plainText, EncryptLength encryptLength) {
        // 返回字符串
        String md5Str = null;
        try {
            // 操作字符串
            StringBuilder buf = new StringBuilder();
            /**
             * MessageDigest 类为应用程序提供信息摘要算法的功能，如 MD5 或 SHA 算法。
             * 信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
             *
             * MessageDigest 对象开始被初始化。 该对象通过使用 update()方法处理数据。 任何时候都可以调用
             * reset()方法重置摘要。 一旦所有需要更新的数据都已经被更新了，应该调用digest()方法之一完成哈希计算。
             *
             * 对于给定数量的更新数据，digest 方法只能被调用一次。 在调用 digest 之后，MessageDigest
             * 对象被重新设置成其初始状态。
             */
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 添加要进行计算摘要的信息,使用 plainText 的 byte 数组更新摘要。
            md.update(plainText.getBytes());
            // 计算出摘要,完成哈希计算。
            byte b[] = md.digest();
            int i;
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                // 将整型 十进制 i 转换为16位，用十六进制参数表示的无符号整数值的字符串表示形式。
                buf.append(Integer.toHexString(i));
            }
            switch (encryptLength) {
                case EL_32:
                    md5Str = buf.toString();
                    break;
                case EL_16:
                    md5Str = buf.toString().substring(8, 24);
                    break;
                default:
                    md5Str = buf.toString().substring(8, 24);
                    break;
            }
        } catch (Exception e) {
            Log.e(MD5ChangeUtil.class.getSimpleName(),e.getMessage());
        }
        return md5Str;
    }

    /**
     * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合
     */
    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    protected static MessageDigest messagedigest = null;

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsaex) {
            nsaex.printStackTrace();
        }
    }

    /**
     * 生成字符串的md5校验值
     *
     * @param s
     * @return
     */
    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    /**
     * 判断字符串的md5校验码是否与一个已知的md5码相匹配
     *
     * @param password
     *            要校验的字符串
     * @param md5PwdStr
     *            已知的md5校验码
     * @return
     */
    public static boolean checkPassword(String password, String md5PwdStr) {
        String s = getMD5String(password);
        return s.equals(md5PwdStr);
    }

    /**
     * 生成文件的md5校验值
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String getFileMD5String(File file) throws IOException {
        InputStream fis;
        fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int numRead = 0;
        while ((numRead = fis.read(buffer)) > 0) {
            messagedigest.update(buffer, 0, numRead);
        }
        fis.close();
        return bufferToHex(messagedigest.digest());
    }

    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];// 取字节中高 4 位的数字转换, >>>
        // 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
        char c1 = hexDigits[bt & 0xf];// 取字节中低 4 位的数字转换
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
}