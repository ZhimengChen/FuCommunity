package top.chenzhimeng.fu_community.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("aliyun.sms.template-code")
public class TemplateCode {
    private String register;
    private String login;
    private String verify;

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }
}
