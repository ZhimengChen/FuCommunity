package top.chenzhimeng.fu_community.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties("handler")
public class User implements Serializable {
    private Integer userId;

    private String phoneNo;

    private String avatar;

    private String secretKey;

    private String password;

    private Integer studentNo;

    private String studentName;

    private String studentCard;

    private Boolean hasCheck;

    private Integer auditorId;

    private Admin auditor;

    private Date auditTime;

    private Boolean hasSubscribed;

    private List<Organization> organizations;

    private List<News> newsList;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(Integer studentNo) {
        this.studentNo = studentNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentCard() {
        return studentCard;
    }

    public void setStudentCard(String studentCard) {
        this.studentCard = studentCard;
    }

    public Boolean getHasCheck() {
        return hasCheck;
    }

    public void setHasCheck(Boolean hasCheck) {
        this.hasCheck = hasCheck;
    }

    public Integer getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Integer auditorId) {
        this.auditorId = auditorId;
    }

    public Admin getAuditor() {
        return auditor;
    }

    public void setAuditor(Admin auditor) {
        this.auditor = auditor;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Boolean getHasSubscribed() {
        return hasSubscribed;
    }

    public void setHasSubscribed(Boolean hasSubscribed) {
        this.hasSubscribed = hasSubscribed;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }

    public List<News> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", phoneNo='" + phoneNo + '\'' +
                ", avatar='" + avatar + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", password='" + password + '\'' +
                ", studentNo=" + studentNo +
                ", studentName='" + studentName + '\'' +
                ", studentCard='" + studentCard + '\'' +
                ", hasCheck=" + hasCheck +
                ", auditorId=" + auditorId +
                ", auditor=" + auditor +
                ", auditTime=" + auditTime +
                ", hasSubscribed=" + hasSubscribed +
                ", organizations=" + organizations +
                ", newsList=" + newsList +
                '}';
    }
}