package com.company.iendo.green.db;

import android.content.Context;
import android.util.Log;

import com.company.iendo.utils.db.DBManager;

import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/26 9:36
 * desc：设备表工具类
 */
public class DeviceDBUtils {

    //插入或者替换,如果没有,插入,如果有,替换
    public static void insertOrReplaceInTx(Context context, DeviceDBBean bean) {
        DBManager.getDaoSession(context).getDeviceDBBeanDao().insertOrReplaceInTx(bean);
    }

    //更新
    public static void update(Context context, DeviceDBBean bean) {
        DBManager.getDaoSession(context).getDeviceDBBeanDao().update(bean);
    }

    //删除
    public static void delete(Context context, DeviceDBBean bean) {
        DBManager.getDaoSession(context).getDeviceDBBeanDao().delete(bean);
    }

    //查询全部
    public static List<DeviceDBBean> queryAll(Context context, DeviceDBBean bean) {
        //查询所有数据
        List<DeviceDBBean> list = DBManager.getDaoSession(context).getDeviceDBBeanDao().queryBuilder().list();
        return list;
    }

    //条件查询
    //精确查询  获取到bean
    public static DeviceDBBean getQueryBeanById(Context context, Long id) {
        DeviceDBBeanDao deviceDBBeanDao = DBManager.getDaoSession(context).getDeviceDBBeanDao();

        DeviceDBBean queryBean = deviceDBBeanDao.queryBuilder().where(DeviceDBBeanDao.Properties.DeviceID.eq("1")).unique();

        return queryBean;

    }

    //条件查询
    //精确查询 活得list
    public static List<DeviceDBBean> getQueryList(Context context, Long id) {
        DeviceDBBeanDao deviceDBBeanDao = DBManager.getDaoSession(context).getDeviceDBBeanDao();
        List<DeviceDBBean> beanList = deviceDBBeanDao.queryBuilder().where(DeviceDBBeanDao.Properties.DeviceID.notEq("1")).list();
        return beanList;
    }

//    private void queryThread(Context context) {
//        DeviceDBBeanDao deviceDBBeanDao = DBManager.getDaoSession(context).getDeviceDBBeanDao();
//
//        final Query query = deviceDBBeanDao.queryBuilder().build();
//        new Thread() {
//            @Override
//            public void run() {
//                List list = query.forCurrentThread().list();
//                Log.d("queryThread", "run() called" + list);
//            }
//        }.start();
//    }


}
