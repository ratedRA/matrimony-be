package com.matrimony.identity.model;

import com.matrimony.identity.data.FriendRequestStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("friend_request")
public class FriendRequest {

    @Id
    private String id;

    @Indexed
    private String fromUserId;

    @Indexed
    private String toUserId;

    @Indexed
    private FriendRequestStatus status;

    public FriendRequest() {
    }

    public FriendRequest(String fromUserId, String toUserId) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }

    public FriendRequest(String fromUserId, String toUserId, FriendRequestStatus status) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }
}
