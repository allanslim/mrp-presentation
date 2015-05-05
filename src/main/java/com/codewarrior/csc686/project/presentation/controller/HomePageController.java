package com.codewarrior.csc686.project.presentation.controller;

import com.codewarrior.csc686.project.presentation.model.*;
import com.codewarrior.csc686.project.presentation.service.UserService;
import com.codewarrior.csc686.project.presentation.util.Either;
import com.codewarrior.csc686.project.presentation.util.MrxError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class HomePageController extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public String index(LoginCredential loginCredential, Model model) {
        createDefaultSignInOutLink(model);
        model.addAttribute("showPortal", false);
        return "index";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/signOut")
    public String logout( HttpServletRequest request, Model model) {

        createDefaultSignInOutLink(model);
        model.addAttribute("showPortal", false);
        Either<MrxError, Boolean> logout = userService.logout(extractToken(request).get());

        if(logout.isRight()) {
            removeTokenFromCookie(request);
        }

        return "redirect:/";
    }


    @RequestMapping(method = RequestMethod.POST, value = "/")
    public String login(HttpServletRequest request, HttpServletResponse response, @Valid LoginCredential loginCredential, BindingResult bindingResult, Model model) {

        createDefaultSignInOutLink(model);
        model.addAttribute("showPortal", false);

        if (bindingResult.hasErrors()) {
            return "index";
        }

        Either<MrxError, String> eitherToken = userService.login(loginCredential.getEmail(), loginCredential.getPassword());

        if (eitherToken.isRight()) {
            createSignInOutLink(model, "Sign Out", "/signOut");
            response.addCookie(new Cookie("token", eitherToken.right()));
            return "redirect:/";
        }

        MrxError mrxError = eitherToken.left();
        model.addAttribute("errorMessage", mrxError.getErrorMessage());

        return "index";
    }




    @RequestMapping(method = RequestMethod.GET, value = "/")
    public String home(HttpServletRequest request,  Model model) {
        createDefaultSignInOutLink(model);
        model.addAttribute("showPortal", false);

        if (isTokenValid(request)) {

            Optional<String> optionalToken = extractToken(request);

            Either<MrxError, Map<String, String>> annualBenefits = userService.retrieveAnnualBenefits(optionalToken.get());

            if(annualBenefits.isRight()) {
                Map<String, String> annualBenefitsMap = annualBenefits.right();
                model.addAllAttributes(annualBenefitsMap);
                model.addAttribute("welcomeMessage",  "Welcome " + annualBenefitsMap.get("firstName") + " " + annualBenefitsMap.get("lastName"));
            }

            createSignInOutLink(model, "Sign Out", "/signOut");
            model.addAttribute("showPortal", true);
        }

        return "home";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/about")
    public String about(HttpServletRequest request,  Model model) {
        createDefaultSignInOutLink(model);

        if (isTokenValid(request)) {
            createSignInOutLink(model, "Sign Out", "/signOut");
            model.addAttribute("showPortal", true);
        }

        return "about";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/prescriptionHistory")
    @ResponseBody
    public List<PrescriptionHistory> retrievePrescriptionHistory( @RequestParam(value = "token") String token,
                                                                  @RequestParam(value = "mrbId") String mrbId,
                                                                  @RequestParam(value = "period") int period) {

        Either<MrxError, List<PrescriptionHistory>> mrxErrorListEither = userService.retrievePrescriptionHistory(token, mrbId, period);

        if(mrxErrorListEither.isRight()) {
            return mrxErrorListEither.right();
        }
        return new ArrayList<>();
    }

    @RequestMapping( method = RequestMethod.GET, value = "/findPharmacy")
    @ResponseBody
    public List<Pharmacy> findPharmacy(  @RequestParam(value = "token") String token,
                                         @RequestParam(value = "zipcode") String zipcode,
                                         @RequestParam(value = "radius") int radius) {


        Either<MrxError, List<Pharmacy>> pharmacies = userService.retrievePharmacies(token, zipcode, radius);

        if(pharmacies.isRight()) {
            return pharmacies.right();
        }

        return new ArrayList<>();
    }


    @RequestMapping(method = RequestMethod.GET, value = "/portal")
    public String portal(HttpServletRequest request, Model model){
        createDefaultSignInOutLink(model);
        model.addAttribute("showPortal", false);


        if (isTokenValid(request)) {
            createSignInOutLink(model, "Sign Out", "/signOut");
            model.addAttribute("showPortal", true);

            Optional<String> optionalToken = extractToken(request);

            if(optionalToken.isPresent()) {

                model.addAttribute("token", optionalToken.get());


                Either<MrxError, Map<String, String>> welcomeSummary = userService.retrieveWelcomeSummary(optionalToken.get());
                Either<MrxError, Map<String, String>> annualBenefits = userService.retrieveAnnualBenefits(optionalToken.get());

                Either<MrxError, List<Dependent>> dependents = userService.retrieveDependents(optionalToken.get());

                if(welcomeSummary.isRight()) {
                    Map<String, String> welcomeSummaryMap = welcomeSummary.right();
                    model.addAllAttributes(welcomeSummaryMap);
                }

                if(annualBenefits.isRight()) {
                    Map<String, String> annualBenefitsMap = annualBenefits.right();
                    model.addAllAttributes(annualBenefitsMap);
                    model.addAttribute("fullName", annualBenefitsMap.get("firstName") + " " + annualBenefitsMap.get("lastName"));
                }

                if(dependents.isRight()) {
                    model.addAttribute("dependents", dependents.right());
                }

            }

            return "portal";
        }

        return "redirect:/login";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/contactUs")
    public String contactUs( HttpServletRequest request, Model model) {

        createDefaultSignInOutLink(model);
        model.addAttribute("showPortal", false);

        if (isTokenValid(request)) {
            createSignInOutLink(model, "Sign Out", "/signOut");
            model.addAttribute("showPortal", true);
        }


        return "contactUs";
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
