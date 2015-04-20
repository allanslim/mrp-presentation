package com.codewarrior.csc686.project.presentation.client;

import com.codewarrior.csc686.project.presentation.model.Dependent;
import com.codewarrior.csc686.project.presentation.model.InsuranceForm;
import com.codewarrior.csc686.project.presentation.model.PrescriptionHistory;
import com.codewarrior.csc686.project.presentation.model.RegisterUserInput;
import com.codewarrior.csc686.project.presentation.util.Either;
import com.codewarrior.csc686.project.presentation.util.MrxError;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MrxClient extends BaseClient {


    private static final Logger LOG = LoggerFactory.getLogger(MrxClient.class);




    //curl -i "http://localhost:9595/mrxuser/token/1WACSNQD9O51MVBAV7XNOFM4AGLOOVM24S5GW1D80W2VHYA2FW"
    public String getToken( String token) {

        HttpEntity<String> entity = createHttpEntity();

        RestTemplate restTemplate = createRestTemplate();

        ResponseEntity<Map> responseEntity = restTemplate.exchange( middleTierHost + "/mrxuser/token/" + token, HttpMethod.GET, entity, Map.class);

        return (String) responseEntity.getBody().get("token");

    }


    public Either<MrxError, Boolean> logout( String token ) {

        HttpEntity<String> entity = createHttpEntity();

        RestTemplate restTemplate = createRestTemplate();

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(middleTierHost + "/mrxuser/logout/" + token, HttpMethod.GET, entity, Map.class);

            Map<String, String> responseBody = responseEntity.getBody();

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return Either.right( Boolean.valueOf(responseBody.get("isLogOutSuccessful")));
            }


        } catch (HttpClientErrorException e) {
            LOG.error(e.getResponseBodyAsString(), e);
            return Either.left(new MrxError(e.getStatusCode().toString(), "Invalid Credentials"));

        }

        return Either.left(new MrxError("Generic Error", "Generic Error"));

    }


    // curl -i -XPOST "http://localhost:9595/user/login?email=chong@lee.com&password=abc123"
    public Either<MrxError, String> login(String email, String password) {

        HttpEntity<String> entity = createHttpEntity();

        RestTemplate restTemplate = createRestTemplate();

        ResponseEntity<Map> responseEntity = null;

        try {
            responseEntity = restTemplate.exchange(middleTierHost + "/user/login?email=" + email + "&password=" + password, HttpMethod.POST, entity, Map.class);

            Map responseBody = responseEntity.getBody();

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return Either.right((String) responseBody.get("token"));
            }


        } catch (HttpClientErrorException e) {
            LOG.error(e.getResponseBodyAsString(), e);

            return Either.left(new MrxError(e.getStatusCode().toString(), "Invalid Credentials"));

        }

        return Either.left(new MrxError("Generic Error", "Generic Error"));
    }


//   curl -i -H "content-type:application/json" -XPOST "http://localhost:9595/user/validateMember"
//            -d '{"groupId":1,"insuranceId":"2222224","firstName":"CHENG","lastName":"LEE","birthday":"1966-04-22","email":"allanslim@gmail.com","password":"abc123","seqQ1":"answer1","seqA1":"this is a test","seqQ2":"answer2","seqA2":"this is a test2","seqQ3":"answer3","seqA3":"this is a test3"}'
    public Either<MrxError, Boolean> isMemberInsuranceInTheSystem(InsuranceForm insuranceForm) {

        return validateAccount("/user/validateMember", "isMemberValid", "No match found for the member/group/plan data provided.", insuranceForm);

    }


//    curl -i -H "content-type:application/json" -XPOST "http://localhost:9595/user/isEmailAvailable"
//            -d '{"groupId":1,"insuranceId":"3333333","firstName":"ALLAN","lastName":"LIM","birthday":"1980-06-12","email":"cahujap@gmail.com","password":"abc123","seqQ1":"answer1","seqA1":"this is a test","seqQ2":"answer2","seqA2":"this is a test2","seqQ3":"answer3","seqA3":"this is a test3"}'

    public Either<MrxError,Boolean> isEmailAvailable(InsuranceForm insuranceForm) {

        return validateAccount("/user/isEmailAvailable", "isEmailAvailable","Email account already exist in the system.", insuranceForm);

    }


    public Either<MrxError,Boolean> registerUser(InsuranceForm insuranceForm) {
        return validateAccount("/user/registration", "isRegistrationSuccessful", "Email account already exist in the system.", insuranceForm);
    }


    public Either<MrxError, Map<String, String>> retrieveAnnualBenefits(String token) {

        return retrieveDataFromRest(middleTierHost + "/mrxuser/annualBenefits/" + token);
    }

    public Either<MrxError, Map<String, String>> retrieveWelcomeSummary(String token) {

        return retrieveDataFromRest(middleTierHost + "/mrxuser/memberInfo/" + token );
    }


    // mrxuser/prescriptionHistory/PF9H76WF3J8J26LJ4VZN2SB36LIQNOYJ3BFZID0WHU1C0Z0K92/mrbId/34/period/3
    public Either<MrxError, List<PrescriptionHistory>> retrievePrescriptionHistory(String token, String mrbId, int period) {

        RestTemplate restTemplate = createRestTemplate();

         HttpEntity<String> entity = createHttpEntity();

        String url = middleTierHost + "/mrxuser/prescriptionHistory/" + token + "/mrbId/" + mrbId + "/period/" + period;

         try {
             ResponseEntity<List> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

             List<PrescriptionHistory> responseBody = responseEntity.getBody();

             if (responseEntity.getStatusCode() == HttpStatus.OK) {
                 return Either.right(responseBody);
             }

         } catch (HttpClientErrorException e) {
             LOG.error(e.getResponseBodyAsString(), e);
             return Either.left(new MrxError(e.getStatusCode().toString(), "Invalid Credentials"));

         }

         return Either.left(new MrxError("Generic Error", "Generic Error"));
    }

    public Either<MrxError, List<Dependent>> retrieveDependents(String token) {

        RestTemplate restTemplate = createRestTemplate();

        HttpEntity<String> entity = createHttpEntity();

        try {
            ResponseEntity<List> responseEntity = restTemplate.exchange(middleTierHost + "/mrxuser/memberDependents/" + token, HttpMethod.GET, entity, List.class);

            List<Dependent> responseBody = responseEntity.getBody();

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return Either.right(responseBody);
            }

        } catch (HttpClientErrorException e) {
            LOG.error(e.getResponseBodyAsString(), e);
            return Either.left(new MrxError(e.getStatusCode().toString(), "Invalid Credentials"));

        }

        return Either.left(new MrxError("Generic Error", "Generic Error"));
    }


    public Either<MrxError, Map<String, String>> retrieveDataFromRest(String url) {

        RestTemplate restTemplate = createRestTemplate();

        HttpEntity<String> entity = createHttpEntity();

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            Map<String, String> responseBody = responseEntity.getBody();

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return Either.right(responseBody);
            }

        } catch (HttpClientErrorException e) {
            LOG.error(e.getResponseBodyAsString(), e);
            return Either.left(new MrxError(e.getStatusCode().toString(), "Invalid Credentials"));

        }

        return Either.left(new MrxError("Generic Error", "Generic Error"));
    }



    private Either<MrxError, Boolean> validateAccount(String url, String labelName, String errorMessage, InsuranceForm insuranceForm) {

        RegisterUserInput registerUserInput = createRegisterUserInput(insuranceForm);

        HttpEntity<RegisterUserInput> entity = createHttpEntityWithBody(registerUserInput);

        RestTemplate restTemplate = createRestTemplate();

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(middleTierHost + url, HttpMethod.POST, entity, Map.class);

            Map responseBody = responseEntity.getBody();

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return Either.right( Boolean.valueOf(responseBody.get(labelName).toString()));
            }
        } catch (HttpClientErrorException e) {
            LOG.error(e.getResponseBodyAsString(), e);

            Map<String, String> map = convertToHashMap(e.getResponseBodyAsString());

            return Either.left(new MrxError(e.getStatusCode().toString(), map.get("errorMessage")));
        }

        return Either.left(new MrxError("Generic Error", "Generic Error"));
    }




    private RegisterUserInput createRegisterUserInput(InsuranceForm insuranceForm) {SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        RegisterUserInput registerUserInput = new RegisterUserInput();

        registerUserInput.birthday = sdf.format(insuranceForm.getBirthDate());

        registerUserInput.groupId = insuranceForm.getGroupId();

        registerUserInput.insuranceId = insuranceForm.getMemberId();

        registerUserInput.firstName = insuranceForm.getFirstname();

        registerUserInput.lastName = insuranceForm.getLastname();

        registerUserInput.email = insuranceForm.getEmail();

        registerUserInput.password = insuranceForm.getPassword();

        registerUserInput.seqQ1 = insuranceForm.getQuestion1();
        registerUserInput.seqQ2 = insuranceForm.getQuestion2();
        registerUserInput.seqQ3 = insuranceForm.getQuestion3();

        registerUserInput.seqA1 = insuranceForm.getAnswer1();
        registerUserInput.seqA2 = insuranceForm.getAnswer2();
        registerUserInput.seqA3 = insuranceForm.getAnswer3();

        return registerUserInput;
    }

    private Map<String,String> convertToHashMap(String json)  {

        Map<String, String> map = new HashMap<>();
        map.put("errorMessage", "Error Occured");

        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(json, Map.class);
        } catch (IOException e) {
            LOG.error("error converting json string to Map", e);

        }

        return map;

    }


    private  HttpEntity<RegisterUserInput> createHttpEntityWithBody(RegisterUserInput registerUserInput) {

        HttpHeaders headers = createHttpheaders();

        return new HttpEntity<>(registerUserInput, headers);

    }

    private HttpEntity<String> createHttpEntity() {

        HttpHeaders headers = createHttpheaders();

        return new HttpEntity<>(headers);
    }

    private HttpHeaders createHttpheaders() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<MediaType> acceptTypes = new ArrayList<MediaType>();
        acceptTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(acceptTypes);
        return headers;
    }


}
