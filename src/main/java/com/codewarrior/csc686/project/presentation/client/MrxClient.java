package com.codewarrior.csc686.project.presentation.client;

import com.codewarrior.csc686.project.presentation.model.InsuranceForm;
import com.codewarrior.csc686.project.presentation.model.RegisterUserInput;
import com.codewarrior.csc686.project.presentation.util.Either;
import com.codewarrior.csc686.project.presentation.util.MrxError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MrxClient extends BaseClient {


    private static final Logger LOG = LoggerFactory.getLogger(MrxClient.class);




    //curl -i "http://localhost:9595/mrxuser/token/O5HVOAEXG2K32CAKMNMHMXOOYEQOG3TM3FAQLMTD45G0TLZ7IF"
    public String getToken( String token) {

        HttpEntity<String> entity = createHttpEntity();

        RestTemplate restTemplate = createRestTemplate();

        ResponseEntity<Map> responseEntity = restTemplate.exchange( middleTierHost + "/mrxuser/token/" + token, HttpMethod.GET, entity, Map.class);

        return (String) responseEntity.getBody().get("token");

    }

    // curl -i -XPOST "http://ec2-54-145-194-211.compute-1.amazonaws.com/user/login?email=chong@lee.com&password=abc123"
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


        RegisterUserInput registerUserInput = createRegisterUserInput(insuranceForm);

        HttpEntity<RegisterUserInput> entity = createHttpEntityWithBody(registerUserInput);

        RestTemplate restTemplate = createRestTemplate();


        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(middleTierHost + "/user/validateMember", HttpMethod.POST, entity, Map.class);

            Map responseBody = responseEntity.getBody();

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return Either.right( Boolean.valueOf(responseBody.get("isMemberValid").toString()));
            }
        } catch (HttpClientErrorException e) {
            LOG.error(e.getResponseBodyAsString(), e);

            return Either.left(new MrxError(e.getStatusCode().toString(), "No match found for the member/group/plan data provided."));
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
        return registerUserInput;
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
