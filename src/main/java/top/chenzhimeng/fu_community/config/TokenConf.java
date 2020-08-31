package top.chenzhimeng.fu_community.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * token配置类
 */
@ConfigurationProperties("token")
public class TokenConf {
    private String secretKey;
    @Autowired
    private ValidityPeriod validityPeriod;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public ValidityPeriod getValidityPeriod() {
        return validityPeriod;
    }
}

