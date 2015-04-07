package com.codewarrior.csc686.project.presentation.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MrxClient extends BaseClient {


    private static final Logger LOG = LoggerFactory.getLogger(MrxClient.class);

    //curl -i "http://localhost:9595/mrxuser/token/O5HVOAEXG2K32CAKMNMHMXOOYEQOG3TM3FAQLMTD45G0TLZ7IF"
    public String getToken( String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<MediaType> acceptTypes = new ArrayList<MediaType>();
        acceptTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(acceptTypes);

        HttpEntity<String> entity = new HttpEntity<String>(headers);

        RestTemplate restTemplate = createRestTemplate();

        ResponseEntity<Map> responseEntity = restTemplate.exchange( middleTierHost + "/mrxuser/token/O5HVOAEXG2K32CAKMNMHMXOOYEQOG3TM3FAQLMTD45G0TLZ7IF", HttpMethod.GET, entity, Map.class);

        return (String) responseEntity.getBody().get("token");

    }
}
