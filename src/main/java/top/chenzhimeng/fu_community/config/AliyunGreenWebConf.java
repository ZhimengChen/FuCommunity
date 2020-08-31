package top.chenzhimeng.fu_community.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("aliyun.green-web")
public class AliyunGreenWebConf {

    private String accessKeyId;
    private String accessSecret;
    private String regionId;
    private String accountId;
    private String seed;

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

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }
}
