package teabar.ph.com.teabar.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class UserEntry {
    @Id(autoincrement = false)
    private long userId;
    public String userName;
    public String appKey;
    public String getAppKey() {
        return this.appKey;
    }
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public long getUserId() {
        return this.userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    @Generated(hash = 626996541)
    public UserEntry(long userId, String userName, String appKey) {
        this.userId = userId;
        this.userName = userName;
        this.appKey = appKey;
    }
    @Generated(hash = 1412082065)
    public UserEntry() {
    }
    
}
