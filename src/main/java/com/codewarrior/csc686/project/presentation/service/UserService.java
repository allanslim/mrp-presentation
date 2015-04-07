package com.codewarrior.csc686.project.presentation.service;

import com.codewarrior.csc686.project.presentation.client.MrxClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    @Autowired
    private MrxClient mrxClient;

    public String login(String email, String password) {

        String token = mrxClient.getToken("abc");

        return "abc123";
    }
}
