package teabar.ph.com.teabar.pojo;

import java.io.Serializable;

public class News implements Serializable {

    long id;
    String  titile;
    String  related;
    String  createTime;
    String  type;
    String  newsPicture;
    String  content;
    String  newsPictureFile;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitile() {
        return titile;
    }

    public void setTitile(String titile) {
        this.titile = titile;
    }

    public String getRelated() {
        return related;
    }

    public void setRelated(String related) {
        this.related = related;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNewsPicture() {
        return newsPicture;
    }

    public void setNewsPicture(String newsPicture) {
        this.newsPicture = newsPicture;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNewsPictureFile() {
        return newsPictureFile;
    }

    public void setNewsPictureFile(String newsPictureFile) {
        this.newsPictureFile = newsPictureFile;
    }
}
