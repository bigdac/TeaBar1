package teabar.ph.com.teabar.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Equpment {
    @Id(autoincrement = false)
    long equpmentId;
    String name;

    @Generated(hash = 1826395938)
    public Equpment(long equpmentId, String name) {
        this.equpmentId = equpmentId;
        this.name = name;
    }

    @Generated(hash = 1019008802)
    public Equpment() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getEqupmentId() {
        return this.equpmentId;
    }

    public void setEqupmentId(long equpmentId) {
        this.equpmentId = equpmentId;
    }

  

}
