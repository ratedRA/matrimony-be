package com.matrimony.identity.controller;

import com.matrimony.common.ResponseBuilder;
import com.matrimony.identity.data.FriendRequestType;
import com.matrimony.identity.facade.FriendRequestFacade;
import com.matrimony.identity.model.FriendRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class FriendServiceController {

    @Autowired
    private ResponseBuilder responseBuilder;

    @Autowired
    private FriendRequestFacade friendRequestFacade;

    @ApiOperation(
            value = "call to SEND/ACCEPT/IGNORE friend request based on type sent in path variable, also ignore type removes friend request from table",
            response = FriendRequest.class)
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 201,
                            message = "friend request sent/updated",
                            response = FriendRequest.class)
            })
    @PostMapping("/1/user/friendRequest/{type}")
    @ResponseBody
    public ResponseEntity<FriendRequest> processFriendRequest(@PathVariable FriendRequestType type, @RequestBody FriendRequest friendRequest){
        FriendRequest processFriendRequest = friendRequestFacade.processFriendRequest(type, friendRequest);
        return new ResponseEntity(responseBuilder.returnSuccess(processFriendRequest), HttpStatus.CREATED);
    }
}
