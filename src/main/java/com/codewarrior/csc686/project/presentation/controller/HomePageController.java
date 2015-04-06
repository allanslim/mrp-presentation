package com.codewarrior.csc686.project.presentation.controller;

import com.codewarrior.csc686.project.presentation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/")
public class HomePageController {

    @Autowired
    private UserService userService;

   @RequestMapping( method = RequestMethod.GET, value = "/")
   public String index(Model model) {


        model.addAttribute("greeting", "Hello Allan");
       return "index";
   }

    @RequestMapping( method = RequestMethod.POST, value = "/login")
    public String login(HttpServletRequest request, HttpServletResponse response, Model model) {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String token = userService.login(email, password);

        if(token != null) {
            response.addCookie( new Cookie("token", token));
        }


        model.addAttribute("greeting", "Hello Allan");
        return "redirect: home";
    }

    @RequestMapping( method = RequestMethod.GET, value = "/home")
    public String home(HttpServletRequest request, HttpServletResponse response, Model model) {

        Cookie[] cookies = request.getCookies();
        if(cookies != null ) {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    if(token == null) {
                        try {
                            response.sendRedirect("index");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }


        return "redirect:/";
    }

    @RequestMapping( method = RequestMethod.GET, value = "/about")
    public String about(Model model) {


         model.addAttribute("greeting", "Hello Allan");
        return "about";
    }

    @RequestMapping( method = RequestMethod.GET, value = "/contactUs")
    public String contactUs(Model model) {


         model.addAttribute("greeting", "Hello Allan");
        return "contactUs";
    }
}
