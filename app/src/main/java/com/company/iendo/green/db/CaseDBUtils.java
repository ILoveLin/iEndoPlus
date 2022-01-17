package com.company.iendo.green.db;

import android.content.Context;

import com.company.iendo.green.db.downcase.CaseDBBean;
import com.company.iendo.utils.db.DBManager;
import com.company.iendo.green.db.CaseDBBeanDao;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/26 9:36
 * desc：设备表工具类
 */
public class CaseDBUtils {

    //插入或者替换,如果没有,插入,如果有,替换
    public static void insertOrReplaceInTx(Context context, CaseDBBean bean) {
        DBManager.getDaoSession(context).getCaseDBBeanDao().insertOrReplaceInTx(bean);
    }

    //更新
    public static void update(Context context, CaseDBBean bean) {
        DBManager.getDaoSession(context).getCaseDBBeanDao().update(bean);
    }

    //删除
    public static void delete(Context context, CaseDBBean bean) {
        DBManager.getDaoSession(context).getCaseDBBeanDao().delete(bean);
    }
    //删除
    public static void deleteAll(Context context) {
        DBManager.getDaoSession(context).getCaseDBBeanDao().deleteAll();
    }

    //查询全部
    public static List<CaseDBBean> queryAll(Context context) {
        //查询所有数据
        List<CaseDBBean> list = DBManager.getDaoSession(context).getCaseDBBeanDao().queryBuilder().list();
        return list;
    }

    /**
     * eq 相等
     * noteq 不相等
     * like 模糊搜索
     * @param context
     * @param DeviceCaseID
     * @return  精确查询  获取到bean
     */

    public static CaseDBBean getQueryBeanById(Context context, String DeviceCaseID) {
        CaseDBBeanDao caseDBBeanDao = DBManager.getDaoSession(context).getCaseDBBeanDao();

        CaseDBBean queryBean = caseDBBeanDao.queryBuilder().where(CaseDBBeanDao.Properties.DeviceCaseID.eq(DeviceCaseID)).unique();

        return queryBean;


    }
    /**
     * eq 相等
     * noteq 不相等
     * like 模糊搜索
     * @param context
     * @param DeviceCaseID
     * @return  精确查询  获取到list
     */

    public static List<CaseDBBean> getQueryList(Context context, String DeviceCaseID) {
        CaseDBBeanDao caseDBBeanDao = DBManager.getDaoSession(context).getCaseDBBeanDao();
        List<CaseDBBean> beanList = caseDBBeanDao.queryBuilder().where(CaseDBBeanDao.Properties.DeviceCaseID.notEq( DeviceCaseID)).list();
        return beanList;
    }

//    private void queryThread(Context context) {
//        CaseDBBeanDao CaseDBBeanDao = DBManager.getDaoSession(context).getCaseDBBeanDao();
//
//        final Query query = CaseDBBeanDao.queryBuilder().build();
//        new Thread() {
//            @Override
//            public void run() {
//                List list = query.forCurrentThread().list();
//                Log.d("queryThread", "run() called" + list);
//            }
//        }.start();
//    }


}
