package top.chenzhimeng.fu_community.util;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 加密相关的类
 */
public class EncryptUtil {

    private EncryptUtil() {

    }

    /**
     * 根据原密码产生随机盐加密，返回{"盐","密文"}。
     *
     * @param password 未加密的密码
     * @return {"盐","密文"} (Base64编码)
     */
    public static String[] encrypt(String password) throws NoSuchAlgorithmException, InvalidKeyException {
        String[] keyAndSecret = new String[2];

        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacMD5");
        SecretKey secretKey = keyGenerator.generateKey();

        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(secretKey);
        mac.update(password.getBytes(StandardCharsets.UTF_8));

        keyAndSecret[0] = Base64.getEncoder().withoutPadding().encodeToString(secretKey.getEncoded());
        keyAndSecret[1] = Base64.getEncoder().withoutPadding().encodeToString(mac.doFinal());
        return keyAndSecret;
    }

    /**
     * 加盐加密，返回密文
     * @param salt  Base64编码的盐
     * @param password  未加密的密码密码
     * @return  密文（Base64编码）
     */
    public static String encrypt(String salt, String password) throws NoSuchAlgorithmException, InvalidKeyException {

        SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(salt), "HmacMD5");

        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(secretKey);
        mac.update(password.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().withoutPadding().encodeToString(mac.doFinal());
    }

}
