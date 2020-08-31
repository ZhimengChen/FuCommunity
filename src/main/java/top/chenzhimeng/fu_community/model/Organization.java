package top.chenzhimeng.fu_community.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties("handler")
public class Organization implements Serializable {
    private Integer organizationId;

    private String organizationName;

    private String logo;

    private String slogan;

    private String intro;

    private Integer founderId;

    private User founder;

    private String contact;

    private Integer auditorId;

    private Admin auditor;

    private Boolean hasCheck;

    private Date auditTime;

    private Boolean hasSubscribed;

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Integer getFounderId() {
        return founderId;
    }

    public void setFounderId(Integer founderId) {
        this.founderId = founderId;
    }

    public User getFounder() {
        return founder;
    }

    public void setFounder(User founder) {
        this.founder = founder;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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

    public Boolean getHasCheck() {
        return hasCheck;
    }

    public void setHasCheck(Boolean hasCheck) {
        this.hasCheck = hasCheck;
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

    @Override
    public String toString() {
        return "Organization{" +
                "organizationId=" + organizationId +
                ", organizationName='" + organizationName + '\'' +
                ", logo='" + logo + '\'' +
                ", slogan='" + slogan + '\'' +
                ", intro='" + intro + '\'' +
                ", founderId=" + founderId +
                ", founder=" + founder +
                ", contact='" + contact + '\'' +
                ", auditorId=" + auditorId +
                ", auditor=" + auditor +
                ", hasCheck=" + hasCheck +
                ", auditTime=" + auditTime +
                ", hasSubscribed=" + hasSubscribed +
                '}';
    }
}