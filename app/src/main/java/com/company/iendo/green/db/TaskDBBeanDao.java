package com.company.iendo.green.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TASK_DBBEAN".
*/
public class TaskDBBeanDao extends AbstractDao<TaskDBBean, Long> {

    public static final String TABLENAME = "TASK_DBBEAN";

    /**
     * Properties of entity TaskDBBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DownStatue = new Property(1, String.class, "downStatue", false, "DOWN_STATUE");
        public final static Property DownStatueDes = new Property(2, String.class, "downStatueDes", false, "DOWN_STATUE_DES");
        public final static Property CommonCode = new Property(3, String.class, "commonCode", false, "COMMON_CODE");
        public final static Property SingleCode = new Property(4, String.class, "singleCode", false, "SINGLE_CODE");
        public final static Property TaskString = new Property(5, String.class, "taskString", false, "TASK_STRING");
    }


    public TaskDBBeanDao(DaoConfig config) {
        super(config);
    }
    
    public TaskDBBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TASK_DBBEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"DOWN_STATUE\" TEXT," + // 1: downStatue
                "\"DOWN_STATUE_DES\" TEXT," + // 2: downStatueDes
                "\"COMMON_CODE\" TEXT," + // 3: commonCode
                "\"SINGLE_CODE\" TEXT," + // 4: singleCode
                "\"TASK_STRING\" TEXT);"); // 5: taskString
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TASK_DBBEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TaskDBBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String downStatue = entity.getDownStatue();
        if (downStatue != null) {
            stmt.bindString(2, downStatue);
        }
 
        String downStatueDes = entity.getDownStatueDes();
        if (downStatueDes != null) {
            stmt.bindString(3, downStatueDes);
        }
 
        String commonCode = entity.getCommonCode();
        if (commonCode != null) {
            stmt.bindString(4, commonCode);
        }
 
        String singleCode = entity.getSingleCode();
        if (singleCode != null) {
            stmt.bindString(5, singleCode);
        }
 
        String taskString = entity.getTaskString();
        if (taskString != null) {
            stmt.bindString(6, taskString);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TaskDBBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String downStatue = entity.getDownStatue();
        if (downStatue != null) {
            stmt.bindString(2, downStatue);
        }
 
        String downStatueDes = entity.getDownStatueDes();
        if (downStatueDes != null) {
            stmt.bindString(3, downStatueDes);
        }
 
        String commonCode = entity.getCommonCode();
        if (commonCode != null) {
            stmt.bindString(4, commonCode);
        }
 
        String singleCode = entity.getSingleCode();
        if (singleCode != null) {
            stmt.bindString(5, singleCode);
        }
 
        String taskString = entity.getTaskString();
        if (taskString != null) {
            stmt.bindString(6, taskString);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TaskDBBean readEntity(Cursor cursor, int offset) {
        TaskDBBean entity = new TaskDBBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // downStatue
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // downStatueDes
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // commonCode
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // singleCode
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // taskString
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TaskDBBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDownStatue(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDownStatueDes(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCommonCode(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSingleCode(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setTaskString(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TaskDBBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TaskDBBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TaskDBBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
