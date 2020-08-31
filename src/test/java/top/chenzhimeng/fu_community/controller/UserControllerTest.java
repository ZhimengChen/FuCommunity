package top.chenzhimeng.fu_community.controller;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import top.chenzhimeng.fu_community.model.User;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Test
    void testIdentityAuth() {
        System.out.println("gif".matches("^(?i)JPG|BPM|PNG|TIFF|RAW|MP4|AVI|rmvb|flv|MOV|MB|swf|ANI|GIF|fla$"));
    }
}