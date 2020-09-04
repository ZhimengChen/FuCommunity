package top.chenzhimeng.fu_community.util;

import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EncryptUtilTest {

    @Test
    void encrypt() throws InvalidKeyException, NoSuchAlgorithmException {
        System.out.println(Arrays.toString(EncryptUtil.encrypt("4cc9dc6d919742d6aff9477c12296eb1")));
    }
}