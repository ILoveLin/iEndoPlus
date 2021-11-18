package com.company.iendo.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.company.iendo.green.db.DaoMaster;

import org.greenrobot.greendao.database.Database;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/17 11:28
 * desc：
 */
public class MyOpenHelper extends DaoMaster.OpenHelper {
    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    /**
     * 数据库升级
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //操作数据库的更新 有几个表升级都可以传入到下面
        //todo- 添加需要升级的数据库
//        MigrationHelper.getInstance().migrate(db, MyBeanDao.class);


    }
}