package teabar.ph.com.teabar.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class FriendInfor {
    @Id(autoincrement = false)
    long id;
    String useName;
    int addNum;
    String appKey;
    public String getAppKey() {
        return this.appKey;
    }
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
    public int getAddNum() {
        return this.addNum;
    }
    public void setAddNum(int addNum) {
        this.addNum = addNum;
    }
    public String getUseName() {
        return this.useName;
    }
    public void setUseName(String useName) {
        this.useName = useName;
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    @Generated(hash = 5776347)
    public FriendInfor(long id, String useName, int addNum, String appKey) {
        this.id = id;
        this.useName = useName;
        this.addNum = addNum;
        this.appKey = appKey;
    }
    @Generated(hash = 1592264104)
    public FriendInfor() {
    }
   
    

}
