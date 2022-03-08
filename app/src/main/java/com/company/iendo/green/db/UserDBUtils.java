package com.company.iendo.green.db;

import android.content.Context;
import android.util.Log;

import com.company.iendo.utils.db.DBManager;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/26 9:36
 * desc：用户表工具类
 */
public class UserDBUtils {

    //插入或者替换,如果没有,插入,如果有,替换
    public static void insertOrReplaceInTx(Context context, UserDBBean bean) {
        DBManager.getDaoSession(context).getUserDBBeanDao().insertOrReplaceInTx(bean);
    }

    //更新
    public static void update(Context context, UserDBBean bean) {
        DBManager.getDaoSession(context).getUserDBBeanDao().update(bean);
    }

    //删除
    public static void delete(Context context, UserDBBean bean) {
        DBManager.getDaoSession(context).getUserDBBeanDao().delete(bean);
    }

    //删除
    public static void deleteAll(Context context) {
        DBManager.getDaoSession(context).getUserDBBeanDao().deleteAll();
    }

    //查询全部
    public static List<UserDBBean> queryAll(Context context) {
        //查询所有数据
        List<UserDBBean> list = DBManager.getDaoSession(context).getUserDBBeanDao().queryBuilder().list();
        return list;
    }

    //条件查询
    //精确查询  获取到bean
    public static UserDBBean getQueryBeanById(Context context, Long id) {
        UserDBBeanDao userDBBeanDao = DBManager.getDaoSession(context).getUserDBBeanDao();

        UserDBBean queryBean = userDBBeanDao.queryBuilder().where(UserDBBeanDao.Properties.Id.eq("1")).unique();

        return queryBean;

    }

    /**
     * @param context
     * @param id
     * @return
     */
    //条件查询
    //精确查询 活得list
    public static List<UserDBBean> getQueryList(Context context, Long id) {
        UserDBBeanDao userDBBeanDao = DBManager.getDaoSession(context).getUserDBBeanDao();
        List<UserDBBean> beanList = userDBBeanDao.queryBuilder().where(UserDBBeanDao.Properties.Id.notEq("1")).list();
        return beanList;
    }
    public static List<UserDBBean> getQueryByDeviceID(Context context, String ID) {

        UserDBBeanDao userDBBeanDao = DBManager.getDaoSession(context).getUserDBBeanDao();
        List<UserDBBean> beanList = userDBBeanDao.queryBuilder().where(UserDBBeanDao.Properties.DeviceID.notEq(ID)).list();
        return beanList;
    }

    public static UserDBBean queryListByName(Context context, String UserName) {
        List<UserDBBean> list = DBManager.getDaoSession(context).getUserDBBeanDao().queryBuilder().list();
        if (0 != list.size()) {
            for (int i = 0; i < list.size(); i++) {
                UserDBBean userDBBean = list.get(i);
                if (userDBBean.getUserName().equals(UserName)) {
                    return userDBBean;
                }
            }
        }
        return list.get(0);
    }


    /**
     * @param
     * @return 查询是否存在
     */
    public static boolean queryListIsExist(Context context, String UserName) {
        List<UserDBBean> list = DBManager.getDaoSession(context).getUserDBBeanDao().queryBuilder().list();
        Boolean isExit = false;
        if (0 != list.size()) {
            for (int i = 0; i < list.size(); i++) {
                UserDBBean userDBBean = list.get(i);
                if (userDBBean.getUserName().equals(UserName)) {
                    isExit = true;
                }
            }
        }
        return isExit;
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
//        UserDBBeanDao UserDBBeanDao = DBManager.getDaoSession(context).getUserDBBeanDao();
//
//        final Query query = UserDBBeanDao.queryBuilder().build();
//        new Thread() {
//            @Override
//            public void run() {
//                List list = query.forCurrentThread().list();
//                Log.d("queryThread", "run() called" + list);
//            }
//        }.start();
//    }


}
