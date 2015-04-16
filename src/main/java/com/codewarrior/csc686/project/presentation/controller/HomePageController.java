package com.codewarrior.csc686.project.presentation.controller;

import com.codewarrior.csc686.project.presentation.model.LoginCredential;
import com.codewarrior.csc686.project.presentation.model.SignInOutLink;
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
public class HomePageController extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public String index(LoginCredential loginCredential, Model model) {
        createDefaultSignInOutLink(model);
        return "index";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/signOut")
    public String logout( HttpServletRequest request, Model model) {

        createDefaultSignInOutLink(model);

        Either<MrxError, Boolean> logout = userService.logout(extractToken(request).get());

        if(logout.isRight()) {
            removeTokenFromCookie(request);
        }

        return "redirect:/";
    }


    @RequestMapping(method = RequestMethod.POST, value = "/")
    public String login(HttpServletRequest request, HttpServletResponse response, @Valid LoginCredential loginCredential, BindingResult bindingResult, Model model) {

        createDefaultSignInOutLink(model);

        if (bindingResult.hasErrors()) {
            return "index";
        }

        Either<MrxError, String> eitherToken = userService.login(loginCredential.getEmail(), loginCredential.getPassword());

        if (eitherToken.isRight()) {
            createSignInOutLink(model, "Sign Out", "/signOut");
            response.addCookie(new Cookie("token", eitherToken.right()));
            return "home";
        }

        MrxError mrxError = eitherToken.left();
        model.addAttribute("errorMessage", mrxError.getErrorMessage());

        return "index";
    }




    @RequestMapping(method = RequestMethod.GET, value = "/home")
    public String home(HttpServletRequest request,  Model model) {
        createDefaultSignInOutLink(model);

        if (isTokenValid(request)) {
            createSignInOutLink(model, "Sign Out", "/signOut");

            return "home";
        }

        return "redirect:/";
    }



    @RequestMapping(method = RequestMethod.GET, value = "/about")
    public String about(HttpServletRequest request,  Model model) {
        createDefaultSignInOutLink(model);

        model.addAttribute("greeting", "Hello Allan");

        if (isTokenValid(request)) {
            createSignInOutLink(model, "Sign Out", "/signOut");
            return "about";
        }

        return "redirect:/";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/contactUs")
    public String contactUs(HttpServletRequest request,  Model model) {

        createDefaultSignInOutLink(model);

        if (isTokenValid(request)) {
            createSignInOutLink(model, "Sign Out", "/signOut");
            return "contactUs";
        }

        return "redirect:/";
    }

    private boolean isTokenValid(HttpServletRequest request) {

        Optional<String> optionalToken = extractToken(request);

        return optionalToken.isPresent() ? userService.validateToken(optionalToken.get()) : false;
    }

    private Optional<String> extractToken(HttpServletRequest request) {

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


    private void removeTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                }
            }
        }
    }
}
