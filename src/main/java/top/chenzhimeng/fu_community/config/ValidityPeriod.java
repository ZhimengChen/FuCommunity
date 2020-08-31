package top.chenzhimeng.fu_community.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("token.validity-period")
public class ValidityPeriod {
    private Long access;
    private Long refresh;

    public Long getAccess() {
        return access;
    }

    public void setAccess(Long access) {
        this.access = access;
    }

    public Long getRefresh() {
        return refresh;
    }

    public void setRefresh(Long refresh) {
        this.refresh = refresh;
    }
}
