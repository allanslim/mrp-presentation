package com.codewarrior.csc686.project.presentation.controller;

import com.codewarrior.csc686.project.presentation.model.LoginCredential;
import com.codewarrior.csc686.project.presentation.service.UserService;
import com.codewarrior.csc686.project.presentation.util.Either;
import com.codewarrior.csc686.project.presentation.util.MrxError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class HomePageController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public String index(LoginCredential loginCredential) { return "index"; }



    @RequestMapping(method = RequestMethod.POST, value = "/")
    public String login(HttpServletRequest request, HttpServletResponse response, @Valid LoginCredential loginCredential, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "index";
        }

        Either<MrxError, String> eitherToken = userService.login(loginCredential.getEmail(), loginCredential.getPassword());

        if (eitherToken.isRight()) {

            response.addCookie(new Cookie("token", eitherToken.right()));
            return "home";
        }

        MrxError mrxError = eitherToken.left();
        model.addAttribute("errorMessage", mrxError.getErrorMessage());

        return "index";
    }




    @RequestMapping(method = RequestMethod.GET, value = "/home")
    public String home(HttpServletRequest request, HttpServletResponse response, Model model) {

        if (isTokenValid(request, response)) {
            return "home";
        }

        return "redirect:/";
    }


    @RequestMapping(method = RequestMethod.GET, value = "/about")
    public String about(HttpServletRequest request, HttpServletResponse response, Model model) {


        model.addAttribute("greeting", "Hello Allan");

        if (isTokenValid(request, response)) {
            return "about";
        }

        return "redirect:/";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/contactUs")
    public String contactUs(HttpServletRequest request, HttpServletResponse response, Model model) {

        model.addAttribute("greeting", "Hello Allan");

        if (isTokenValid(request, response)) {
            return "contactUs";
        }

        return "redirect:/";
    }

    private boolean isTokenValid(HttpServletRequest request, HttpServletResponse response) {

        Optional<String> optionalToken = extractToken(request, response);

        return optionalToken.isPresent() ? userService.validateToken(optionalToken.get()) : false;
    }

    private Optional<String> extractToken(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }
}
