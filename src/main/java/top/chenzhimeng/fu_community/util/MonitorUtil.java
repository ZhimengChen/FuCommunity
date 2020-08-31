package top.chenzhimeng.fu_community.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.green.model.v20180509.ImageAsyncScanRequest;
import com.aliyuncs.green.model.v20180509.TextScanRequest;
import com.aliyuncs.green.model.v20180509.VideoAsyncScanRequest;
import com.aliyuncs.http.*;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.bytecode.ByteArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.chenzhimeng.fu_community.config.AliyunGreenWebConf;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
@Component
public class MonitorUtil {

    private MonitorUtil() {

    }

    private static IAcsClient acsClient;

    private static AliyunGreenWebConf aliyunGreenWebConf;

    /**
     * 初始化该静态类的两个静态变量
     *
     * @param aliyunGreenWebConf 用于初始化的配置类
     */
    @Autowired
    public void init(AliyunGreenWebConf aliyunGreenWebConf) {
        MonitorUtil.aliyunGreenWebConf = aliyunGreenWebConf;
        DefaultProfile profile = DefaultProfile.getProfile(aliyunGreenWebConf.getRegionId(),
                aliyunGreenWebConf.getAccessKeyId(), aliyunGreenWebConf.getAccessSecret());
        DefaultProfile.addEndpoint(aliyunGreenWebConf.getRegionId(), aliyunGreenWebConf.getRegionId(),
                "green" + aliyunGreenWebConf.getRegionId() + ".aliyuncs.com");
        MonitorUtil.acsClient = new DefaultAcsClient(profile);
    }

    /**
     * 设置共同请求头
     *
     * @param httpRequest 待设置的请求
     * @param <T>         AcsResponse 的子类
     */
    public static <T extends AcsResponse> void setHeader(RoaAcsRequest<T> httpRequest) {
        httpRequest.setSysAcceptFormat(FormatType.JSON);    //指定API返回格式。
        httpRequest.setSysMethod(MethodType.POST);  //指定请求方法。
        httpRequest.setSysEncoding("UTF-8");
    }

    /**
     * 检测文字是否违规
     *
     * @param text 待检测文字
     * @return true|false
     */
    public static boolean textIsSpam(String text) throws ClientException {
        TextScanRequest textScanRequest = new TextScanRequest();
        textScanRequest.setHttpContentType(FormatType.JSON);
        textScanRequest.setSysRegionId(aliyunGreenWebConf.getRegionId());
        Map<String, Object> task = new LinkedHashMap<>();
        task.put("dataId", UUID.randomUUID().toString());

        //待检测的文本，长度不超过10000个字符。
        task.put("content", text);
        JSONObject data = new JSONObject();

        //检测场景。文本垃圾检测请传递antispam。
        data.put("scenes", "antispam");
        data.put("tasks", task);
        log.info("data: {}", JSON.toJSONString(data, true));
        textScanRequest.setHttpContent(data.toJSONString().getBytes(StandardCharsets.UTF_8), "UTF-8", FormatType.JSON);
        // 请务必设置超时时间。
        textScanRequest.setSysConnectTimeout(3000);
        textScanRequest.setSysReadTimeout(6000);

        HttpResponse httpResponse = acsClient.doAction(textScanRequest);
        JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), StandardCharsets.UTF_8));
        return textAnalyze(scrResponse);
    }

    /**
     * 使用回调函数的方式检测图片是否违规
     *
     * @param url 待检测的图片地址
     */
    public static void imgScan(String url) throws ClientException {
        ImageAsyncScanRequest imageAsyncScanRequest = new ImageAsyncScanRequest();
        //支持HTTP和HTTPS。
        imageAsyncScanRequest.setSysProtocol(ProtocolType.HTTPS);

        JSONObject httpBody = new JSONObject();

        httpBody.put("scenes", Arrays.asList("porn", "terrorism", "ad"));
        httpBody.put("callback", "http://www.chenzhimeng.top:10001/news/callback");
        httpBody.put("seed", aliyunGreenWebConf.getSeed());

        JSONObject task = new JSONObject();
        task.put("dataId", UUID.randomUUID().toString());

        //设置图片链接。
        task.put("url", url);
        task.put("time", new Date());
        httpBody.put("tasks", task);

        imageAsyncScanRequest.setHttpContent(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(httpBody.toJSONString()),
                "UTF-8", FormatType.JSON);

        /*
         * 设置超时时间。服务端全链路处理超时时间为10秒，请做相应设置。
         * 如果设置的ReadTimeout小于服务端处理的时间，程序中会获得一个read timeout异常。
         */
        imageAsyncScanRequest.setSysConnectTimeout(3000);
        imageAsyncScanRequest.setSysReadTimeout(10000);
        acsClient.doAction(imageAsyncScanRequest);
    }

    /**
     * 使用回调函数的方式检测视频是否违规
     *
     * @param url 待检测的视频路径
     */
    public static void videoScan(String url) throws UnsupportedEncodingException, ClientException {
        VideoAsyncScanRequest videoAsyncScanRequest = new VideoAsyncScanRequest();

        Map<String, Object> task = new LinkedHashMap<String, Object>();
        task.put("dataId", UUID.randomUUID().toString());
        task.put("url", url);

        JSONObject data = new JSONObject();
        data.put("scenes", Arrays.asList("porn", "terrorism"));
        data.put("tasks", task);
        data.put("callback", "http://www.chenzhimeng.top:10001/news/callback");
        data.put("seed", aliyunGreenWebConf.getSeed());

        videoAsyncScanRequest.setHttpContent(data.toJSONString().getBytes(StandardCharsets.UTF_8), "UTF-8", FormatType.JSON);

        /*
         * 请务必设置超时时间。
         */
        videoAsyncScanRequest.setSysConnectTimeout(3000);
        videoAsyncScanRequest.setSysReadTimeout(300000);

        acsClient.doAction(videoAsyncScanRequest);
    }

    /**
     * 分析图片视频检测响应内容
     *
     * @param checksum      用于检查数据是否被篡改
     * @param contentString 待分析的内容
     * @return true（违规或出现异常）|false
     */
    public static Map<String, Object> mediaAnalyze(String checksum, String contentString) throws NoSuchAlgorithmException {
        Map<String, Object> map = new HashMap<>();
        map.put("result", true);

        //验证数据是否被篡改
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update((aliyunGreenWebConf.getAccountId() + aliyunGreenWebConf.getSeed() + contentString).getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        if (!new BigInteger(1, digest).toString(16).equals(checksum)) {
            log.error("数据被篡改！");
            return null;
        }

        JSONObject content = JSON.parseObject(contentString);
        int requestCode = content.getIntValue("code");
        if (200 == requestCode) {
            JSONArray sceneResults = content.getJSONArray("results");
            for (Object sceneResult : sceneResults) {
                String scene = ((JSONObject) sceneResult).getString("scene");
                String suggestion = ((JSONObject) sceneResult).getString("suggestion");
                log.info("{scene : {}, suggestion : {}}", scene, suggestion);
                if (suggestion.equals("block")) {
                    map.put("url", content.getString("url"));
                    return map;
                }
            }
            map.put("result", false);
            return map;
        }
        log.error("测试请求失败");
        return null;
    }

    /**
     * 分析文字检测响应内容
     *
     * @param scrResponse 待分析的JSONObject
     * @return true|false
     */
    public static boolean textAnalyze(JSONObject scrResponse) {
        String suggestion = null;
        try {
            log.info("srcResponse: {}", JSON.toJSONString(scrResponse, true));
            if (200 == scrResponse.getInteger("code")) {
                JSONObject taskResult = scrResponse.getJSONArray("data").getJSONObject(0);
                if (200 == taskResult.getInteger("code")) {
                    JSONObject sceneResult = taskResult.getJSONArray("results").getJSONObject(0);
                    String scene = sceneResult.getString("scene");
                    suggestion = sceneResult.getString("suggestion");
                    //根据scene和suggetion做相关处理。
                    //suggestion == pass表示未命中垃圾。suggestion == block表示命中了垃圾，可以通过label字段查看命中的垃圾分类。
                    log.info("{scene : {}, suggestion : {}}", scene, suggestion);
                } else {
                    log.info("task process fail:" + ((JSONObject) taskResult).getInteger("code"));
                }

            } else {
                log.info("detect not success. code:" + scrResponse.getInteger("code"));
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }

        assert suggestion != null;
        return suggestion.equals("block");
    }
}

