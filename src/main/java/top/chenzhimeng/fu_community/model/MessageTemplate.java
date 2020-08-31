package top.chenzhimeng.fu_community.model;

import top.chenzhimeng.fu_community.config.AliyunSMS_Conf;
import top.chenzhimeng.fu_community.util.SpringUtil;

import java.io.Serializable;

public enum MessageTemplate implements Serializable {

    REGISTER, LOGIN, VERIFY;

    private AliyunSMS_Conf aliyunSMS_conf = SpringUtil.getBean(AliyunSMS_Conf.class);

    public String getTemplateCode() {
        String templateCode = null;
        switch (this) {
            case REGISTER:
                templateCode = aliyunSMS_conf.getTemplateCode().getRegister();
                break;
            case LOGIN:
                templateCode = aliyunSMS_conf.getTemplateCode().getLogin();
                break;
            case VERIFY:
                templateCode = aliyunSMS_conf.getTemplateCode().getVerify();
        }
        return templateCode;
    }
}
