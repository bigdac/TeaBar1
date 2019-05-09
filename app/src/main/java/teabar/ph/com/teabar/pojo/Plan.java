package teabar.ph.com.teabar.pojo;

import java.io.Serializable;

public class Plan implements Serializable {
    long id ;
    String   planNameCn;
    int  planTime;
    String    planPhoto;
    String dietitianCn;
    String dietitianEn;
    String price ;
    String dietitianPhoto;
    String describeCn;
    String describeEn;
    String planNameEn;
    String featuresCn;
    String featuresEn;
    String aboutCn ;
    String aboutEn;
    String dietitianDescribeCn;
    String dietitianDescribeEn;

    public String getDescribeCn() {
        return describeCn;
    }

    public void setDescribeCn(String describeCn) {
        this.describeCn = describeCn;
    }

    public String getDescribeEn() {
        return describeEn;
    }

    public void setDescribeEn(String describeEn) {
        this.describeEn = describeEn;
    }

    public String getPlanNameEn() {
        return planNameEn;
    }

    public void setPlanNameEn(String planNameEn) {
        this.planNameEn = planNameEn;
    }

    public String getFeaturesCn() {
        return featuresCn;
    }

    public void setFeaturesCn(String featuresCn) {
        this.featuresCn = featuresCn;
    }

    public String getFeaturesEn() {
        return featuresEn;
    }

    public void setFeaturesEn(String featuresEn) {
        this.featuresEn = featuresEn;
    }

    public String getAboutCn() {
        return aboutCn;
    }

    public void setAboutCn(String aboutCn) {
        this.aboutCn = aboutCn;
    }

    public String getAboutEn() {
        return aboutEn;
    }

    public void setAboutEn(String aboutEn) {
        this.aboutEn = aboutEn;
    }

    public String getDietitianDescribeCn() {
        return dietitianDescribeCn;
    }

    public void setDietitianDescribeCn(String dietitianDescribeCn) {
        this.dietitianDescribeCn = dietitianDescribeCn;
    }

    public String getDietitianDescribeEn() {
        return dietitianDescribeEn;
    }

    public void setDietitianDescribeEn(String dietitianDescribeEn) {
        this.dietitianDescribeEn = dietitianDescribeEn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlanNameCn() {
        return planNameCn;
    }

    public void setPlanNameCn(String planNameCn) {
        this.planNameCn = planNameCn;
    }

    public int getPlanTime() {
        return planTime;
    }

    public void setPlanTime(int planTime) {
        this.planTime = planTime;
    }

    public String getPlanPhoto() {
        return planPhoto;
    }

    public void setPlanPhoto(String planPhoto) {
        this.planPhoto = planPhoto;
    }

    public String getDietitianCn() {
        return dietitianCn;
    }

    public void setDietitianCn(String dietitianCn) {
        this.dietitianCn = dietitianCn;
    }

    public String getDietitianEn() {
        return dietitianEn;
    }

    public void setDietitianEn(String dietitianEn) {
        this.dietitianEn = dietitianEn;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDietitianPhoto() {
        return dietitianPhoto;
    }

    public void setDietitianPhoto(String dietitianPhoto) {
        this.dietitianPhoto = dietitianPhoto;
    }
}
