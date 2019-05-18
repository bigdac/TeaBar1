package teabar.ph.com.teabar.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class UserEntry {
    @Id(autoincrement = false)
    private  long id ;
    private String userId;
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
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    @Generated(hash = 279504359)
    public UserEntry(long id, String userId, String userName, String appKey) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.appKey = appKey;
    }
    @Generated(hash = 1412082065)
    public UserEntry() {
    }

}
