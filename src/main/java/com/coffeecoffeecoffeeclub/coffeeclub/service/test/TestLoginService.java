package com.coffeecoffeecoffeeclub.coffeeclub.service.test;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedUser;
import com.coffeecoffeecoffeeclub.coffeeclub.service.LoginService;
import com.coffeecoffeecoffeeclub.coffeeclub.service.LoginService.AuthData;
import com.coffeecoffeecoffeeclub.coffeeclub.service.LoginService.AuthState;
import com.coffeecoffeecoffeeclub.coffeeclub.util.Conca;

public class TestLoginService {
    private LoginService loginService;
    private Logger log4jLogger;
    
    @Before
    public void setup() {
        System.setProperty("log4j.configuration", "file:./src/main/resources/log4j.properties");
        
        log4jLogger = Logger.getLogger(getClass());
        log4jLogger.setLevel(Level.INFO);
        
        ApplicationContext ctx = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/login-service.xml");
        loginService = (LoginService) ctx.getBean("loginService");
    }
    
    @Ignore
    @Test
    public void createTestUser() {
        loginService.createUser("test", "test");
    }
    
    @Test
    public void createAuthModify() {
        final String username = Conca.t("test", System.nanoTime());;
        final String password = "test";
        final String changePasswordTo = "test1";

        log4jLogger.info(Conca.t("attempting to create new user [", username, "] with password [", password, "]"));
        
        final MappedUser newUser = loginService.createUser(username, password);
        
        log4jLogger.info(Conca.t("Created user [", newUser, "]"));
        
        TestCase.assertNotNull(newUser);
        TestCase.assertTrue(newUser.getId() != -1);
        TestCase.assertTrue(StringUtils.equalsIgnoreCase(username, newUser.getUsername()));
        TestCase.assertTrue(newUser.isActive());
        
        log4jLogger.info("User appears to be created successfully!");
        
        log4jLogger.info("Attempting to authenticate user");
        
        AuthData authData = loginService.authUser(username, password);
        
        log4jLogger.info(Conca.t("AuthData returned from auth attempt[", authData, "]"));
        
        TestCase.assertNotNull(authData);
        TestCase.assertTrue(authData.getState() == AuthState.SUCCESS);
        TestCase.assertTrue(authData.getUser().getId() == newUser.getId());
        
        log4jLogger.info("User appears to have been authenticated successfully!");
      
        log4jLogger.info(Conca.t("Attempting to change password to [", changePasswordTo, "]"));
        
        final boolean changePwSuccess = loginService.changePassword(username, password, changePasswordTo);
        
        log4jLogger.info(Conca.t("Password change result [", changePwSuccess, "]"));
        
        TestCase.assertTrue(changePwSuccess);
        
        log4jLogger.info("Attempting to authenticate user with new password");
        
        authData = loginService.authUser(username, changePasswordTo);
        
        log4jLogger.info(Conca.t("AuthData returned from auth attempt[", authData, "]"));
        
        TestCase.assertNotNull(authData);
        TestCase.assertTrue(authData.getState() == AuthState.SUCCESS);
        TestCase.assertTrue(authData.getUser().getId() == newUser.getId());
        
        log4jLogger.info("User appears to have been authenticated successfully with new password!");
    }
}
