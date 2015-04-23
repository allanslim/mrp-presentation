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
public class RegistrationPageController extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = "/registrationSuccess")
    public String registrationSuccess(HttpServletRequest request, Model model) {
        createDefaultSignInOutLink(model);


        model.addAttribute("showPortal", false);


        return "registrationSuccess";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/registration")
    public String registrationForm(InsuranceForm insuranceForm, Model model) {
        createDefaultSignInOutLink(model);
        return "registration";
    }



    @RequestMapping(method = RequestMethod.POST, value = "/registration")
    public String registerInsuranceForm(HttpServletRequest request, HttpServletResponse response, @Valid InsuranceForm insuranceForm, BindingResult bindingResult, Model model) {

        createDefaultSignInOutLink(model);
        model.addAttribute("showPortal", false);


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

        return "registration";
    }


    @RequestMapping(method = RequestMethod.GET, value = "/registrationEmail")
    public String registrationEmailAccount(InsuranceForm insuranceForm, Model model) {
        createDefaultSignInOutLink(model);
        model.addAttribute("showPortal", false);

        return "registrationEmail";
    }


    @RequestMapping(method = RequestMethod.POST, value = "/registrationEmail")
    public String validateEmailAccount(HttpServletRequest request, HttpServletResponse response, @Valid InsuranceForm insuranceForm, BindingResult bindingResult, Model model) {

        createDefaultSignInOutLink(model);
        model.addAttribute("showPortal", false);


        if (!insuranceForm.isEmailAndPasswordSupplied()) {
            model.addAttribute("errorMessage", "All fields are required.");
            return "registrationEmail";
        }

        Either<MrxError, Boolean> isEmailAvailable = userService.isEmailAvailable(insuranceForm);

        if (isEmailAvailable.isRight()) {

            return "registrationFinal";
        }

        MrxError mrxError = isEmailAvailable.left();
        model.addAttribute("errorMessage", mrxError.getErrorMessage());


        return "registrationEmail";
    }


    @RequestMapping(method = RequestMethod.GET, value = "/registrationFinal")
    public String registrationFinal(InsuranceForm insuranceForm, Model model) {

        createDefaultSignInOutLink(model);
        model.addAttribute("showPortal", false);

        return "registrationFinal";
    }


    @RequestMapping(method = RequestMethod.POST, value = "/registrationFinal")
    public String registerUser(HttpServletRequest request, HttpServletResponse response, @Valid InsuranceForm insuranceForm, BindingResult bindingResult, Model model) {

        createDefaultSignInOutLink(model);
        model.addAttribute("showPortal", false);


        if (!insuranceForm.areQuestionsAndAnswersSupplied()) {
            model.addAttribute("errorMessage", "All fields are required.");
            return "registrationFinal";
        }

        Either<MrxError, Boolean> isEmailAvailable = userService.registerUser(insuranceForm);

        if (isEmailAvailable.isRight()) {

            return "registrationSuccess";
        }

        MrxError mrxError = isEmailAvailable.left();
        model.addAttribute("errorMessage", mrxError.getErrorMessage());

        return "registrationFinal";
    }
}
