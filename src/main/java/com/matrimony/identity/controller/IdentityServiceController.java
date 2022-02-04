package com.matrimony.identity.controller;

import com.matrimony.common.ResponseBuilder;
import com.matrimony.identity.data.LoginRequest;
import com.matrimony.identity.data.UserRegistrationRequest;
import com.matrimony.identity.facade.IdentityServiceFacade;
import com.matrimony.identity.model.MatrimonyUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @PostMapping("/1/user/register")
    @ResponseBody
    public ResponseEntity<MatrimonyUser> register(@RequestBody UserRegistrationRequest registrationRequest){
        MatrimonyUser user = identityServiceFacade.register(registrationRequest);
        return new ResponseEntity(responseBuilder.returnSuccess(user), HttpStatus.CREATED);
    }

    @PostMapping("/1/user/login")
    @ResponseBody
    public ResponseEntity<MatrimonyUser> login(@RequestBody LoginRequest loginRequest){
        MatrimonyUser loggedInUser = identityServiceFacade.login(loginRequest);
        return new ResponseEntity(responseBuilder.returnSuccess(loggedInUser), HttpStatus.ACCEPTED);
    }

    @ApiOperation(
            value = "verify otp, create password, returns auth token",
            response = String.class)
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 202,
                            message = "auth token to be used in Authorization header",
                            response = String.class)
            })
    @GetMapping("/pub/1/user/verifyOtp/{userId}")
    @ResponseBody
    public ResponseEntity<String> verifyOtp(@PathVariable String userId, @RequestParam String otp, @RequestParam String password){
        String token = identityServiceFacade.verifyOtp(userId, otp, password);
        return new ResponseEntity(responseBuilder.returnSuccess(token), HttpStatus.ACCEPTED);
    }

    @GetMapping("/pub/hello")
    @ResponseBody
    public ResponseEntity<String> hello(){
        return new ResponseEntity(responseBuilder.returnSuccess("hello"), HttpStatus.ACCEPTED);
    }

    @ApiOperation(
            value = "returns authenticated user based on auth token, auth token to be sent in Authorization header with Bearer prefix",
            response = String.class)
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 202,
                            message = "authenticated user",
                            response = MatrimonyUser.class)
            })
    @GetMapping("/1/user/authenticated")
    public ResponseEntity<MatrimonyUser> authenticatedUser() {
        MatrimonyUser authenticatedUser = identityServiceFacade.getAuthenticatedUser();
        return new ResponseEntity(responseBuilder.returnSuccess(authenticatedUser), HttpStatus.CREATED);
    }
}
