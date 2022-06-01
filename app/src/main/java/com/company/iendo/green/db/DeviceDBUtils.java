package com.company.iendo.green.db;

import android.content.Context;

import com.company.iendo.utils.db.DBManager;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/26 9:36
 * desc：设备表工具类
 */
public class DeviceDBUtils {

    //插入或者替换,如果没有,插入,如果有,替换  --传入的对象主键如果存在于数据库中，有则更新，否则插入
    public static void insertOrReplaceInTx(Context context, DeviceDBBean bean) {
        DBManager.getDaoSession(context).getDeviceDBBeanDao().insertOrReplaceInTx(bean);
    }


    //插入或者替换,如果没有,插入,如果有,替换
    public static void insertOrReplace(Context context, DeviceDBBean bean) {
        DBManager.getDaoSession(context).getDeviceDBBeanDao().insertOrReplace(bean);

    }

    //更新
    public static void update(Context context, DeviceDBBean bean) {
        DBManager.getDaoSession(context).getDeviceDBBeanDao().update(bean);
    }

    //删除
    public static void delete(Context context, DeviceDBBean bean) {
        DBManager.getDaoSession(context).getDeviceDBBeanDao().delete(bean);
    }

    //删除
    public static void deleteAll(Context context) {
        DBManager.getDaoSession(context).getDeviceDBBeanDao().deleteAll();
    }

    //查询全部
    public static List<DeviceDBBean> queryAll(Context context) {
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
    //精确查询  获取到bean  授权接入是否存入DB的标识
    public static List getQueryBeanBySelected(Context context, Boolean mSelected) {
        DeviceDBBeanDao deviceDBBeanDao = DBManager.getDaoSession(context).getDeviceDBBeanDao();
        List<DeviceDBBean> list = deviceDBBeanDao.queryBuilder().where(DeviceDBBeanDao.Properties.MSelected.eq(mSelected)).list();
        if (null == list) {
            return null;
        } else {
            return list;
        }

    }

    //条件查询
    //精确查询  获取到bean  授权接入是否存入DB的标识
    public static DeviceDBBean getQueryBeanByAcceptAndInsertDB(Context context, String acceptAndInsertDB) {
        DeviceDBBeanDao deviceDBBeanDao = DBManager.getDaoSession(context).getDeviceDBBeanDao();
        List<DeviceDBBean> list = deviceDBBeanDao.queryBuilder().where(DeviceDBBeanDao.Properties.AcceptAndInsertDB.eq(acceptAndInsertDB)).list();
        if (null == list) {
            return null;
        } else {
            for (int i = 0; i < list.size(); i++) {
                DeviceDBBean deviceDBBean = list.get(i);
                boolean deviceCode = (acceptAndInsertDB).equals(deviceDBBean.getAcceptAndInsertDB());     // code 存在并且,type存在--返回bean对象,说明数据库有该条数据
                if (deviceCode) {
                    return deviceDBBean;
                } else {
                    return null;
                }
            }


        }
        return null;

    }

    //条件查询
    //精确查询  获取到bean
    public static DeviceDBBean getQueryBeanByCode(Context context, String code, String Type) {
        DeviceDBBeanDao deviceDBBeanDao = DBManager.getDaoSession(context).getDeviceDBBeanDao();
        Query<DeviceDBBean> query = deviceDBBeanDao.queryBuilder().where(DeviceDBBeanDao.Properties.DeviceCode.eq(
                code)).build();
        List<DeviceDBBean> beanList = query.list();
        if ("0".equals(beanList.size())) {
            return null;
        } else {
            for (int i = 0; i < beanList.size(); i++) {
                boolean type = (Type).equals(beanList.get(i).getType());
                if (type) {
                    return beanList.get(i);
                } else {
                    return null;
                }
            }
            return null;

        }


//        DeviceDBBean unique = deviceDBBeanDao.queryBuilder().where(DeviceDBBeanDao.Properties.DeviceCode.eq(code)).unique();
//        if (null == unique) {
//            return null;
//        } else {
//            boolean type = (Type).equals(unique.getType());     // code 存在并且,type存在--返回bean对象,说明数据库有该条数据
//            if (type) {
//                return unique;
//            } else {
//                return null;
//            }
//        }


    }

    //条件查询
    //精确查询  获取到bean
    public static DeviceDBBean getQueryBeanByType(Context context, String code, String Type) {

        DeviceDBBeanDao deviceDBBeanDao = DBManager.getDaoSession(context).getDeviceDBBeanDao();
        Query<DeviceDBBean> query = deviceDBBeanDao.queryBuilder().where(DeviceDBBeanDao.Properties.Type.eq(
                Type)).build();
        List<DeviceDBBean> beanList = query.list();
        if ("0".equals(beanList.size())) {
            return null;
        } else {
            for (int i = 0; i < beanList.size(); i++) {
                boolean codeFlag = (code).equals(beanList.get(i).getDeviceCode());
                if (codeFlag) {
                    return beanList.get(i);
                } else {
                    return null;
                }
            }
            return null;
        }

//        DeviceDBBeanDao deviceDBBeanDao = DBManager.getDaoSession(context).getDeviceDBBeanDao();
//        DeviceDBBean unique = deviceDBBeanDao.queryBuilder().where(DeviceDBBeanDao.Properties.Type.eq(Type)).unique();
//        if (null == unique) {
//            return null;
//        } else {
//            boolean deviceCode = (code).equals(unique.getDeviceCode());     // code 存在并且,type存在--返回bean对象,说明数据库有该条数据
//            if (deviceCode) {
//                return unique;
//            } else {
//                return null;
//            }
//        }

    }


    /**
     * @param context
     * @param id
     * @return
     */
    //条件查询
    //精确查询 活得list
    public static List<DeviceDBBean> getQueryList(Context context, Long id) {
        DeviceDBBeanDao deviceDBBeanDao = DBManager.getDaoSession(context).getDeviceDBBeanDao();
        List<DeviceDBBean> beanList = deviceDBBeanDao.queryBuilder().where(DeviceDBBeanDao.Properties.DeviceID.notEq("1")).list();
        return beanList;
    }
//    QueryBuilder 查询
//    List joes = userDao.queryBuilder()  // 查询 User
//            .where(Properties.FirstName.eq("Joe"))  // 首名为 Joe
//            .orderAsc(Properties.LastName)  // 末名升序排列
//            .list();  // 返回集合
//
//    // Joe，>= 1970.10
//    QueryBuilder qb = userDao.queryBuilder();
//qb.where(Properties.FirstName.eq("Joe"),
//        qb.or(Properties.YearOfBirth.gt(1970),
//        qb.and(Properties.YearOfBirth.eq(1970), Properties.MonthOfBirth.ge(10))));
//    List youngJoes = qb.list();


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
