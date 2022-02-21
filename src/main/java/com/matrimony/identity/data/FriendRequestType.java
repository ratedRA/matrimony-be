package com.matrimony.identity.data;

public enum FriendRequestType {
    SEND, // send request and set status to pending
    ACCEPT, // accepted status
    IGNORE; // remove from friendRequest table
}
