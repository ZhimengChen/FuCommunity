package top.chenzhimeng.fu_community.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云短信服务的配置类
 */
@ConfigurationProperties("aliyun.sms")
public class AliyunSMS_Conf {

    private String accessKeyId;
    private String accessSecret;
    private String signName;
    @Autowired
    private TemplateCode templateCode;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public TemplateCode getTemplateCode() {
        return templateCode;
    }
}
