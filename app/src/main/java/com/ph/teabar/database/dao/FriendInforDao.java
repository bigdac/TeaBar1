package com.ph.teabar.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import teabar.ph.com.teabar.pojo.FriendInfor;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "FRIEND_INFOR".
*/
public class FriendInforDao extends AbstractDao<FriendInfor, Long> {

    public static final String TABLENAME = "FRIEND_INFOR";

    /**
     * Properties of entity FriendInfor.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property UseName = new Property(1, String.class, "useName", false, "USE_NAME");
        public final static Property AddNum = new Property(2, int.class, "addNum", false, "ADD_NUM");
        public final static Property AppKey = new Property(3, String.class, "appKey", false, "APP_KEY");
        public final static Property AddFriend = new Property(4, boolean.class, "addFriend", false, "ADD_FRIEND");
    }


    public FriendInforDao(DaoConfig config) {
        super(config);
    }
    
    public FriendInforDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"FRIEND_INFOR\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: id
                "\"USE_NAME\" TEXT," + // 1: useName
                "\"ADD_NUM\" INTEGER NOT NULL ," + // 2: addNum
                "\"APP_KEY\" TEXT," + // 3: appKey
                "\"ADD_FRIEND\" INTEGER NOT NULL );"); // 4: addFriend
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"FRIEND_INFOR\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, FriendInfor entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        String useName = entity.getUseName();
        if (useName != null) {
            stmt.bindString(2, useName);
        }
        stmt.bindLong(3, entity.getAddNum());
 
        String appKey = entity.getAppKey();
        if (appKey != null) {
            stmt.bindString(4, appKey);
        }
        stmt.bindLong(5, entity.getAddFriend() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, FriendInfor entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        String useName = entity.getUseName();
        if (useName != null) {
            stmt.bindString(2, useName);
        }
        stmt.bindLong(3, entity.getAddNum());
 
        String appKey = entity.getAppKey();
        if (appKey != null) {
            stmt.bindString(4, appKey);
        }
        stmt.bindLong(5, entity.getAddFriend() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public FriendInfor readEntity(Cursor cursor, int offset) {
        FriendInfor entity = new FriendInfor( //
            cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // useName
            cursor.getInt(offset + 2), // addNum
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // appKey
            cursor.getShort(offset + 4) != 0 // addFriend
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, FriendInfor entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setUseName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAddNum(cursor.getInt(offset + 2));
        entity.setAppKey(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAddFriend(cursor.getShort(offset + 4) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(FriendInfor entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(FriendInfor entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(FriendInfor entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
