package com.company.iendo.utils;

/**
 * Created by Lovelin on 2019/3/13
 * <p>
 * Describe:
 */

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * SharePreference  工具类
 */
public class SharePreferenceUtil {
    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "share_data";
    public static final String UID = "uid";

    public static final String Current_Port = "port";
    public static final String Current_Host = "host";  // 加了---/---的

    public static final String Current_BaseUrl = "BaseUrl";     //末尾不加斜杠  http://192.168.66.42:8008
    public static final String OnLine_Flag = "OnLine";             //登入方式--true=在线登录,false=离线登录
    public static final String Current_Chose_CaseID = "Current_Chose_CaseID";    //当前选中的病例ID


    public static final String Current_Login_Role = "login_role";  //登录用户的role  权限
    public static final String Current_Login_UserID = "login_userid";  //登录用户的ID
    public static final String Current_Login_UserName = "login_username";  //登录用户的username
    public static final String Current_Login_Password = "login_password";  //登录用户的password

    // 角色 0-超级管理员 1-管理员 2-操作员 3-查询员 4-自定义
    // 角色 0-超级管理员 1-管理员 2-操作员 3-查询员 4-自定义

    /**
     * 登入界面记住密码,第一次用户表不存在bug是因为还没存入过和创建用户表
     */
    public static final String Flag_UserDBSave = "Flag_UserDBSave";                //用户表是否创建过
    /**
     * 被选中当前登入设备的信息
     */
//    public static final String Current_MainID = "Current_MainID";                    //设备主键ID--//这个主键ID是需要绑定用户表中的deviceID,确保是这个设备下,离线模式能通过此字段查询绑定用户
    public static final String Current_DeviceID = "Current_DeviceID";                //设备唯一标识--//这个主键ID是需要绑定用户表中的deviceID,确保是这个设备下,离线模式能通过此字段查询绑定用户
    public static final String Current_IP = "Current_IP";                            //设备ip    直播和通讯的ip
    public static final String Current_HttpPort = "Current_HttpPort";                //设备http端口
    public static final String Current_SocketPort = "Current_SocketPort";            //socket端口
    public static final String Current_LivePort = "Current_LivePort";                //直播端口
    public static final String Current_MicPort = "Current_MicPort";                  //语音端口
    public static final String Current_DeviceUsername = "Current_DeviceUsername";    //设备账号(直播)
    public static final String Current_DevicePassword = "Current_DevicePassword";    //设备密码(直播)
    public static final String Current_Type = "Current_Type";                        //设备类型  --一体机 ,耳鼻喉治疗台等等类型
    public static final String Current_EndoType = "Current_EndoType";                //工作站类型
    public static final String Current_Type_Num = "Current_Type_Num";                //工作站类型
    /**
     * mDeviceCode  mDBBean.getDeviceName----这个是智能搜索之后返回过来的设备码
     * 需要再搜索完成后创建dialog的时候设置上去,不然为null
     */
    public static final String Current_DeviceCode = "Current_DeviceCode";             //设备码--后台返回和Current_DeviceID 相同的
    public static final String Current_DeviceName = "Current_Usemsg01";               //
    public static final String Current_MSelected = "Current_MSelected";               //是否被选中:0未选中,1被选中


    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    public static void put(Context context, String key, Object object) {

        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     *
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }


}
