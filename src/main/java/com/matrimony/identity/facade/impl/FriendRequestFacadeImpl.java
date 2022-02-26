package com.matrimony.identity.facade.impl;

import com.matrimony.identity.data.FriendRequestStatus;
import com.matrimony.identity.data.FriendRequestType;
import com.matrimony.identity.facade.FriendRequestFacade;
import com.matrimony.identity.model.FriendList;
import com.matrimony.identity.model.FriendRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class FriendRequestFacadeImpl  implements FriendRequestFacade {

    @Autowired
    private MongoTemplate mongoTemplate;

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

    private void addNewFriend(String primaryUserId, String newFriendUserId) {
        Query requestingUserFriendListQuery = new Query();
        requestingUserFriendListQuery.addCriteria(Criteria.where("userId").is(primaryUserId));
        FriendList requestingUserFriendList = mongoTemplate.findOne(requestingUserFriendListQuery, FriendList.class);

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
}
