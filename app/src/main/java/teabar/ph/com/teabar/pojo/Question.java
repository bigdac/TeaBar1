package teabar.ph.com.teabar.pojo;

import java.util.List;

public class Question {
    long id;
    String examTitle;
    int examNum;
    String   type1;
    String multiple;
    String selectnum;
    int  examLanguage;
    int  type ;
    List<examOptions> examOptions;

    public int getExamLanguage() {
        return examLanguage;
    }

    public void setExamLanguage(int examLanguage) {
        this.examLanguage = examLanguage;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<teabar.ph.com.teabar.pojo.examOptions> getExamOptions() {
        return examOptions;
    }

    public void setExamOptions(List<teabar.ph.com.teabar.pojo.examOptions> examOptions) {
        this.examOptions = examOptions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public int getExamNum() {
        return examNum;
    }

    public void setExamNum(int examNum) {
        this.examNum = examNum;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getMultiple() {
        return multiple;
    }

    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    public String getSelectnum() {
        return selectnum;
    }

    public void setSelectnum(String selectnum) {
        this.selectnum = selectnum;
    }
}
