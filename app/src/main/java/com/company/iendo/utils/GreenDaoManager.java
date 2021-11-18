package com.company.iendo.utils;

import android.app.Application;
import android.content.Context;

import com.company.iendo.app.AppActivity;
import com.company.iendo.app.AppApplication;
import com.company.iendo.green.db.DaoSession;
import com.company.iendo.green.db.UserDBBean;
import com.company.iendo.green.db.UserDBBeanDao;
import com.company.iendo.utils.db.DBManager;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/16 15:33
 * desc：
 */
public class GreenDaoManager {

    private Context mContext;
    private  UserDBBeanDao mUserDao;

    public GreenDaoManager (Context context) {
        this.mContext = context;
        // 获取DAO实例
//        mUserDao = AppApplication.getDaoSession().getUserDBBeanDao();

        //数据库升级
        mUserDao = DBManager.getDaoSession(context).getUserDBBeanDao();
    }

    /**
     * 新增数据
     * 添加一个实体
     * DAO.insert(T entity);
     * 添加多个实体
     * DAO.insertInTx(T... entities);
     */

    public  void insertUserName(){

    }


    /**
     * 查询数据
     * DAO.loadAll();
     * 根据主键查询
     * DAO.load(Long key);
     */
    public List<UserDBBean> queryAllUser(){
        return mUserDao.loadAll();
    }


    /**
     * 利用 QueryBuilder 与 properties 设置查询条件
     */
//// 查询水果的数据
//    public List<UserDBBean> queryUserByMessage () {
//        QueryBuilder<UserDBBean> result = mUserDao.queryBuilder();
//        //借助Property属性类提供的筛选方法
//        result = result.where(UserDBBean.Properties.Type.eq("0")).orderAsc(GoodsModelDao.Properties.GoodsId);
//        return result.list();
//    }




    /**
     * 更新数据
     * DAO.update(T entity);
     * DAO.updateInTx(T... entities);
     */

    // 修改指定商品的商品信息
    public void updateUser (UserDBBean model) {
        mUserDao.update(model);
    }


    /**
     * 删除数据
     * DAO.delete(T entity);
     * DAO.deleteAll();
     * DAO.deleteByKey(K key);
     */

    // 删除指定商品
    public void deleteUser (UserDBBean model) {
        mUserDao.deleteByKey(model.getId());
    }
















}
