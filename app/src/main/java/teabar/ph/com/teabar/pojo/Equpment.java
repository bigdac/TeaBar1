package teabar.ph.com.teabar.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

@Entity
public class Equpment implements Serializable {
    @Id(autoincrement = false)
    long equpmentId;
    String name;
    boolean isFirst;//是否是默认设备
    String macAdress;
    int mStage;//机器状态
    String lightColor;//灯光颜色
    String washTime;//清洗周期
    String hasWater;//水量
    boolean inform_isFinish;// 通知冲泡完成
    boolean inform_isHot;// 通知预热完成
    boolean inform_noWater;// 通知水量不足
    boolean inform_isFull;// 通知废料已满
    boolean inform_isWashing;// 通知清洗周期
    String errorCode;
    int Mode;//燈光模式
    boolean onLine;//是否在线
    int lightOpen ;//燈光開關
    int bringht;
    int hotFinish;//是否预热完成
    public int getHotFinish() {
        return this.hotFinish;
    }
    public void setHotFinish(int hotFinish) {
        this.hotFinish = hotFinish;
    }
    public int getBringht() {
        return this.bringht;
    }
    public void setBringht(int bringht) {
        this.bringht = bringht;
    }
    public int getLightOpen() {
        return this.lightOpen;
    }
    public void setLightOpen(int lightOpen) {
        this.lightOpen = lightOpen;
    }
    public boolean getOnLine() {
        return this.onLine;
    }
    public void setOnLine(boolean onLine) {
        this.onLine = onLine;
    }
    public int getMode() {
        return this.Mode;
    }
    public void setMode(int Mode) {
        this.Mode = Mode;
    }
    public String getErrorCode() {
        return this.errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public boolean getInform_isWashing() {
        return this.inform_isWashing;
    }
    public void setInform_isWashing(boolean inform_isWashing) {
        this.inform_isWashing = inform_isWashing;
    }
    public boolean getInform_isFull() {
        return this.inform_isFull;
    }
    public void setInform_isFull(boolean inform_isFull) {
        this.inform_isFull = inform_isFull;
    }
    public boolean getInform_noWater() {
        return this.inform_noWater;
    }
    public void setInform_noWater(boolean inform_noWater) {
        this.inform_noWater = inform_noWater;
    }
    public boolean getInform_isHot() {
        return this.inform_isHot;
    }
    public void setInform_isHot(boolean inform_isHot) {
        this.inform_isHot = inform_isHot;
    }
    public boolean getInform_isFinish() {
        return this.inform_isFinish;
    }
    public void setInform_isFinish(boolean inform_isFinish) {
        this.inform_isFinish = inform_isFinish;
    }
    public String getHasWater() {
        return this.hasWater;
    }
    public void setHasWater(String hasWater) {
        this.hasWater = hasWater;
    }
    public String getWashTime() {
        return this.washTime;
    }
    public void setWashTime(String washTime) {
        this.washTime = washTime;
    }
    public String getLightColor() {
        return this.lightColor;
    }
    public void setLightColor(String lightColor) {
        this.lightColor = lightColor;
    }
    public int getMStage() {
        return this.mStage;
    }
    public void setMStage(int mStage) {
        this.mStage = mStage;
    }
    public String getMacAdress() {
        return this.macAdress;
    }
    public void setMacAdress(String macAdress) {
        this.macAdress = macAdress;
    }
    public boolean getIsFirst() {
        return this.isFirst;
    }
    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
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
    @Generated(hash = 303452426)
    public Equpment(long equpmentId, String name, boolean isFirst,
            String macAdress, int mStage, String lightColor, String washTime,
            String hasWater, boolean inform_isFinish, boolean inform_isHot,
            boolean inform_noWater, boolean inform_isFull,
            boolean inform_isWashing, String errorCode, int Mode, boolean onLine,
            int lightOpen, int bringht, int hotFinish) {
        this.equpmentId = equpmentId;
        this.name = name;
        this.isFirst = isFirst;
        this.macAdress = macAdress;
        this.mStage = mStage;
        this.lightColor = lightColor;
        this.washTime = washTime;
        this.hasWater = hasWater;
        this.inform_isFinish = inform_isFinish;
        this.inform_isHot = inform_isHot;
        this.inform_noWater = inform_noWater;
        this.inform_isFull = inform_isFull;
        this.inform_isWashing = inform_isWashing;
        this.errorCode = errorCode;
        this.Mode = Mode;
        this.onLine = onLine;
        this.lightOpen = lightOpen;
        this.bringht = bringht;
        this.hotFinish = hotFinish;
    }
    @Generated(hash = 1019008802)
    public Equpment() {
    }



}
