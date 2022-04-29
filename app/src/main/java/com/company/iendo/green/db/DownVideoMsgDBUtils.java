package com.company.iendo.green.db;

import android.content.Context;

import com.company.iendo.green.db.downcase.dwonmsg.DownVideoMessage;
import com.company.iendo.utils.db.DBManager;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/26 9:36
 * desc：用户表工具类
 */
public class DownVideoMsgDBUtils {

    //插入或者替换,如果没有,插入,如果有,替换
    public static void insertOrReplaceInTx(Context context, DownVideoMessage bean) {
        DBManager.getDaoSession(context).getDownVideoMessageDao().insertOrReplaceInTx(bean);
    }

    //更新
    public static void update(Context context, DownVideoMessage bean) {
        DBManager.getDaoSession(context).getDownVideoMessageDao().update(bean);
    }

    //删除
    public static void delete(Context context, DownVideoMessage bean) {
        DBManager.getDaoSession(context).getDownVideoMessageDao().delete(bean);
    }

    //删除
    public static void deleteAll(Context context) {
        DBManager.getDaoSession(context).getDownVideoMessageDao().deleteAll();
    }

    //查询全部
    public static List<DownVideoMessage> queryAll(Context context) {
        //查询所有数据
        List<DownVideoMessage> list = DBManager.getDaoSession(context).getDownVideoMessageDao().queryBuilder().list();
        return list;
    }

    //条件查询
    //精确查询  获取到bean
    public static DownVideoMessage getQueryBeanById(Context context, Long id) {
        DownVideoMessageDao bean = DBManager.getDaoSession(context).getDownVideoMessageDao();

        DownVideoMessage queryBean = bean.queryBuilder().where(DownVideoMessageDao.Properties.Id.eq("1")).unique();

        return queryBean;

    }

    //条件查询
    //精确查询  获取到bean
    public static List<DownVideoMessage> getQueryBeanByTag(Context context, String Name) {
        DownVideoMessageDao bean = DBManager.getDaoSession(context).getDownVideoMessageDao();
        List<DownVideoMessage> list = bean.queryBuilder().where(DownVideoMessageDao.Properties.Tag.eq(Name)).list();

        return list;

    }

    //条件查询
    //精确查询  获取到bean
    public static List<DownVideoMessage> getQueryBeanByCode(Context context, String Code) {
        DownVideoMessageDao bean = DBManager.getDaoSession(context).getDownVideoMessageDao();
        List<DownVideoMessage> list = bean.queryBuilder().where(DownVideoMessageDao.Properties.DeviceCode.eq(Code)).list();

        return list;

    }

    public static List<DownVideoMessage> getQueryBeanByTow(Context context, String deviceCode, String CaseID) {
        DownVideoMessageDao bean = DBManager.getDaoSession(context).getDownVideoMessageDao();

        List<DownVideoMessage> beanList = bean.queryBuilder().where(DownVideoMessageDao.Properties.DeviceCode.eq(deviceCode), DownVideoMessageDao.Properties.SaveCaseID.eq(CaseID)).list();

        return beanList;


    }

    public static List<DownVideoMessage> getQueryBeanByTowCodeUserID(Context context, String code, String UserID) {
        DownVideoMessageDao bean = DBManager.getDaoSession(context).getDownVideoMessageDao();

        List<DownVideoMessage> beanList = bean.queryBuilder().where(DownVideoMessageDao.Properties.Tag.eq(code), DownVideoMessageDao.Properties.Tag.eq(UserID)).list();

        return beanList;


    }

    /**
     * 三条件查询  查询当前设备下,当前病例id下, 标题为tag的视频,是否存在
     *
     * @param context
     * @param deviceID
     * @param caseID
     * @param tag
     * @return
     */

    public static List<DownVideoMessage> getQueryBeanByThree(Context context, String deviceID, String caseID, String tag) {
        DownVideoMessageDao bean = DBManager.getDaoSession(context).getDownVideoMessageDao();

        List<DownVideoMessage> beanList = bean.queryBuilder().where(DownVideoMessageDao.Properties.DeviceCode.eq(deviceID),
                DownVideoMessageDao.Properties.SaveCaseID.eq(caseID), DownVideoMessageDao.Properties.Tag.eq(tag)).list();

        return beanList;


    }

    /**
     * @param context
     * @param id
     * @return
     */
    //条件查询
    //精确查询 活得list
    public static List<DownVideoMessage> getQueryList(Context context, Long id) {
        DownVideoMessageDao bean = DBManager.getDaoSession(context).getDownVideoMessageDao();
        List<DownVideoMessage> beanList = bean.queryBuilder().where(DownVideoMessageDao.Properties.Id.notEq("1")).list();
        return beanList;
    }

    public static List<DownVideoMessage> getQueryByDeviceID(Context context, String ID) {

        DownVideoMessageDao bean = DBManager.getDaoSession(context).getDownVideoMessageDao();
        List<DownVideoMessage> beanList = bean.queryBuilder().where(DownVideoMessageDao.Properties.Tag.eq(ID)).list();
        return beanList;
    }

    public static DownVideoMessage queryListByName(Context context, String UserName) {
        List<DownVideoMessage> list = DBManager.getDaoSession(context).getDownVideoMessageDao().queryBuilder().list();
        if (0 != list.size()) {
            for (int i = 0; i < list.size(); i++) {
//                DownVideoMessage DownVideoMessage = list.get(i);
//                if (DownVideoMessage.getUserName().equals(UserName)) {
//                    return DownVideoMessage;
//                }
            }
        }
        return list.get(0);
    }


    /**
     * @param
     * @return 查询是否存在
     */
    public static boolean queryListIsExist(Context context, String UserName) {
        List<DownVideoMessage> list = DBManager.getDaoSession(context).getDownVideoMessageDao().queryBuilder().list();
        Boolean isExit = false;
        if (0 != list.size()) {
            for (int i = 0; i < list.size(); i++) {
                DownVideoMessage DownVideoMessage = list.get(i);
//                if (DownVideoMessage.getUserName().equals(UserName)) {
//                    isExit = true;
//                }
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
//        DownVideoMessageDao DownVideoMessageDao = DBManager.getDaoSession(context).getDownVideoMessageDao();
//
//        final Query query = DownVideoMessageDao.queryBuilder().build();
//        new Thread() {
//            @Override
//            public void run() {
//                List list = query.forCurrentThread().list();
//                Log.d("queryThread", "run() called" + list);
//            }
//        }.start();
//    }


}
