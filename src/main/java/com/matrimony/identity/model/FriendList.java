package com.matrimony.identity.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(value = "friend_list")
public class FriendList {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;
    private List<String> friendUserIds;

    public FriendList(String userId, List<String> friendUserIds) {
        this.userId = userId;
        this.friendUserIds = friendUserIds;
    }

    public FriendList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getFriendUserIds() {
        return friendUserIds;
    }

    public void setFriendUserIds(List<String> friendUserIds) {
        this.friendUserIds = friendUserIds;
    }
}

