package com.codewarrior.csc686.project.presentation.service;

import com.codewarrior.csc686.project.presentation.client.MrxClient;
import com.codewarrior.csc686.project.presentation.model.*;
import com.codewarrior.csc686.project.presentation.util.Either;
import com.codewarrior.csc686.project.presentation.util.MrxError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {


    @Autowired
    private MrxClient mrxClient;

    public Either<MrxError, String> login(String email, String password) { return  mrxClient.login(email, password); }


    public boolean validateToken(String token) { return StringUtils.isBlank(mrxClient.getToken(token))? false : true;  }


    public Either<MrxError, Boolean> isMemberInsuranceInTheSystem(InsuranceForm insuranceForm) {

        return mrxClient.isMemberInsuranceInTheSystem(insuranceForm);
    }


    public Either<MrxError, Boolean> isEmailAvailable(InsuranceForm insuranceForm) {

        return mrxClient.isEmailAvailable(insuranceForm);
    }


    public Either<MrxError, Boolean> registerUser(InsuranceForm insuranceForm) {

        return mrxClient.registerUser(insuranceForm);
    }

    public Either<MrxError, Boolean> logout(String token) {

        return mrxClient.logout(token);

    }

    public Either<MrxError,Map<String,String>> retrieveWelcomeSummary(String token) {
        return mrxClient.retrieveWelcomeSummary(token);
    }

    public Either<MrxError,Map<String,String>> retrieveAnnualBenefits(String token) {
        return mrxClient.retrieveAnnualBenefits(token);
    }

    public Either<MrxError,List<Dependent>> retrieveDependents(String token) {
        return mrxClient.retrieveDependents(token);
    }

    public Either<MrxError, List<PrescriptionHistory>> retrievePrescriptionHistory(String token, String mrbId, int period) {
        return mrxClient.retrievePrescriptionHistory(token, mrbId, period);
    }

    public Either<MrxError, List<Pharmacy>> retrievePharmacies(String token, String zipcode, int radius) {

        return mrxClient.retrievePharmacies(token, zipcode, radius);
    }

    public Either<MrxError, List<String>> retrieveDrugs(String token, String drug) {

        return mrxClient.retrieveDrugs(token, drug);
    }

    public Either<MrxError, List<DrugDetail>> findDrugDetails(String token, String drugDescription) {

        return mrxClient.retrieveDrugDetails(token, drugDescription);
    }

    public Either<MrxError, DrugPrice> findDrugPrice(String token, String drugNdc, String pharmacyId) {

        return mrxClient.retrieveDrugPrice(token, drugNdc, pharmacyId);
    }

    public Either<MrxError, Map<String, CopayDetail>> retrieveCopayDetails(String token) {

        return mrxClient.retrieveCopayDetails(token);
    }
}
