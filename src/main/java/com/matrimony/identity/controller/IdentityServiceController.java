package com.matrimony.identity.controller;

import com.matrimony.common.ResponseBuilder;
import com.matrimony.identity.data.UserRegistrationRequest;
import com.matrimony.identity.facade.IdentityServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IdentityServiceController {

    @Autowired
    private ResponseBuilder responseBuilder;

    @Autowired private IdentityServiceFacade identityServiceFacade;

    @PostMapping("/1/user/registration")
    @ResponseBody
    public ResponseEntity<String> register(@RequestBody UserRegistrationRequest registrationRequest){
        identityServiceFacade.register(registrationRequest);
        return new ResponseEntity(responseBuilder.returnSuccess("user successfully created"), HttpStatus.CREATED);
    }

    @GetMapping("/1/user/verifyOtp/{userId}")
    @ResponseBody
    public ResponseEntity<String> verifyOtp(@PathVariable String userId, @RequestParam String otp){
        identityServiceFacade.verifyOtp(userId, otp);
        return new ResponseEntity(responseBuilder.returnSuccess("successful"), HttpStatus.ACCEPTED);
    }
}
