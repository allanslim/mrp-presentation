package com.codewarrior.csc686.project.presentation.controller;

import com.codewarrior.csc686.project.presentation.model.SignInOutLink;
import org.springframework.ui.Model;

public class BaseController {

    protected void createSignInOutLink(Model model, String label, String link) {
        SignInOutLink signInOutLink = new SignInOutLink();
        signInOutLink.label = label;
        signInOutLink.link = link;
        model.addAttribute("signInOutLink", signInOutLink);
    }

    protected void createDefaultSignInOutLink(Model model) {
        createSignInOutLink(model, "Sign In", "/");
    }
}
