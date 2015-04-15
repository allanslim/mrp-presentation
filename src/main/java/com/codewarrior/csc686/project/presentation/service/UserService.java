package com.codewarrior.csc686.project.presentation.service;

import com.codewarrior.csc686.project.presentation.client.MrxClient;
import com.codewarrior.csc686.project.presentation.model.InsuranceForm;
import com.codewarrior.csc686.project.presentation.util.Either;
import com.codewarrior.csc686.project.presentation.util.MrxError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    @Autowired
    private MrxClient mrxClient;

    public Either<MrxError, String> login(String email, String password) { return  mrxClient.login(email, password); }

    public boolean validateToken(String token) { return mrxClient.getToken(token) == null? false : true;  }

    public Either<MrxError, Boolean> isMemberInsuranceInTheSystem(InsuranceForm insuranceForm) {

        return mrxClient.isMemberInsuranceInTheSystem(insuranceForm);
    }

    public Either<MrxError, Boolean> isEmailAvailable(InsuranceForm insuranceForm) {

        return mrxClient.isEmailAvailable(insuranceForm);
    }

    public Either<MrxError, Boolean> registerUser(InsuranceForm insuranceForm) {

        return mrxClient.registerUser(insuranceForm);
    }
}
