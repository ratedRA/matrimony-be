package com.matrimony.identity.facade.impl;

import com.matrimony.common.orika.OrikaBoundMapperFacade;
import com.matrimony.identity.data.FriendRequestStatus;
import com.matrimony.identity.data.FriendRequestType;
import com.matrimony.identity.data.UserPublicProfile;
import com.matrimony.identity.facade.FriendFacade;
import com.matrimony.identity.facade.IdentityServiceFacade;
import com.matrimony.identity.model.FriendList;
import com.matrimony.identity.model.FriendRequest;
import com.matrimony.identity.model.MatrimonyUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Component
public class FriendFacadeImpl implements FriendFacade {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdentityServiceFacade identityServiceFacade;

    private static final OrikaBoundMapperFacade<MatrimonyUser, UserPublicProfile> USER_MAPPER = new OrikaBoundMapperFacade<>(MatrimonyUser.class, UserPublicProfile.class);


    @Override
    public FriendRequest processFriendRequest(FriendRequestType type, FriendRequest friendRequest) {

        FriendRequest savedRequest = null;
        Query query = new Query();
        query.addCriteria(Criteria.where("fromUserId").is(friendRequest.getFromUserId()).and("toUserId").is(friendRequest.getToUserId()));

        FriendRequest existingRequest = mongoTemplate.findOne(query, FriendRequest.class);

        Query reverseQuery = new Query();
        reverseQuery.addCriteria(Criteria.where("fromUserId").is(friendRequest.getToUserId()).and("toUserId").is(friendRequest.getFromUserId()));

        FriendRequest reverseExistingRequest = mongoTemplate.findOne(reverseQuery, FriendRequest.class);

        switch (type){
            case SEND:
                if(existingRequest != null && existingRequest.getStatus() != null && existingRequest.getStatus().equals(FriendRequestStatus.ACCEPTED)){
                    throw new RuntimeException("Already friends");
                }

                if(existingRequest != null){
                    throw new RuntimeException("Request already sent before");
                }

                if(reverseExistingRequest != null){
                    throw new RuntimeException("Other user already sent the friend request");
                }
                savedRequest = mongoTemplate.save(new FriendRequest(friendRequest.getFromUserId(), friendRequest.getToUserId(), FriendRequestStatus.PENDING));
                break;

            case ACCEPT:
                if(existingRequest == null){
                    throw new RuntimeException("Request doesn't exist to accept");
                }
                if(existingRequest.getStatus() != null && existingRequest.getStatus().equals(FriendRequestStatus.ACCEPTED)){
                    throw new RuntimeException("Already friends");
                }

                existingRequest.setStatus(FriendRequestStatus.ACCEPTED);
                savedRequest = mongoTemplate.save(existingRequest);

                // update friendList table
                addNewFriend(friendRequest.getFromUserId(), friendRequest.getToUserId());

                addNewFriend(friendRequest.getToUserId(), friendRequest.getFromUserId());

                break;

            case IGNORE:
                if(existingRequest != null && existingRequest.getId() == null){
                    throw new RuntimeException("Request doesn't exist to accept");
                }
                if(existingRequest != null && existingRequest.getStatus() != null && !existingRequest.getStatus().equals(FriendRequestStatus.PENDING)){
                    throw new RuntimeException("Only Pending requests can be ignored");
                }

                mongoTemplate.remove(existingRequest);
                existingRequest.setStatus(FriendRequestStatus.IGNORED);
                savedRequest = existingRequest;
                break;

        }
        return savedRequest;
    }

    @Override
    public UserPublicProfile publicProfile(String requestingUserId, String requestedUserId) {
        Assert.isTrue(StringUtils.isNotBlank(requestingUserId) && StringUtils.isNotBlank(requestedUserId), "userIds missing");

        MatrimonyUser requestedUser = identityServiceFacade.loadUserById(requestedUserId);

        Assert.notNull(requestedUser, "user not found");

        UserPublicProfile publicProfile = USER_MAPPER.writeToD(requestedUser);

        FriendList friendList = getFriendList(requestingUserId);
        if(friendList.isFriend(requestedUserId)){
            publicProfile.setFriends(Boolean.TRUE);
        }

        if(!publicProfile.isFriends()){
            publicProfile.setPhoneNumber(null);
            publicProfile.setCountryCode(null);
            publicProfile.setInstagramId(null);
            publicProfile.setFacebookId(null);
            publicProfile.setSnapchatId(null);
        }

        return publicProfile;
    }

    private void addNewFriend(String primaryUserId, String newFriendUserId) {
        FriendList requestingUserFriendList = getFriendList(primaryUserId);

        List<String> fromUserList = new ArrayList<>();
        if (requestingUserFriendList == null) {
            fromUserList.add(newFriendUserId);
            mongoTemplate.save(new FriendList(primaryUserId, fromUserList));
        } else {
            List<String> friendUserIds = requestingUserFriendList.getFriendUserIds();
            friendUserIds.add(newFriendUserId);
            requestingUserFriendList.setFriendUserIds(friendUserIds);
            mongoTemplate.save(requestingUserFriendList);
        }
    }

    private FriendList getFriendList(String primaryUserId) {
        Query requestingUserFriendListQuery = new Query();
        requestingUserFriendListQuery.addCriteria(Criteria.where("userId").is(primaryUserId));
        return mongoTemplate.findOne(requestingUserFriendListQuery, FriendList.class);
    }
}
