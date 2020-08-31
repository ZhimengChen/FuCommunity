package top.chenzhimeng.fu_community.util;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import top.chenzhimeng.fu_community.config.AliyunSMS_Conf;
import top.chenzhimeng.fu_community.model.MessageTemplate;

@Slf4j
public class SMS_Util {

    private SMS_Util() {

    }

    private static final AliyunSMS_Conf ALIYUN_SMS_CONF = SpringUtil.getBean(AliyunSMS_Conf.class);

    private static final DefaultProfile PROFILE =
            DefaultProfile.getProfile("cn-hangzhou", ALIYUN_SMS_CONF.getAccessKeyId(), ALIYUN_SMS_CONF.getAccessSecret());

    private static final IAcsClient CLIENT = new DefaultAcsClient(PROFILE);

    /**
     * 发送短信验证码
     *
     * @param messageTemplate 短信模板
     * @param phoneNo         目标电话号码
     * @param code            验证码
     * @return true|false
     */
    public static Boolean send(MessageTemplate messageTemplate, String phoneNo, Integer code) throws ClientException {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNo);
        request.putQueryParameter("SignName", ALIYUN_SMS_CONF.getSignName());
        request.putQueryParameter("TemplateCode", messageTemplate.getTemplateCode());
        request.putQueryParameter("TemplateParam", "{'code':" + code + "}");

        CommonResponse response = CLIENT.getCommonResponse(request);
        String data = response.getData();
        log.info(data);
        String message = data.substring(data.indexOf("Message") + 10);

        return message.substring(0, message.indexOf('\"')).equals("OK");
    }
}
