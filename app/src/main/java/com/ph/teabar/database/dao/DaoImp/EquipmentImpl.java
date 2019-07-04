package com.ph.teabar.database.dao.DaoImp;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import com.ph.teabar.database.dao.DBManager;
import com.ph.teabar.database.dao.DaoMaster;
import com.ph.teabar.database.dao.DaoSession;
import com.ph.teabar.database.dao.EqupmentDao;

import org.greenrobot.greendao.identityscope.IdentityScopeType;

import java.util.List;

import teabar.ph.com.teabar.pojo.Equpment;


public class EquipmentImpl {
    private Context context;
    private SQLiteDatabase db;
    private DaoMaster master;
    private EqupmentDao equipmentDao;
    private DaoSession session;
    public EquipmentImpl(Context context) {
        this.context = context;
        db= DBManager.getInstance(context).getWritableDasebase();
        master=new DaoMaster(db);
        session=master.newSession(IdentityScopeType.None);
        equipmentDao = session.getEqupmentDao();
    }

    /**
     * 添加信息
     * @param equipment
     */
    public void insert(Equpment equipment){
        equipmentDao.insert(equipment);
    }

    /**
     * 删除信息
     * @param equipment
     */
    public void delete(Equpment equipment){
        equipmentDao.delete(equipment);
    }

    /**
     * 更新信息
     * @param equipment
     */
    public void update(Equpment equipment){
        equipmentDao.update(equipment);
    }

    public Equpment findById(long Id){
        return equipmentDao.load(Id);
    }
    public List<Equpment> findAll(){
        return equipmentDao.loadAll();
    }

    public void  deleteAll(){
        equipmentDao.deleteAll();
    }

    /**
     * 根据macAddress来查询设备列表
     * @param macAddress
     * @return
     */
    public List<Equpment> findDeviceByMacAddress(String macAddress){
        return equipmentDao.queryBuilder().where(EqupmentDao.Properties.MacAdress.eq(macAddress)).list();
    }

    /**
     * 根据macAddress来查询设备
     * @param macAddress
     * @return
     */
    public Equpment findDeviceByMacAddress2(String macAddress){
        return equipmentDao.queryBuilder().where(EqupmentDao.Properties.MacAdress.eq(macAddress)).unique();
    }

    /**
     * 根据macAddress来查询分享设备
     * @param roleFlag
     * @return
     */
//    public List<Equipment> findDeviceByRoleFlag(int roleFlag){
//        return equipmentDao.queryBuilder().where(EquipmentDao.Properties.RoleFlag.eq(roleFlag)).list();
//    }

}
