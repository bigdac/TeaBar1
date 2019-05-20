package teabar.ph.com.teabar.pojo;

import com.google.android.gms.common.util.DataUtils;

import java.io.Serializable;

import teabar.ph.com.teabar.util.DatasUtil;

public class ScoreRecords implements Serializable {
    String grades;
    int num;
    String createTime;
    String teaName1;
    String teaName2;
    String teaName3;
    String bodyGrades;
    String lifeGrades;
    String mindGrades;
    String nutritionGrades;
    String teaName1En;
    String teaName2En;
    String teaName3En;
    String productName1En;
    String productName2En;
    String productName3En;
    String productName1Cn;
    String productName2Cn;
    String productName3Cn;

    public String getTeaName2En() {
        return teaName2En;
    }

    public void setTeaName2En(String teaName2En) {
        this.teaName2En = teaName2En;
    }

    public String getTeaName3En() {
        return teaName3En;
    }

    public void setTeaName3En(String teaName3En) {
        this.teaName3En = teaName3En;
    }

    public String getProductName1En() {
        return productName1En;
    }

    public void setProductName1En(String productName1En) {
        this.productName1En = productName1En;
    }

    public String getProductName2En() {
        return productName2En;
    }

    public void setProductName2En(String productName2En) {
        this.productName2En = productName2En;
    }

    public String getProductName3En() {
        return productName3En;
    }

    public void setProductName3En(String productName3En) {
        this.productName3En = productName3En;
    }

    public String getProductName1Cn() {
        return productName1Cn;
    }

    public void setProductName1Cn(String productName1Cn) {
        this.productName1Cn = productName1Cn;
    }

    public String getProductName2Cn() {
        return productName2Cn;
    }

    public void setProductName2Cn(String productName2Cn) {
        this.productName2Cn = productName2Cn;
    }

    public String getProductName3Cn() {
        return productName3Cn;
    }

    public void setProductName3Cn(String productName3Cn) {
        this.productName3Cn = productName3Cn;
    }

    public String getTeaName1En() {
        return teaName1En;
    }

    public void setTeaName1En(String teaName1En) {
        this.teaName1En = teaName1En;
    }


    public String getGrades() {
        return grades;

    }

    public void setGrades(String grades) {
        this.grades = grades;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTeaName1() {
        return teaName1;
    }

    public void setTeaName1(String teaName1) {
        this.teaName1 = teaName1;
    }

    public String getTeaName2() {
        return teaName2;
    }

    public void setTeaName2(String teaName2) {
        this.teaName2 = teaName2;
    }

    public String getTeaName3() {
        return teaName3;
    }

    public void setTeaName3(String teaName3) {
        this.teaName3 = teaName3;
    }

    public String getBodyGrades() {
        return bodyGrades;
    }

    public void setBodyGrades(String bodyGrades) {
        this.bodyGrades = bodyGrades;
    }

    public String getLifeGrades() {
        return lifeGrades;
    }

    public void setLifeGrades(String lifeGrades) {
        this.lifeGrades = lifeGrades;
    }

    public String getMindGrades() {
        return mindGrades;
    }

    public void setMindGrades(String mindGrades) {
        this.mindGrades = mindGrades;
    }

    public String getNutritionGrades() {
        return nutritionGrades;
    }

    public void setNutritionGrades(String nutritionGrades) {
        this.nutritionGrades = nutritionGrades;
    }
}
