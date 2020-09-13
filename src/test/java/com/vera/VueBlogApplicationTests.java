package com.vera;

import com.vera.controller.AccountController;
import com.vera.util.JwtUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class VueBlogApplicationTests {
    @Autowired
    AccountController accountController;

    @Test
    void contextLoads() {
    }

}
