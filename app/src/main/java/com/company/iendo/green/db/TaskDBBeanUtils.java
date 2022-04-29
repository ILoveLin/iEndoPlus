package com.company.iendo.green.db;

import android.content.Context;

import com.company.iendo.utils.db.DBManager;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/26 9:36
 * desc：下载任务表  工具类
 */
public class TaskDBBeanUtils {

    //插入或者替换,如果没有,插入,如果有,替换
    public static void insertOrReplaceInTx(Context context, TaskDBBean bean) {
        DBManager.getDaoSession(context).getTaskDBBeanDao().insertOrReplaceInTx(bean);
    }

    //更新
    public static void update(Context context, TaskDBBean bean) {
        DBManager.getDaoSession(context).getTaskDBBeanDao().update(bean);
    }

    //删除
    public static void delete(Context context, TaskDBBean bean) {
        DBManager.getDaoSession(context).getTaskDBBeanDao().delete(bean);
    }

    //删除
    public static void deleteAll(Context context) {
        DBManager.getDaoSession(context).getTaskDBBeanDao().deleteAll();
    }

    //查询全部
    public static List<TaskDBBean> queryAll(Context context) {
        //查询所有数据
        List<TaskDBBean> list = DBManager.getDaoSession(context).getTaskDBBeanDao().queryBuilder().list();
        return list;
    }

    //条件查询
    //精确查询  获取到bean
    public static TaskDBBean getQueryBeanById(Context context, Long id) {
        TaskDBBeanDao bean = DBManager.getDaoSession(context).getTaskDBBeanDao();

        TaskDBBean queryBean = bean.queryBuilder().where(TaskDBBeanDao.Properties.Id.eq(id)).unique();

        return queryBean;

    }

    //条件查询
    //查询出当前设备,当前病例下所有下载任务
    public static List<TaskDBBean> getQueryBeanByCommonCode(Context context, String CommonCode) {
        TaskDBBeanDao bean = DBManager.getDaoSession(context).getTaskDBBeanDao();
        List<TaskDBBean> list = bean.queryBuilder().where(TaskDBBeanDao.Properties.CommonCode.eq(CommonCode)).list();

        return list;

    }

    //条件查询
    //精确查询  获取具体某个下载任务
    public static List<TaskDBBean> getQueryBeanBySingleCode(Context context, String SingleCode) {
        TaskDBBeanDao bean = DBManager.getDaoSession(context).getTaskDBBeanDao();
        List<TaskDBBean> list = bean.queryBuilder().where(TaskDBBeanDao.Properties.SingleCode.eq(SingleCode)).list();
        return list;

    }


}
