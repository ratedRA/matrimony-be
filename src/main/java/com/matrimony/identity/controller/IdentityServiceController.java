package com.matrimony.identity.controller;

import com.matrimony.common.ResponseBuilder;
import com.matrimony.common.orika.OrikaBoundMapperFacade;
import com.matrimony.identity.data.LoginRequest;
import com.matrimony.identity.data.PasswordCreateRequest;
import com.matrimony.identity.data.User;
import com.matrimony.identity.data.UserFilter;
import com.matrimony.identity.data.UserRegistrationRequest;
import com.matrimony.identity.facade.IdentityServiceFacade;
import com.matrimony.identity.model.MatrimonyUser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class IdentityServiceController {

    @Autowired
    private ResponseBuilder responseBuilder;

    @Autowired private IdentityServiceFacade identityServiceFacade;

    private static final OrikaBoundMapperFacade<MatrimonyUser, User> USER_MAPPER = new OrikaBoundMapperFacade<>(MatrimonyUser.class, User.class);

    @PostMapping("/1/user/register")
    @ResponseBody
    public ResponseEntity<User> register(@RequestBody UserRegistrationRequest registrationRequest){
        MatrimonyUser user = identityServiceFacade.register(registrationRequest);
        User responseUser = USER_MAPPER.writeToD(user);

        return new ResponseEntity(responseBuilder.returnSuccess(responseUser), HttpStatus.CREATED);
    }

    @PostMapping("/1/user/login")
    @ResponseBody
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest){
        MatrimonyUser loggedInUser = identityServiceFacade.login(loginRequest);
        User responseUser = USER_MAPPER.writeToD(loggedInUser);

        return new ResponseEntity(responseBuilder.returnSuccess(responseUser), HttpStatus.ACCEPTED);
    }

    @ApiOperation(
            value = "verify otp, create password, returns auth token",
            response = String.class)
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 202,
                            message = "auth token returned to be used in Authorization header",
                            response = String.class,
                            examples = @Example(@ExampleProperty(value = "auth_token")))
            })
    @PostMapping("/pub/1/user/verifyOtp")
    @ResponseBody
    public ResponseEntity<String> verifyOtp(@RequestBody PasswordCreateRequest passwordCreateRequest){
        String token = identityServiceFacade.verifyOtp(passwordCreateRequest.getUserId(), passwordCreateRequest.getOtp(), passwordCreateRequest.getPassword());
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
    public ResponseEntity<User> authenticatedUser() {
        MatrimonyUser authenticatedUser = identityServiceFacade.getAuthenticatedUser();
        User user = USER_MAPPER.writeToD(authenticatedUser);
        return new ResponseEntity(responseBuilder.returnSuccess(user), HttpStatus.OK);
    }

    @ApiOperation(
            value = "updates user profile fields, doesn't update phoneno or authToken",
            response = String.class)
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "user",
                            response = User.class)
            })
    @PutMapping("/1/user/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody User requestUser){
        MatrimonyUser matrimonyUser = USER_MAPPER.writeToS(requestUser);
        matrimonyUser.setId(userId);
        MatrimonyUser updatedUser = identityServiceFacade.update(matrimonyUser);
        User responseUser = USER_MAPPER.writeToD(updatedUser);
        return new ResponseEntity(responseBuilder.returnSuccess(responseUser), HttpStatus.ACCEPTED);
    }

    @ApiOperation(
            value = "search users based on filters, \n tags and socialIds should be sent in searchTerm field \n It is pageable, by default pageStart is 0 and pageSize is 10",
            response = List.class)
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "list of matched users",
                            response = List.class)
            })
    @GetMapping("/1/user/search")
    public ResponseEntity<List<User>> search(UserFilter userFilter){
        List<MatrimonyUser> matrimonyUsers = identityServiceFacade.search(userFilter);
        List<User> matchedUsers = matrimonyUsers.stream().map(matrimonyUser -> USER_MAPPER.writeToD(matrimonyUser)).collect(Collectors.toList());
        return new ResponseEntity(responseBuilder.returnSuccess(matchedUsers), HttpStatus.ACCEPTED);
    }
}
