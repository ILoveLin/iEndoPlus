package com.company.iendo.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.company.iendo.green.db.DaoMaster;
import com.company.iendo.green.db.DaoSession;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/17 11:29
 * desc：直接使用如下代码,可以对原来表 添加字段升级等等
 * https://www.cnblogs.com/Sharley/p/8884263.html   使用案例
 *
 * DBManager.getDaoSession(getApplicationContext()).getBeanDao();
 */
public class DBManager {

    // 是否加密
    public static final boolean ENCRYPTED = true;

    private static final String DB_NAME = "green.db";
    private static DBManager mDBManager;
    private static DaoMaster.DevOpenHelper mDevOpenHelper;
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;

    private Context mContext;

    public DBManager(Context context) {
        this.mContext = context;
        // 初始化数据库信息
        mDevOpenHelper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        getDaoMaster(context);
        getDaoSession(context);
    }

    public static DBManager getInstance(Context context) {
        if (null == mDBManager) {
            synchronized (DBManager.class) {
                if (null == mDBManager) {
                    mDBManager = new DBManager(context);
                }
            }
        }
        return mDBManager;
    }

    /**
     * 获取可读数据库
     *
     * @param context
     * @return
     */
    public static SQLiteDatabase getReadableDatabase(Context context) {
        if (null == mDevOpenHelper) {
            getInstance(context);
        }
        return mDevOpenHelper.getReadableDatabase();
    }

    /**
     * 获取可写数据库
     *
     * @param context
     * @return
     */
    public static SQLiteDatabase getWritableDatabase(Context context) {
        if (null == mDevOpenHelper) {
            getInstance(context);
        }
        return mDevOpenHelper.getWritableDatabase();
    }

    /**
     * 获取DaoMaster
     * <p>
     * 判断是否存在数据库，如果没有则创建数据库
     *
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context) {
        if (null == mDaoMaster) {
            synchronized (DBManager.class) {
                if (null == mDaoMaster) {
                    MyOpenHelper helper = new MyOpenHelper(context, DB_NAME, null);
                    mDaoMaster = new DaoMaster(helper.getWritableDatabase());
                }
            }
        }
        return mDaoMaster;
    }


    /**
     * 获取DaoSession
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context) {
        if (null == mDaoSession) {
            synchronized (DBManager.class) {
                mDaoSession = getDaoMaster(context).newSession();
            }
        }
        return mDaoSession;
    }
}