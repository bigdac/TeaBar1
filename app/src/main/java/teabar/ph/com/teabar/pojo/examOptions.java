package teabar.ph.com.teabar.pojo;

public class examOptions {
        long id ;
        int examId;
        String optionNum;
        String optionLanguage;
        String  optionTxt  ;
        String    unique1;
        String  grade;
        String type1;//标题
        String kindId;
        String statusId;
        String multiple;//是否多选
        String selectnum;//多选个数
        String examNum; //考试题数
        boolean Isselect;

    public boolean isIsselect() {
        return Isselect;
    }

    public void setIsselect(boolean isselect) {
        Isselect = isselect;
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

    public String getExamNum() {
        return examNum;
    }

    public void setExamNum(String examNum) {
        this.examNum = examNum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getOptionNum() {
        return optionNum;
    }

    public void setOptionNum(String optionNum) {
        this.optionNum = optionNum;
    }

    public String getOptionLanguage() {
        return optionLanguage;
    }

    public void setOptionLanguage(String optionLanguage) {
        this.optionLanguage = optionLanguage;
    }

    public String getOptionTxt() {
        return optionTxt;
    }

    public void setOptionTxt(String optionTxt) {
        this.optionTxt = optionTxt;
    }

    public String getUnique1() {
        return unique1;
    }

    public void setUnique1(String unique1) {
        this.unique1 = unique1;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getKindId() {
        return kindId;
    }

    public void setKindId(String kindId) {
        this.kindId = kindId;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }
}
