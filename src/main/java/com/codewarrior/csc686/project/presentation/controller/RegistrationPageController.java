package com.codewarrior.csc686.project.presentation.controller;


import com.codewarrior.csc686.project.presentation.model.InsuranceForm;
import com.codewarrior.csc686.project.presentation.service.UserService;
import com.codewarrior.csc686.project.presentation.util.Either;
import com.codewarrior.csc686.project.presentation.util.MrxError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
public class RegistrationPageController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = "/registration")
    public String registrationForm(InsuranceForm insuranceForm) {


        return "registration";
    }


    @RequestMapping(method = RequestMethod.POST, value = "/registration")
    public String registerInsuranceForm(HttpServletRequest request, HttpServletResponse response, @Valid InsuranceForm insuranceForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "All fields are required.");
            return "registration";
        }

        Either<MrxError, Boolean> isInsuranceInTheSystem = userService.isMemberInsuranceInTheSystem(insuranceForm);

        if (isInsuranceInTheSystem.isRight()) {

            return "registrationEmail";
        }

        MrxError mrxError = isInsuranceInTheSystem.left();
        model.addAttribute("errorMessage", mrxError.getErrorMessage());

        return "registrationEmail";
    }


    @RequestMapping(method = RequestMethod.GET, value = "/registrationEmail")
    public String registrationEmailAccount(InsuranceForm insuranceForm) {


        return "registrationEmail";
    }


    @RequestMapping(method = RequestMethod.GET, value = "/registrationFinal")
    public String registrationFinal(InsuranceForm insuranceForm) {


        return "registrationFinal";
    }
}