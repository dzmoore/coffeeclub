package com.coffeecoffeecoffeeclub.coffeeclub.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedDrink;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedUser;
import com.coffeecoffeecoffeeclub.coffeeclub.service.LoginService;
import com.coffeecoffeecoffeeclub.coffeeclub.service.LoginService.AuthData;
import com.coffeecoffeecoffeeclub.coffeeclub.service.LoginService.AuthState;
import com.coffeecoffeecoffeeclub.coffeeclub.util.Constants;

@Controller
public class LoginController {
    private LoginService loginService;
    private Logger log4jLogger;
    
    @Autowired
    public LoginController(LoginService loginService) {
        log4jLogger = Logger.getLogger(getClass());
        this.loginService = loginService;
    }
    
    @RequestMapping(value="/")
    public String index(HttpSession session) {
        String to = "index";
        if (session.getAttribute(Constants.USER) != null) {
            to = "main";
        }
        return to;
    }
    
    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String login(HttpSession session) {
        return "login";
    }
    
    @RequestMapping(value="/login", method = RequestMethod.POST)
    public String login(
            @RequestParam("username") String username, 
            @RequestParam("password") String password,
            HttpSession session)
    {
        final AuthData authData = loginService.authUser(username, password);
        String to = "login";
        
        if (authData.getState() == AuthState.SUCCESS) {
            session.setAttribute(Constants.USER, authData.getUser());
            
            Object returnPath = session.getAttribute(Constants.RETURN_PATH);
            if (returnPath != null) {
                to = returnPath.toString();
                
            } else {
                to = "redirect:main";
            }
        }
        
        return to;
    }
}
