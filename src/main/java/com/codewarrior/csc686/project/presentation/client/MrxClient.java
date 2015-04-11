package com.codewarrior.csc686.project.presentation.client;

import com.codewarrior.csc686.project.presentation.util.Either;
import com.codewarrior.csc686.project.presentation.util.MrxError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
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

    private HttpEntity<String> createHttpEntity() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<MediaType> acceptTypes = new ArrayList<MediaType>();
        acceptTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(acceptTypes);

        return new HttpEntity<String>(headers);
    }


}
