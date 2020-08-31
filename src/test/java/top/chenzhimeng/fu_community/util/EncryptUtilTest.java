package top.chenzhimeng.fu_community.util;

import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EncryptUtilTest {

    @Test
    void encrypt() throws InvalidKeyException, NoSuchAlgorithmException {
        System.out.println(Arrays.toString(EncryptUtil.encrypt("698d51a19d8a121ce581499d7b701668")));
    }
}