package com.ph.teabar.database.dao.DaoImp;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.ph.teabar.database.dao.DBManager;
import com.ph.teabar.database.dao.DaoMaster;
import com.ph.teabar.database.dao.DaoSession;
import com.ph.teabar.database.dao.EqupmentDao;
import com.ph.teabar.database.dao.UserEntryDao;
import java.util.List;

import teabar.ph.com.teabar.pojo.Equpment;
import teabar.ph.com.teabar.pojo.UserEntry;


public class UserEntryImpl {
    private Context context;
    private SQLiteDatabase db;
    private DaoMaster master;
    private UserEntryDao userEntryDao;
    private DaoSession session;
    public UserEntryImpl(Context context) {
        this.context = context;
        db= DBManager.getInstance(context).getWritableDasebase();
        master=new DaoMaster(db);
        session=master.newSession();
        userEntryDao = session.getUserEntryDao();
    }

    /**
     * 添加信息
     * @param userEntry
     */
    public void insert(UserEntry userEntry){
        userEntryDao.insert(userEntry);
    }

    /**
     * 删除信息
     * @param userEntry
     */
    public void delete(UserEntry userEntry){
        userEntryDao.delete(userEntry);
    }

    /**
     * 更新信息
     * @param userEntry
     */
    public void update(UserEntry userEntry){
        userEntryDao.update(userEntry);
    }

    public UserEntry findById(long Id){
        return userEntryDao.load(Id);
    }
    public List<UserEntry> findAll(){
        return userEntryDao.loadAll();
    }

    public void  deleteAll(){
        userEntryDao.deleteAll();
    }

    /**
     * 根据macAddress来查询设备列表
     * @param UserNmae
     * @Param AppKey
     * @return
     */
    public  UserEntry findDeviceByUserApp(String UserNmae,String AppKey){
        return userEntryDao.queryBuilder().where(UserEntryDao.Properties.AppKey.eq(AppKey)).where(UserEntryDao.Properties.UserName.eq(UserNmae)).unique();
    }

    /**
     * 根据macAddress来查询设备
     * @param macAddress
     * @return
     */
//    public Equpment findDeviceByMacAddress2(String macAddress){
//        return equipmentDao.queryBuilder().where(EquipmentDao.Properties.DeviceMac.eq(macAddress)).unique();
//    }

    /**
     * 根据macAddress来查询分享设备
     * @param roleFlag
     * @return
     */
//    public List<Equipment> findDeviceByRoleFlag(int roleFlag){
//        return equipmentDao.queryBuilder().where(EquipmentDao.Properties.RoleFlag.eq(roleFlag)).list();
//    }

}
