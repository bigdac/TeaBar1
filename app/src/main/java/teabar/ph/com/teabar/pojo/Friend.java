package teabar.ph.com.teabar.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;


@Entity
public class Friend {
    @Id(autoincrement = false)
    long id;
    String userName;
    String photoUrl ;
    @Transient
    UserEntry userEntry ;
    public String getPhotoUrl() {
        return this.photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    @Generated(hash = 714665598)
    public Friend(long id, String userName, String photoUrl) {
        this.id = id;
        this.userName = userName;
        this.photoUrl = photoUrl;
    }
    @Generated(hash = 287143722)
    public Friend() {
    }
  
 


}
