package top.chenzhimeng.fu_community.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDateTime;

@JsonIgnoreProperties("handler")
public class News implements Serializable {
    private Integer newsId;

    private Integer publisherId;

    private User publisher;

    private Integer organizationId;

    private Organization organization;

    private String text;

    private String media;

    private LocalDateTime publishTime;

    private Boolean hasCheck;

    private Integer likesNum;

    private Boolean hasLiked;

    private Integer commentsNum;

    public Integer getNewsId() {
        return newsId;
    }

    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
    }

    public Integer getPublisherId() {
        return publisherId;
    }

    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public Boolean getHasCheck() {
        return hasCheck;
    }

    public void setHasCheck(Boolean hasCheck) {
        this.hasCheck = hasCheck;
    }

    public Integer getLikesNum() {
        return likesNum;
    }

    public void setLikesNum(Integer likesNum) {
        this.likesNum = likesNum;
    }

    public Boolean getHasLiked() {
        return hasLiked;
    }

    public void setHasLiked(Boolean hasLiked) {
        this.hasLiked = hasLiked;
    }

    public Integer getCommentsNum() {
        return commentsNum;
    }

    public void setCommentsNum(Integer commentsNum) {
        this.commentsNum = commentsNum;
    }

    @Override
    public String toString() {
        return "News{" +
                "newsId=" + newsId +
                ", publisherId=" + publisherId +
                ", publisher=" + publisher +
                ", organizationId=" + organizationId +
                ", organization=" + organization +
                ", text='" + text + '\'' +
                ", media='" + media + '\'' +
                ", publishTime=" + publishTime +
                ", hasCheck=" + hasCheck +
                ", likesNum=" + likesNum +
                ", hasLiked=" + hasLiked +
                ", commentsNum=" + commentsNum +
                '}';
    }
}