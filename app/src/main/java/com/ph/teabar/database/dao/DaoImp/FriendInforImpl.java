package com.ph.teabar.database.dao.DaoImp;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.ph.teabar.database.dao.DBManager;
import com.ph.teabar.database.dao.DaoMaster;
import com.ph.teabar.database.dao.DaoSession;
import com.ph.teabar.database.dao.FriendInforDao;
import java.util.List;
import teabar.ph.com.teabar.pojo.FriendInfor;


public class FriendInforImpl {
    private Context context;
    private SQLiteDatabase db;
    private DaoMaster master;
    private FriendInforDao friendInforDao;
    private DaoSession session;
    public FriendInforImpl(Context context) {
        this.context = context;
        db= DBManager.getInstance(context).getWritableDasebase();
        master=new DaoMaster(db);
        session=master.newSession();
        friendInforDao = session.getFriendInforDao();
    }

    /**
     * 添加信息
     * @param friend
     */
    public void insert(FriendInfor friend){
        friendInforDao.insert(friend);
    }

    /**
     * 删除信息
     * @param friend
     */
    public void delete(FriendInfor friend){
        friendInforDao.delete(friend);
    }

    /**
     * 更新信息
     * @param friend
     */
    public void update(FriendInfor friend){
        friendInforDao.update(friend);
    }

    public FriendInfor findById(long Id){
        return friendInforDao.load(Id);
    }
    public List<FriendInfor> findAll(){
        return friendInforDao.loadAll();
    }

    public void  deleteAll(){
        friendInforDao.deleteAll();
    }

    /**
     * 根据macAddress来查询设备列表
     * @param UserNmae
     * @Param AppKey
     * @return
     */
//    public  Friend findDeviceByUserApp(String UserNmae,String AppKey){
//        return friendDao.queryBuilder().where(FriendDao.Properties.AppKey.eq(AppKey)).where(FriendDao.Properties.UserName.eq(UserNmae)).unique();
//    }

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
