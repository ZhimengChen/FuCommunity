package top.chenzhimeng.fu_community.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Header;
import com.auth0.jwt.interfaces.JWTVerifier;
import top.chenzhimeng.fu_community.config.TokenConf;

import java.util.*;

public class TokenUtil {

    private TokenUtil() {

    }

    private static final TokenConf TOKEN_CONF = SpringUtil.getBean(TokenConf.class);

    private static final Map<String, Object> HEADER = new HashMap<>();

    static {
        HEADER.put("typ", "JWT");
        HEADER.put("alg", "HS256");
    }

    private static final JWTVerifier VERIFIER = JWT.require(Algorithm.HMAC256(TOKEN_CONF.getSecretKey())).build();

    /**
     * 获取access和refresh tokens
     *
     * @param userId       用户id
     * @param tokenVersion token版本
     * @return {"accessToken"（包含用户id和token版本信息）,"refreshToken"（包含accessToken信息）}
     */
    public static String[] signTokens(Integer userId, Long tokenVersion) {
        String[] accessAndRefreshToken = new String[2];
        accessAndRefreshToken[0] = JWT.create()
                .withHeader(HEADER)
                .withClaim("userId", userId)
                .withClaim("tokenVersion", tokenVersion)
                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_CONF.getValidityPeriod().getAccess()))
                .sign(Algorithm.HMAC256(TOKEN_CONF.getSecretKey()));
        accessAndRefreshToken[1] = JWT.create()
                .withHeader(HEADER)
                .withClaim("userId", userId)
                .withClaim("tokenVersion", tokenVersion)
                .withClaim("accessToken", accessAndRefreshToken[0])
                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_CONF.getValidityPeriod().getRefresh()))
                .sign(Algorithm.HMAC256(TOKEN_CONF.getSecretKey()));
        return accessAndRefreshToken;
    }

    /**
     * 获取管理员token
     *
     * @param adminId      管理员id
     * @param adminName    管理员用户名
     * @param tokenVersion token版本
     * @return accessToken
     */
    public static String signToken(Integer adminId, String adminName, Long tokenVersion) {
        return JWT.create()
                .withHeader(HEADER)
                .withClaim("adminId", adminId)
                .withClaim("adminName", adminName)
                .withClaim("tokenVersion", tokenVersion)
                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_CONF.getValidityPeriod().getAccess()))
                .sign(Algorithm.HMAC256(TOKEN_CONF.getSecretKey()));
    }

    /**
     * 验证accessToken
     *
     * @param accessToken token
     * @return {用户id,token版本}
     */
    public static Object[] verifyAccessToken(String accessToken) {
        DecodedJWT decodedJWT;
        try {
            decodedJWT = VERIFIER.verify(accessToken);
        } catch (Exception e) {
            return null;
        }
        return new Object[]{decodedJWT.getClaim("userId").asInt(), decodedJWT.getClaim("tokenVersion").asLong()};
    }

    /**
     * 验证管理员accessToken
     *
     * @param adminToken 管理员token
     * @return {userId,"userName",tokenVersion}
     */
    public static Object[] verifyAdminToken(String adminToken) {
        DecodedJWT decodedJWT;
        try {
            decodedJWT = VERIFIER.verify(adminToken);
        } catch (Exception e) {
            return null;
        }
        return new Object[]{decodedJWT.getClaim("adminId").asInt(), decodedJWT.getClaim("adminName").asString(),
                decodedJWT.getClaim("tokenVersion").asLong()};
    }

    /**
     * 验证refreshToken
     *
     * @param refreshToken token
     * @return {用户id,token版本,"accessToken"}
     */
    public static Object[] verifyRefreshToken(String refreshToken) {
        DecodedJWT decodedJWT;
        try {
            decodedJWT = VERIFIER.verify(refreshToken);
        } catch (Exception e) {
            return null;
        }
        return new Object[]{
                decodedJWT.getClaim("userId").asInt(),
                decodedJWT.getClaim("tokenVersion").asLong(),
                decodedJWT.getClaim("accessToken").asString()
        };
    }

}
